package com.gdlgxy.internshipcommunity.network.networktask;

import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.youth.banner.util.LogUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zuoyong
 */
public class RequestQueue {
    private static final String TAG = "RequestQueue";

    //网络连接的线程数目
    private static final int NETWORK_THREAD_CORE_POOL_SIZE = 5; //最小5个线程
    private static final int NETWORK_THREAD_MAX_POOL_SIZE = 5; //最大5个线程。
    private static final int NETWORK_THREAD_KEEP_ALIVE_TIME = 10; // 10 seconds

    //分发线程数目，处理速度很快，不用上网的直接处理掉，要上网的转到网络线程池处理。
    private static final int DISPATCH_THREAD_CORE_POOL_SIZE = 4; //最小4个线程
    private static final int DISPATCH_THREAD_MAX_POOL_SIZE = 6; //最大6个线程。
    private static final int DISPATCH_THREAD_KEEP_ALIVE_TIME = 10; // 10 seconds

    //任务结束的原因。
    private static final int TASK_FINISH_BY_DISPATCH = 1;
    private static final int TASK_FINISH_BY_NETWORK = 2;
    private static final int TASK_FINISH_BY_CANCEL = 3;

    //dispatcher task msg
    private static final int TASK_ADD_MSG = 1000;
    private static final int TASK_REMOVE_MSG = 1001;


    private final ThreadPoolExecutor mDispatcherExecutor;
    private final ThreadPoolExecutor mNetworkExecutor;
    private CacheManager mCacheManager;
    private final Map<String, Queue<RequestTask>> mWaitingRequests =
            new HashMap<String, Queue<RequestTask>>();
    private AtomicLong mRequestSequence = new AtomicLong();
    private Handler mDispatcherTask;

    private RequestQueue() {
        mDispatcherExecutor = new PriorityThreadPoolExecutor(
                DISPATCH_THREAD_CORE_POOL_SIZE,
                DISPATCH_THREAD_MAX_POOL_SIZE,
                DISPATCH_THREAD_KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new OrderedBlockingQueue<Runnable>(),
                new PriorityThreadFactory("dispath-thrd",
                        android.os.Process.THREAD_PRIORITY_BACKGROUND));
        mNetworkExecutor = new PriorityThreadPoolExecutor(
                NETWORK_THREAD_CORE_POOL_SIZE,
                NETWORK_THREAD_MAX_POOL_SIZE,
                NETWORK_THREAD_KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new OrderedBlockingQueue<Runnable>(),
                new PriorityThreadFactory("net-thrd",
                        android.os.Process.THREAD_PRIORITY_BACKGROUND));
        mDispatcherTask = new DispatcherTaskHandler(Looper.myLooper());
        mCacheManager = CacheDataManager.getInstance();
    }

    public static RequestQueue getInstance() {
        return RequestQueueHolder.sInstance;
    }

    /**
     * 请求的任务将会先进入分发队列，如果不用上网，将在分发线程里处理掉这个任务；
     * 如果需要上网，则转到网络队列继续处理。
     * 这样设计的目的，是让网络状态不好时，正在网络处理的任务，不阻塞掉不需要上网的任务。
     *
     * @param task
     */
    public void addRequest(RequestTask task) {
        Message msg = mDispatcherTask.obtainMessage();
        msg.what = TASK_ADD_MSG;
        msg.obj = task;
        mDispatcherTask.sendMessage(msg);
    }

    public void cancelRequest(RequestTask task) {
        Message msg = mDispatcherTask.obtainMessage();
        msg.what = TASK_REMOVE_MSG;
        msg.obj = task;
        mDispatcherTask.sendMessage(msg);
    }

    private void cancelTask(RequestTask task) {
        if (task == null) {
            return;
        }
        task.cancel();
        if (task.getDispatchTag() != null) {
            mDispatcherExecutor.remove(task.getDispatchTag());
        }
        if (task.getNetworkTag() != null) {
            mNetworkExecutor.remove(task.getNetworkTag());
        }
        if (!task.isStarted()) {
            onFinishRequest(task, TASK_FINISH_BY_CANCEL);
        }
    }

    public void clearWaitingTask() {
        mDispatcherExecutor.getQueue().clear();
        mNetworkExecutor.getQueue().clear();
        mWaitingRequests.clear();
    }

    public CacheManager getCacheManager() {
        return mCacheManager;
    }

    private void enqueueDispatchInternal(RequestTask task) {
        if (null == task) {
            return;
        }
        Runnable dispatchRun = new DispatchRunnable(task);
        task.dispatchQueued(mCacheManager, dispatchRun, mRequestSequence.getAndIncrement());
        mDispatcherExecutor.submit(dispatchRun);
    }

    private void enqueueNetworkInternal(RequestTask task) {
        Runnable networkRun = new NetworkRunnable(task);
        task.networkQueued(mCacheManager, networkRun);
        mNetworkExecutor.submit(networkRun);
    }

    private boolean enqueueWaitingInternal(RequestTask task) {
        synchronized (mWaitingRequests) {
            String cacheKey = task.getCacheKey();
            if (cacheKey != null && mWaitingRequests.containsKey(cacheKey)) {
                // There is already a request in flight. Queue up.
                Queue<RequestTask> stagedRequests = mWaitingRequests.get(cacheKey);
                if (stagedRequests == null) {
                    stagedRequests = new LinkedList<RequestTask>();
                }
                stagedRequests.add(task);
                mWaitingRequests.put(cacheKey, stagedRequests);
                return true;
            }
        }
        return false;
    }

    private void enqueueWaitingOrNetworkInternal(RequestTask task) {
        synchronized (mWaitingRequests) {
            if (!enqueueWaitingInternal(task)) {
                mWaitingRequests.put(task.getCacheKey(), null);
                enqueueNetworkInternal(task);
            }
        }
    }

    private void onFinishRequest(RequestTask task, int resion) {
        if (resion != TASK_FINISH_BY_NETWORK) {
            return;
        }
        synchronized (mWaitingRequests) {
            String cacheKey = task.getCacheKey();
            Queue<RequestTask> waitingRequests = mWaitingRequests.remove(cacheKey);
            if (waitingRequests != null) {
                for (RequestTask waitingTask : waitingRequests) {
                    enqueueDispatchInternal(waitingTask);
                }
            }
        }
    }

    private class DispatcherTaskHandler extends Handler {

        public DispatcherTaskHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TASK_ADD_MSG:
                    enqueueDispatchInternal((RequestTask) msg.obj);
                    break;
                case TASK_REMOVE_MSG:
                    cancelTask((RequestTask) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }

    private static abstract class RequestTaskRunnable implements Runnable {
        protected final RequestTask mTask;

        public RequestTaskRunnable(RequestTask task) {
            mTask = task;
        }
    }

    private class DispatchRunnable extends RequestTaskRunnable {
        public DispatchRunnable(RequestTask task) {
            super(task);
        }

        @Override
        public void run() {
            //如果不需要上网，就不加入网络队列了。
            boolean handled = false;
            try {
                handled = mTask.dispatchRun();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!handled) {
                enqueueWaitingOrNetworkInternal(mTask);
            } else {
                onFinishRequest(mTask, TASK_FINISH_BY_DISPATCH);
            }
        }
    }

    private class NetworkRunnable extends RequestTaskRunnable {
        public NetworkRunnable(RequestTask task) {
            super(task);
        }

        @Override
        public void run() {
            try {
                mTask.networkRun();
            } catch (Exception e) {
                e.printStackTrace();
            }
            onFinishRequest(mTask, TASK_FINISH_BY_NETWORK);
        }
    }

    private static class PriorityThreadPoolExecutor extends ThreadPoolExecutor {
        public PriorityThreadPoolExecutor(int corePoolSize,
                                          int maximumPoolSize,
                                          long keepAliveTime,
                                          TimeUnit unit,
                                          BlockingQueue<Runnable> workQueue,
                                          ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        @Override
        protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
            return new PriorityRunnableFuture<T>(runnable, value);
        }
    }

    private static class PriorityRunnableFuture<T> extends FutureTask<T> implements Comparable<PriorityRunnableFuture<T>> {
        private RequestTaskRunnable mTaskRunnable;

        public PriorityRunnableFuture(Runnable runnable, T result) {
            super(runnable, result);
            mTaskRunnable = (RequestTaskRunnable) runnable;
        }

        @Override
        public int compareTo(PriorityRunnableFuture<T> another) {
            return mTaskRunnable.mTask.compareTo(another.mTaskRunnable.mTask);
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }


    private static class RequestQueueHolder {
        private final static RequestQueue sInstance = new RequestQueue();
    }
}

package com.gdlgxy.internshipcommunity.network.networktask;

import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;
import com.gdlgxy.internshipcommunity.CommunityApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;

public abstract class RequestTask implements Comparable<RequestTask>, MakeUrlInterface {
    public static final String TAG = "RequestTask";
    public static boolean DEBUG = true;

    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    public static final int RUNNING_STATE_INIT = 0;
    public static final int RUNNING_STATE_DISPATCH_QUEUED = 1;
    public static final int RUNNING_STATE_DISPATCH_RUNNING = 2;
    public static final int RUNNING_STATE_DISPATCH_DONE = 3;
    public static final int RUNNING_STATE_NETWORK_QUEUED = 4;
    public static final int RUNNING_STATE_NETWORK_RUNNING = 5;
    public static final int RUNNING_STATE_DONE = 6;

    public static final int METHOD_GET = 1;
    public static final int METHOD_POST = 2;

    public static final int PRIORITY_LOW = -100;
    public static final int PRIORITY_NORMAL = 0;
    public static final int PRIORITY_HIGH = 100;
    public static final int PRIORITY_IMMEDIATELY = 200;

    public static final String DATA = "data";

    private static HashMap<String, String> sPhoneInfoHeader = new HashMap<String, String>();
    private static boolean staticInited;

    private static final int MAX_CONNECTION_COUNT = 8;
    private static int sMallocedConnectionCount;
    private static List<String> NOT_ADD_SN_IMEI_URL_LIST;

    public final int method;
    public final String url;
    public Map<String, String> additionheaders;
    public byte[] body;
    public String bodyContentType = "application/x-www-form-urlencoded; charset=" + DEFAULT_PARAMS_ENCODING;
    //超时10s, 重试20s.
    public int timeoutMs = 10000;
    public int retryIncreaceTimeOutMs = 10000;
    public final String tag;
    public final String language;
    public boolean addPhoneInfoHeader;
    public boolean needProxy;

    private NetworkResponse response;
    private CacheEntry cache;
    private Map<String, String> headers = new HashMap<String, String>();
    private boolean mCanceled = false;
    private int mRunningState;
    protected CacheManager mCacheManager;
    protected long mRequestSequence;
    protected String mCachedKey;
    protected int mPriority;
    private Runnable mDispatchTag;
    private Runnable mNetworkTag;
    protected int mVersionCode;
    protected static Context mAppContext;

    static {
        mAppContext = CommunityApplication.getApplication();
    }

    public RequestTask(String url, int method, String tag, String language) {
        this.method = method;
        this.tag = tag;
        this.language = language;
        mRunningState = RUNNING_STATE_INIT;
        // 这句话调用了一个虚函数，将它放到最后不容易出问题。
        this.url = makeUpUrlWithLanguage(url, language);
        mCachedKey = (method == METHOD_GET) ? url : null;
    }

    void dispatchQueued(CacheManager cacheManager, Runnable dispatchTag, long requestSequence) {
        mCacheManager = cacheManager;
        mDispatchTag = dispatchTag;
        mRequestSequence = requestSequence;
        mRunningState = RUNNING_STATE_DISPATCH_QUEUED;
    }

    void networkQueued(CacheManager cacheManager, Runnable networkTag) {
        mCacheManager = cacheManager;
        mNetworkTag = networkTag;
        mRunningState = RUNNING_STATE_NETWORK_QUEUED;
    }

    public Runnable getDispatchTag() {
        return mDispatchTag;
    }

    public void setNeedProxy(boolean need) {
        needProxy = need;
    }

    public Runnable getNetworkTag() {
        return mNetworkTag;
    }

    public void setPriority(int priority) {
        mPriority = priority;
    }

    public int getPriority() {
        return mPriority;
    }

    public NetworkResponse getResponse() {
        return response;
    }

    public void setCacheKey(String key) {
        mCachedKey = key;
    }

    public String getCacheKey() {
        return mCachedKey;
    }

    public void setAcceptGzip(boolean acceptGzip) {
        if (acceptGzip) {
            headers.put("Accept-Encoding", "gzip");
        } else {
            if ("gzip".equals(headers.get("Accept-Encoding"))) {
                headers.remove("Accept-Encoding");
            }
        }
    }

    @Override
    public int compareTo(RequestTask another) {
        RequestTask lTask = this;
        RequestTask rTask = another;
        return lTask.mPriority == rTask.mPriority
                ? (int) (lTask.mRequestSequence - rTask.mRequestSequence)
                : rTask.mPriority - lTask.mPriority;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public int getRunningState() {
        return mRunningState;
    }

    public boolean isStarted() {
        return mRunningState > RUNNING_STATE_INIT;
    }

    boolean dispatchRun() {
        mRunningState = RUNNING_STATE_DISPATCH_RUNNING;
        if (mCanceled) {
            onCancel();
            mRunningState = RUNNING_STATE_DONE;
            mCacheManager = null;
            return true;
        }

        try {
            //如果有缓存，不需要再次下载了，就不去下载了。
            if (!shouldNetworkDownload()) {
                mRunningState = RUNNING_STATE_DONE;
                mCacheManager = null;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mRunningState = RUNNING_STATE_DISPATCH_DONE;
        return false;
    }

    void networkRun() {
    }

    private void logResponseData(NetworkResponse response) {
        try {
            String data = null;
            String contentType = response != null && response.headers != null
                    ? response.headers.get("Content-Type")
                    : null;
            if (response.data != null) {
                if (contentType != null
                        && (contentType.startsWith("application/json")
                        || contentType.startsWith("text/"))) {
                    data = new String(response.data, "utf-8");
                } else {
                    data = contentType;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void addCacheHeaders(CacheEntry entry) {
        // If there's no cache entry, we're done.
        if (entry == null) {
            return;
        }

        if (entry.etag != null) {
            headers.put("If-None-Match", entry.etag);
        }

    }


    private void addPhoneInfoHeaderIfNeed() {
        if (addPhoneInfoHeader) {
            headers.putAll(sPhoneInfoHeader);
        }
    }

    private void addAdditionHeaderIfNeeded() {
        if (additionheaders != null && additionheaders.size() > 0) {
            headers.putAll(additionheaders);
        }
    }

    protected CacheEntry getCacheEntry() {
        if (mCacheManager != null) {
            cache = mCacheManager.queryCacheEntry(getCacheKey(), tag, language);
        }
        return cache;
    }

    protected void saveCacheEntry(String url, String tag, String language, CacheEntry cache) {
        if (mCacheManager != null) {
            mCacheManager.saveCacheEntry(url, tag, language, cache);
        }
    }

    protected boolean deleteCacheEntry(String url, String tag) {
        if (mCacheManager != null) {
            return mCacheManager.deleteCacheEntry(url, tag);
        }
        return false;
    }

    public String makeUpUrlWithLanguage(String url, String language) {
        return url;
    }

    /*private static boolean isMeizuUrl(String url) {
        if (TextUtils.isEmpty(url) || url.length() <= 5 || !url.startsWith("https")) {
            return false;
        }

        String domainName = BrowserUtils.getDomainName(url);

        if (TextUtils.isEmpty(domainName)) {
            return false;
        }

        boolean result = domainName.equals("bro.flyme.cn")
                || domainName.equals("browser.meizu.com")
                || domainName.equals("bro-res.flyme.cn")
                || domainName.equals("bro-res2.flyme.cn");
        return result;
    }*/

    /**
     * 当前请求的本地缓存是否已过期
     *
     * @param expireTime
     * @return
     */
    protected boolean isExpired(long expireTime) {
        return isExpired(null, expireTime, false);
    }

    /**
     * 当前请求的本地缓存是否已过期
     *
     * @param expireTime: 有效时间段
     * @param isImage:
     * @return
     */
    protected boolean isExpired(CacheEntry cache, long expireTime, boolean isImage) {
        boolean expired = true;
        if (expireTime > 0) {
            if (cache == null) {
                cache = getCacheEntry();
            }
            int versionCode = 0;
            if (cache != null && cache.id > 0) {
                final long localUpdateTime = cache.localUpdateTime;
                versionCode = cache.versionCode;
                expired = (Math.abs(System.currentTimeMillis() - localUpdateTime) >= expireTime);
            }
            if (!isImage) {
                expired = expired || (mVersionCode > versionCode);
            }
        }
        return expired;
    }

    private static Gson sGson = null;

    protected String formatJson(String content) {
        if (!TextUtils.isEmpty(content)) {
            try {
                if (sGson == null) {
                    sGson = new GsonBuilder().setPrettyPrinting().create();
                }
                JsonParser jp = new JsonParser();
                JsonElement je = jp.parse(content);
                return sGson.toJson(je);
            } catch (Exception e) {
                e.printStackTrace();
                return content;
            }
        } else {
            return "";
        }
    }

    /**
     * @param errorCode 定义放在{@link NetworkResponse}
     * @param response
     */
    protected abstract void onError(int errorCode, NetworkResponse response);

    /**
     * @param response
     * @return 是否缓存CacheEntry.
     */
    protected abstract boolean onSuccess(NetworkResponse response);

    protected abstract void onCancel();

    protected boolean shouldNetworkDownload() {
        return true;
    }

    public void cancel() {
        mCanceled = true;
    }

    public boolean isCanceled() {
        return mCanceled;
    }
}

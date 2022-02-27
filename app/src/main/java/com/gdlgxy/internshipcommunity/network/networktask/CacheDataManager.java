package com.gdlgxy.internshipcommunity.network.networktask;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class CacheDataManager implements CacheManager {
    private static final String LOG_TAG = "CacheDataManager";
    @SuppressLint("SdCardPath")
    private static final String DIR_IMAGE = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.android.browser/image";
    private final Map<File, FileInfo> mCacheFiles = Collections.synchronizedMap(new HashMap<File, FileInfo>());
    private final AtomicLong mCacheSize;
    private final int SIZE_LIMIT = 60 * 1024 * 1024; //Total size limited to 60M
    private final int RECYCLE_LIMIT = 2 * 1024 * 1024;
    private final int CACHE_ENTRY_COUNT_LIMIT = 3000;
    private final int CACHE_ENTRY_KEEP_COUNT = 2500;


    private CacheDataManager() {
        File f = new File(DIR_IMAGE);
        if (!f.exists()) {
            f.mkdirs();
        }
        mCacheSize = new AtomicLong();
    }

    public static CacheDataManager getInstance() {
        return CacheDataManagerHolder.sInstance;
    }

    @Override
    public CacheEntry queryCacheEntry(String url, String tag, String language) {
        return null;
    }

    @Override
    public boolean saveCacheEntry(String url, String tag, String language, CacheEntry cache) {
        return false;
    }

    @Override
    public boolean deleteCacheEntry(String url, String tag) {
        return false;
    }

    private boolean saveImage(byte[] data, String url, long id) {
        if (id < 0 || url == null || url.length() <= 0 || data == null) {
            return false;
        }

        boolean save = false;
        String path = makeImageName(url, id);
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            File imageDir = new File(DIR_IMAGE);
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            fos = new FileOutputStream(path);
            fos.write(data);
            save = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return save;
    }

    public static String makeImageName(String netUri, long id) {
        if (id <= 0) {
            return null;
        }
        String extersion = ".png";
        if (netUri != null) {
            int lastDotIndex = netUri.lastIndexOf(".");
            if (lastDotIndex > 0 && netUri.indexOf("/", lastDotIndex) < 0) {
                int endIndex = netUri.length();
                int tmpEndIndex;
                if ((tmpEndIndex = netUri.indexOf("&", lastDotIndex)) > 0) {
                    endIndex = tmpEndIndex;
                } else if ((tmpEndIndex = netUri.indexOf("?", lastDotIndex)) > 0) {
                    endIndex = tmpEndIndex;
                }
                extersion = netUri.substring(lastDotIndex, endIndex);
            }
        }
        return DIR_IMAGE + "/" + id + extersion;
    }

    private void staticImage(String path) {
        if (path == null) {
            return;
        }
        File imageFile = new File(path);
        if (!imageFile.exists()) {
            return;
        }

        updateFileInfo(imageFile, true);
        if (mCacheSize.get() > SIZE_LIMIT) {
            while (mCacheSize.get() > SIZE_LIMIT - RECYCLE_LIMIT) {
                long freedSize = removeMostLongUsedFile();
                if (freedSize == -1) {
                    break;
                }
            }
        }
    }

    private void updateFileInfo(File file, boolean logTotalSize) {
        if (file == null) {
            return;
        }

        if (!file.exists()) {
            FileInfo fileInfo = mCacheFiles.remove(file);
            if (fileInfo != null) {
                mCacheSize.addAndGet(-fileInfo.mFileSize);
            }
            return;
        }

        FileInfo fileInfo = mCacheFiles.get(file);
        if (fileInfo == null) {
            fileInfo = new FileInfo();
            mCacheFiles.put(file, fileInfo);
        }
        long increaceSize = file.length() - fileInfo.mFileSize;
        fileInfo.mFileSize = file.length();
        fileInfo.mLastModified = file.lastModified();
        mCacheSize.addAndGet(increaceSize);
    }

    private long removeMostLongUsedFile() {
        if (mCacheFiles.isEmpty()) {
            return -1;
        }
        Long oldestUsage = null;
        File mostLongUsedFile = null;
        Set<Entry<File, FileInfo>> entries = mCacheFiles.entrySet();
        synchronized (mCacheFiles) {
            for (Entry<File, FileInfo> entry : entries) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                if (mostLongUsedFile == null) {
                    mostLongUsedFile = entry.getKey();
                    oldestUsage = entry.getValue().mLastModified;
                } else {
                    Long lastValueUsage = entry.getValue().mLastModified;
                    if (lastValueUsage < oldestUsage) {
                        oldestUsage = lastValueUsage;
                        mostLongUsedFile = entry.getKey();
                    }
                }
            }
        }

        long fileSize = 0;
        if (mostLongUsedFile != null && mostLongUsedFile.exists()) {
            fileSize = mostLongUsedFile.length();
            mostLongUsedFile.delete();
            updateFileInfo(mostLongUsedFile, true);
        }

        return fileSize;
    }

    private static class FileInfo {
        public long mLastModified;
        public long mFileSize;
    }

    private static class CacheDataManagerHolder {
        private final static CacheDataManager sInstance = new CacheDataManager();
    }
}

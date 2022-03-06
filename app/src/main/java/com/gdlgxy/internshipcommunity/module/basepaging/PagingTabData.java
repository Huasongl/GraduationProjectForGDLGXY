package com.gdlgxy.internshipcommunity.module.basepaging;

import android.text.TextUtils;

import java.io.Serializable;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class PagingTabData extends BaseObservable implements Serializable {

    public static final int TYPE_IMAGE_TEXT = 1;//图文
    public static final int TYPE_VIDEO = 2;//视频
    /**
     * id : 364
     * itemId : 6739143063064549000
     * itemType : 2
     * createTime : 1569079017
     * duration : 299.435
     * feeds_text : 当中国地图出来那一幕，我眼泪都出来了！
     * 太震撼了！
     * authorId : 3223400206308231
     * activityIcon : null
     * activityText : null
     * width : 640
     * height : 368
     * url : https://pipijoke.oss-cn-hangzhou.aliyuncs.com/6739143063064549643.mp4
     * cover :
     */

    public int id;
    public long itemId;
    public int itemType;
    public long createTime;
    public double duration;
    public String feeds_text;
    public long authorId;
    public String activityIcon;
    public String activityText;
    public int width;
    public int height;
    public String url;
    public String cover;

    public User author;
    public Comment topComment;
    public Ugc ugc;

    @Bindable
    public Ugc getUgc() {
        if (ugc == null) {
            ugc = new Ugc();
        }
        return ugc;
    }

    @Bindable
    public User getAuthor() {
        return author;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof PagingTabData))
            return false;
        PagingTabData newTabData = (PagingTabData) obj;
        return id == newTabData.id
                && itemId == newTabData.itemId
                && itemType == newTabData.itemType
                && createTime == newTabData.createTime
                && duration == newTabData.duration
                && TextUtils.equals(feeds_text, newTabData.feeds_text)
                && authorId == newTabData.authorId
                && TextUtils.equals(activityIcon, newTabData.activityIcon)
                && TextUtils.equals(activityText, newTabData.activityText)
                && width == newTabData.width
                && height == newTabData.height
                && TextUtils.equals(url, newTabData.url)
                && TextUtils.equals(cover, newTabData.cover)
                && (author != null && author.equals(newTabData.author))
                && (topComment != null && topComment.equals(newTabData.topComment))
                && (ugc != null && ugc.equals(newTabData.ugc));
    }


}

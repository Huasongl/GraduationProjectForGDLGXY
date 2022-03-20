package com.gdlgxy.internshipcommunity.data;

import android.text.TextUtils;


import com.gdlgxy.internshipcommunity.util.BR;

import java.io.Serializable;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
public class User extends BaseObservable implements Serializable {

    /**
     * id : 962
     * userId : 3223400206308231
     * name : 二师弟请随我来
     * avatar : 头像
     * description : 个人描述
     * likeCount : 0 关注数
     * qqOpenId : null //请求Id
     * 手机号
     * 账号
     * QQ号
     * 动态帖子ID
     * 生日
     * 注册的时间
     * 关注用户的列表，存用户id 1-M //关联用户数据
     * 粉丝用户列表  N-1 //
     * expires_time : 0 // 登录的有效期
     * id islike fanid
     */


    public int id;
    public long userId;
    public String name;
    public String avatar;
    public String description;
    public int likeCount;
    public int topCommentCount;
    public int followCount;
    public int followerCount;
    public String qqOpenId;
    public long expires_time;
    public int score;
    public int historyCount;
    public int commentCount;
    public int favoriteCount;
    public int feedCount;
    public boolean hasFollow;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof User))
            return false;
        User newUser = (User) obj;
        return TextUtils.equals(name, newUser.name)
                && TextUtils.equals(avatar, newUser.avatar)
                && TextUtils.equals(description, newUser.description)
                && likeCount == newUser.likeCount
                && topCommentCount == newUser.topCommentCount
                && followCount == newUser.followCount
                && followerCount == newUser.followerCount
                && qqOpenId == newUser.qqOpenId
                && expires_time == newUser.expires_time
                && score == newUser.score
                && historyCount == newUser.historyCount
                && commentCount == newUser.commentCount
                && favoriteCount == newUser.favoriteCount
                && feedCount == newUser.feedCount
                && hasFollow == newUser.hasFollow;
    }

    @Bindable
    public boolean isHasFollow() {
        return hasFollow;
    }

    public void setHasFollow(boolean hasFollow) {
        this.hasFollow = hasFollow;
        notifyPropertyChanged(BR._all);
    }
}

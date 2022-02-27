package com.gdlgxy.internshipcommunity.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import com.gdlgxy.internshipcommunity.CommunityApplication;
import com.gdlgxy.internshipcommunity.module.home.User;
import com.gdlgxy.internshipcommunity.network.ApiResponse;
import com.gdlgxy.internshipcommunity.network.ApiService;
import com.gdlgxy.internshipcommunity.network.JsonCallback;
import com.gdlgxy.internshipcommunity.network.cache.CacheManager;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UserManager {
    private static final String KEY_CACHE_USER = "cache_user";
    private static UserManager mUserManager = new UserManager();
    private MutableLiveData<User> userLiveData;
    private User mUser;

    public static UserManager get() {
        return mUserManager;
    }

    private UserManager() {
        User cache = (User) CacheManager.getCache(KEY_CACHE_USER);
        if (cache != null) { //&& cache.expires_time > System.currentTimeMillis()
            mUser = cache;
        }
    }

    public void save(User user) {
        mUser = user;
        CacheManager.save(KEY_CACHE_USER, user);
        if (getUserLiveData().hasObservers()) {
            getUserLiveData().postValue(user);
        }
    }

    public LiveData<User> login(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        return getUserLiveData();
    }

    public boolean isLogin() { //解决登录卡死的问题，expires_time时间戳有问题，会一直小于 System.currentTimeMillis();
        return mUser != null ;//? false : mUser.expires_time > System.currentTimeMillis();
    }

    public User getUser() {
        if (isLogin()) {
            return mUser;
        }
        User user = new User();
        user.avatar = "";
        user.description = "************";
        user.name = "未登录";
        return user;
    }

    public long getUserId() {
        return isLogin() ? mUser.userId : 0;
    }


    public LiveData<User> refresh() {
        MutableLiveData<User> liveData = new MutableLiveData<>();
        if (!isLogin()) {
            liveData.postValue(getUser());
            return liveData;
        }
        ApiService.get("/user/query")
                .addParam("userId", getUserId())
                .execute(new JsonCallback<User>() {
                    @Override
                    public void onSuccess(ApiResponse<User> response) {
                        save(response.body);
                        liveData.postValue(getUser());
                    }

                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onError(ApiResponse<User> response) {
                        ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CommunityApplication.getApplication(), response.message, Toast.LENGTH_SHORT).show();
                            }
                        });
                        liveData.postValue(null);
                    }
                });
        return liveData;
    }

    public void logout() {
        CacheManager.delete(KEY_CACHE_USER, mUser);
        mUser = null;
        userLiveData = null;
    }

    private MutableLiveData<User> getUserLiveData() {
        if (userLiveData == null) {
            userLiveData = new MutableLiveData<>();
        }
        return userLiveData;
    }
}

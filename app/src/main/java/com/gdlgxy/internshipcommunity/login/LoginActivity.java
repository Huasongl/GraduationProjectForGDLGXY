package com.gdlgxy.internshipcommunity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.gdlgxy.internshipcommunity.CommunityApplication;
import com.gdlgxy.internshipcommunity.base.BaseActivity;
import com.gdlgxy.internshipcommunity.databinding.ActivityLayoutLoginBinding;
import com.gdlgxy.internshipcommunity.module.basepaging.User;
import com.gdlgxy.internshipcommunity.network.ApiResponse;
import com.gdlgxy.internshipcommunity.network.ApiService;
import com.gdlgxy.internshipcommunity.network.JsonCallback;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

public class LoginActivity extends BaseActivity<ActivityLayoutLoginBinding, ViewModel> implements View.OnClickListener {
    private Tencent mTencent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView.qqLoginButton.setOnClickListener(this);
    }

    @Override
    protected ViewModel createViewModel() {
        return null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mView.qqLoginButton.getId()) {
            login();
        }
    }

    private void login() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance("101993725", CommunityApplication.getApplication());
        }
        mTencent.login(this, "all", mLoginListener);
    }

    IUiListener mLoginListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            JSONObject response = (JSONObject) o;
            try {
                String openid = response.getString("openid");
                String access_token = response.getString("access_token");
                String expires_in = response.getString("expires_in");
                long expires_time = response.getLong("expires_time");
                mTencent.setOpenId(openid);
                mTencent.setAccessToken(access_token, expires_in);
                QQToken qqToken = mTencent.getQQToken();
                getUserInfo(qqToken, expires_time, openid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(getApplicationContext(), "登录失败:reason" + uiError.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "登录取消", Toast.LENGTH_SHORT).show();
        }
    };

    private void getUserInfo(QQToken qqToken, long expires_time, String openid) {
        UserInfo userInfo = new UserInfo(getApplicationContext(), qqToken);
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject response = (JSONObject) o;
                try {
                    String nickname = response.getString("nickname");
                    String figureurl_2 = response.getString("figureurl_2");
                    save(nickname, figureurl_2, openid, expires_time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(getApplicationContext(), "登录失败:reason" + uiError.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "登录取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void save(String nickname, String avatar, String openid, long expires_time) {
        ApiService.get("/user/insert")
                .addParam("name", nickname)
                .addParam("avatar", avatar)
                .addParam("qqOpenId", openid)
                .addParam("expires_time", expires_time)
                .execute(new JsonCallback<User>() {
                    @Override
                    public void onSuccess(ApiResponse<User> response) {
                        if (response.body != null) {
                            UserManager.get().save(response.body);
                            finish();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "登陆失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(ApiResponse<User> response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "登陆失败,msg:" + response.message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mLoginListener);
        }
    }

    @Override
    public String getPageName() {
        return null;
    }

    @Override
    public ActivityLayoutLoginBinding inflate_Activity(LayoutInflater layoutInflater) {
        return ActivityLayoutLoginBinding.inflate(layoutInflater);
    }
}

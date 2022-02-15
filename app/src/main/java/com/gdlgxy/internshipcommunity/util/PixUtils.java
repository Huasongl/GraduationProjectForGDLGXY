package com.gdlgxy.internshipcommunity.util;

import android.util.DisplayMetrics;

import com.gdlgxy.internshipcommunity.CommunityApplication;

public class PixUtils {

    public static int dp2px(int dpValue) {
        DisplayMetrics metrics = CommunityApplication.getApplication().getResources().getDisplayMetrics();
        return (int) (metrics.density * dpValue + 0.5f);
    }

    public static int getScreenWidth() {
        DisplayMetrics metrics = CommunityApplication.getApplication().getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics metrics = CommunityApplication.getApplication().getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }
}

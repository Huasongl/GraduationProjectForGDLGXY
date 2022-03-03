package com.gdlgxy.internshipcommunity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.gdlgxy.internshipcommunity.base.BaseActivity;
import com.gdlgxy.internshipcommunity.login.UserManager;
import com.gdlgxy.internshipcommunity.module.home.User;
import com.gdlgxy.internshipcommunity.module.mainpageconfig.NavGraphBuilder;
import com.gdlgxy.internshipcommunity.module.mainpageconfig.data.Destination;
import com.gdlgxy.internshipcommunity.module.mainpageconfig.view.AppBottomBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.gdlgxy.internshipcommunity.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends BaseActivity<ActivityMainBinding, ViewModel>
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    private NavController mNavController;
    private AppBottomBar mNavView;
    private static MainActivity sInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
        mNavView = mView.navView;
        Fragment fragment = getSupportFragmentManager().findFragmentById(mView.navHostFragmentActivityMain.getId());
        mNavController = NavHostFragment.findNavController(fragment);
        NavGraphBuilder.build(this, mNavController, fragment.getChildFragmentManager(), fragment.getId());
        mNavView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mNavController.navigate(item.getItemId());
        return true;
    }

    @Override
    public void onBackPressed() {
        int currentPageId = mNavController.getCurrentDestination().getId();
        int homeDestId = mNavController.getGraph().getStartDestination();
        if (currentPageId != homeDestId) {
            mNavView.setSelectedItemId(homeDestId);
            return;
        }
        finish();
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            sInstance = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected ViewModel createViewModel() {
        return null;
    }

    public static MainActivity getInstance() {
        return sInstance;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public String getPageName() {
        return null;
    }

    @Override
    public ActivityMainBinding inflate_Activity(LayoutInflater layoutInflater) {
        return ActivityMainBinding.inflate(layoutInflater);
    }
}
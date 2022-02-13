package com.gdlgxy.internshipcommunity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.gdlgxy.internshipcommunity.base.BaseActivity;
import com.gdlgxy.internshipcommunity.base.IViewBinding;
import com.gdlgxy.internshipcommunity.module.mainpageconfig.NavGraphBuilder;
import com.gdlgxy.internshipcommunity.module.mainpageconfig.view.AppBottomBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.gdlgxy.internshipcommunity.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding>
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    private NavController mNavController;
    private AppBottomBar mNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mViewBinding.getRoot());
        mNavView = mViewBinding.navView;
        Fragment fragment = getSupportFragmentManager().findFragmentById(mViewBinding.navHostFragmentActivityMain.getId());
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
        //当前正在显示的页面destinationId
        int currentPageId = mNavController.getCurrentDestination().getId();
        //APP页面路导航结构图  首页的destinationId
        int homeDestId = mNavController.getGraph().getStartDestination();
        //如果当前正在显示的页面不是首页，而我们点击了返回键，则拦截。
        if (currentPageId != homeDestId) {
            mNavView.setSelectedItemId(homeDestId);
            return;
        }
        //否则 finish，此处不宜调用onBackPressed。因为navigation会操作回退栈,切换到之前显示的页面。
        finish();
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
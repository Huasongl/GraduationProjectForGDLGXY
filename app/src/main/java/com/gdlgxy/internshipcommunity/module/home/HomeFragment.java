package com.gdlgxy.internshipcommunity.module.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gdlgxy.internshipcommunity.base.BaseFragment;
import com.gdlgxy.internshipcommunity.constant.PageName;
import com.gdlgxy.internshipcommunity.databinding.FragmentHomeBinding;
import com.gdlgxy.navannotationmodule.FragmentDestination;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

@FragmentDestination(pageUrl = "main/module/home", asStarter = true)
public class HomeFragment extends BaseFragment<FragmentHomeBinding> {
    private PageListPlayDetector playDetector;
    private String feedType;
    private boolean shouldPause = true;
    private HomeViewModel mHomeViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mHomeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mViewBinding.text.setText(s);
            }
        });
        Log.d("fragment-onViewCreated", "HomeFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHomeViewModel = null;
    }

    @Override
    public FragmentHomeBinding inflate_Fragment(LayoutInflater layoutInflater, ViewGroup container, boolean at) {
        return FragmentHomeBinding.inflate(layoutInflater, container, false);
    }

    @Override
    public String getPageName() {
        return PageName.HOME;
    }

    public static HomeFragment newInstance(String feedType) {
        Bundle args = new Bundle();
        args.putString("feedType", feedType);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
}

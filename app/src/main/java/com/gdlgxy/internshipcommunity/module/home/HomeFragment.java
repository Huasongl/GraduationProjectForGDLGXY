package com.gdlgxy.internshipcommunity.module.home;

import android.os.Bundle;
import android.view.View;

import com.gdlgxy.internshipcommunity.base.BaseListFragment;
import com.gdlgxy.navannotationmodule.FragmentDestination;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;

@FragmentDestination(pageUrl = "main/module/home", asStarter = true)
public class HomeFragment extends BaseListFragment<HomeTabData, HomeViewModel> {
    private PageListPlayDetector playDetector;
    private String feedType;
    private boolean shouldPause = true;
    private HomeViewModel mHomeViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getCacheLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<HomeTabData>>() {
            @Override
            public void onChanged(PagedList<HomeTabData> feeds) {
                submitList(feeds);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHomeViewModel = null;
    }

    public static HomeFragment newInstance(String feedType) {
        Bundle args = new Bundle();
        args.putString("feedType", feedType);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public PagedListAdapter getAdapter() {
        return null;
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }
}

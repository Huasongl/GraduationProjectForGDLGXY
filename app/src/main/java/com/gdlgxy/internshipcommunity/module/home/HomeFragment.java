package com.gdlgxy.internshipcommunity.module.home;

import android.os.Bundle;
import android.view.View;

import com.gdlgxy.internshipcommunity.base.BaseListFragment;
import com.gdlgxy.navannotationmodule.FragmentDestination;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;

@FragmentDestination(pageUrl = "main/module/home", asStarter = true)
public class HomeFragment extends BaseListFragment<HomeTabData, HomeViewModel> {
    private PageListPlayDetector playDetector;
    private String mPageType;
    private boolean shouldPause = true;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getCacheLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<HomeTabData>>() {
            @Override
            public void onChanged(PagedList<HomeTabData> feeds) {
                submitList(feeds);
            }
        });
        playDetector = new PageListPlayDetector(this, mRecyclerView);
        mViewModel.setFeedType(mPageType);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static HomeFragment newInstance(String feedType) {
        Bundle args = new Bundle();
        args.putString("pageType", feedType);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public PagedListAdapter getAdapter() {
        mPageType = getArguments() == null ? "all" : getArguments().getString("pageType");
        return new HomePagedListAdapter(getContext(), mPageType) {
            @Override
            public void onViewAttachedToWindow2(@NonNull ViewHolder holder) {
                if (holder.isVideoItem()) {
                    playDetector.addTarget(holder.getListPlayerView());
                }
            }

            @Override
            public void onViewDetachedFromWindow2(@NonNull ViewHolder holder) {
                playDetector.removeTarget(holder.getListPlayerView());
            }

            @Override
            public void onStartFeedDetailActivity(HomeTabData feed) {
                boolean isVideo = feed.itemType == HomeTabData.TYPE_VIDEO;
                shouldPause = !isVideo;
            }

            @Override
            public void onCurrentListChanged(@Nullable PagedList<HomeTabData> previousList, @Nullable PagedList<HomeTabData> currentList) {
                //这个方法是在我们每提交一次 pagelist对象到adapter 就会触发一次
                //每调用一次 adpater.submitlist
                if (previousList != null && currentList != null) {
                    if (!currentList.containsAll(previousList)) {
                        mRecyclerView.scrollToPosition(0);
                    }
                }
            }
        };
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        final PagedList<HomeTabData> currentList = mAdapter.getCurrentList();
        if (currentList == null || currentList.size() <= 0) {
            finishRefresh(false);
            return;
        }
        HomeTabData homeTabData = currentList.get(mAdapter.getItemCount() - 1);
        mViewModel.loadAfter(homeTabData.id, new ItemKeyedDataSource.LoadCallback<HomeTabData>() {
            @Override
            public void onResult(@NonNull List<? extends HomeTabData> data) {
                PagedList.Config config = currentList.getConfig();
                if (data != null && data.size() > 0) {
                    //这里 咱们手动接管 分页数据加载的时候 使用MutableItemKeyedDataSource也是可以的。
                    //由于当且仅当 paging不再帮我们分页的时候，我们才会接管。所以 就不需要ViewModel中创建的DataSource继续工作了，所以使用
                    //MutablePageKeyedDataSource也是可以的
                    MutablePageKeyedDataSource dataSource = new MutablePageKeyedDataSource();

                    //这里要把列表上已经显示的先添加到dataSource.data中
                    //而后把本次分页回来的数据再添加到dataSource.data中
                    dataSource.data.addAll(currentList);
                    dataSource.data.addAll(data);
                    PagedList pagedList = dataSource.buildNewPagedList(config);
                    submitList(pagedList);
                }
            }
        });

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }
}

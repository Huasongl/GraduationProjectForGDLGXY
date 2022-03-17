package com.gdlgxy.internshipcommunity.module.mine;

import com.alibaba.fastjson.TypeReference;
import com.gdlgxy.internshipcommunity.base.BaseViewModel;
import com.gdlgxy.internshipcommunity.login.UserManager;
import com.gdlgxy.internshipcommunity.module.basepaging.PagingTabData;
import com.gdlgxy.internshipcommunity.network.ApiResponse;
import com.gdlgxy.internshipcommunity.network.ApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.ItemKeyedDataSource;

public class ProfileViewModel extends BaseViewModel<PagingTabData> {
    private String profileType;


    @Override
    public DataSource createDataSource() {
        return new DataSource();
    }

    public void setProfileType(String tabType) {
        this.profileType = tabType;
    }

    @Override
    public String getPageName() {
        return null;
    }

    private class DataSource extends ItemKeyedDataSource<Integer, PagingTabData> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<PagingTabData> callback) {
            loadData(params.requestedInitialKey, callback);
        }

        private void loadData(Integer key, LoadCallback<PagingTabData> callback) {
            ApiResponse<List<PagingTabData>> response = ApiService.get("/feeds/queryProfileFeeds")
                    .addParam("feedId", key)
                    .addParam("userId", UserManager.get().getUserId())
                    .addParam("pageCount", 10)
                    .addParam("profileType", profileType)
                    .responseType(new TypeReference<ArrayList<PagingTabData>>() {
                    }.getType())
                    .execute();

            List<PagingTabData> result = response.body == null ? Collections.emptyList() : response.body;
            callback.onResult(result);

            if (key > 0) {
                //告知UI层 本次分页是否有更多数据被加载回来了,也方便UI层关闭上拉加载的动画
                ((MutableLiveData) getBoundaryPageData()).postValue(result.size() > 0);
            }

        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<PagingTabData> callback) {
            loadData(params.key, callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<PagingTabData> callback) {
            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull PagingTabData item) {
            return item.id;
        }
    }
}

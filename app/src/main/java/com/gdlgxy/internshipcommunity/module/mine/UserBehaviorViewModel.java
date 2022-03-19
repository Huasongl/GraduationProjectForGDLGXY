package com.gdlgxy.internshipcommunity.module.mine;

import com.alibaba.fastjson.TypeReference;
import com.gdlgxy.internshipcommunity.base.BaseViewModel;
import com.gdlgxy.internshipcommunity.module.login.UserManager;
import com.gdlgxy.internshipcommunity.module.basepaging.PagingTabData;
import com.gdlgxy.internshipcommunity.network.ApiResponse;
import com.gdlgxy.internshipcommunity.network.ApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.ItemKeyedDataSource;

public class UserBehaviorViewModel extends BaseViewModel<PagingTabData> {
    private int mBehavior;

    @Override
    public DataSource createDataSource() {
        return new DataSource();
    }

    public void setBehavior(int behavior) {
        mBehavior = behavior;
    }

    @Override
    public String getPageName() {
        return null;
    }

    class DataSource extends ItemKeyedDataSource<Integer, PagingTabData> {

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<PagingTabData> callback) {
            loadData(params.requestedInitialKey, callback);
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<PagingTabData> callback) {
            loadData(params.key, callback);
        }

        private void loadData(int feedId, LoadCallback<PagingTabData> callback) {
            ApiResponse<List<PagingTabData>> response = ApiService.get("/feeds/queryUserBehaviorList")
                    .addParam("behavior", mBehavior)
                    .addParam("feedId", feedId)
                    .addParam("pageCount", 10)
                    .addParam("userId", UserManager.get().getUserId())
                    .responseType(new TypeReference<ArrayList<PagingTabData>>() {
                    }.getType())
                    .execute();

            List<PagingTabData> result = response.body == null ? Collections.emptyList() : response.body;
            callback.onResult(result);

            if (feedId > 0) {
                ((MutableLiveData) getBoundaryPageData()).postValue(result.size() > 0);
            }
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

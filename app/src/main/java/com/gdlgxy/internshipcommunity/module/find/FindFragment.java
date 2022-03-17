package com.gdlgxy.internshipcommunity.module.find;

import android.text.TextUtils;

import com.gdlgxy.internshipcommunity.AppConfig;
import com.gdlgxy.internshipcommunity.module.community.CommunityFragment;
import com.gdlgxy.internshipcommunity.module.community.CommunityTabData;
import com.gdlgxy.navannotationmodule.FragmentDestination;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


@FragmentDestination(pageUrl = "main/module/find")
public class FindFragment extends CommunityFragment {

    @Override
    public Fragment getTabFragment(int position) {
        CommunityTabData.Tabs tab = getTabConfig().tabs.get(position);
        TagListFragment fragment = TagListFragment.newInstance(tab.tag);
        return fragment;
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        String tagType = childFragment.getArguments().getString(TagListFragment.KEY_TAG_TYPE);
        if (TextUtils.equals(tagType, "onlyFollow")) {
            ViewModelProviders.of(childFragment).get(TagListViewModel.class)
                    .getSwitchTabLiveData().observe(this,
                    object -> mPageContainer.setCurrentItem(1));
        }
    }

    @Override
    public CommunityTabData getTabConfig() {
        return AppConfig.getFindTabConfig();
    }
}
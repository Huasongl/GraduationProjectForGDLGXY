package com.gdlgxy.internshipcommunity.module.community;import android.content.res.ColorStateList;import android.graphics.Color;import android.graphics.Typeface;import android.os.Bundle;import android.util.Log;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.TextView;import com.gdlgxy.internshipcommunity.AppConfig;import com.gdlgxy.internshipcommunity.base.BaseFragment;import com.gdlgxy.internshipcommunity.databinding.FragmentCommunityBinding;import com.gdlgxy.internshipcommunity.module.home.HomeFragment;import com.gdlgxy.navannotationmodule.FragmentDestination;import com.google.android.material.tabs.TabLayout;import com.google.android.material.tabs.TabLayoutMediator;import java.util.ArrayList;import androidx.annotation.NonNull;import androidx.annotation.Nullable;import androidx.fragment.app.Fragment;import androidx.fragment.app.FragmentManager;import androidx.lifecycle.Lifecycle;import androidx.lifecycle.Observer;import androidx.lifecycle.ViewModelProvider;import androidx.viewpager2.adapter.FragmentStateAdapter;import androidx.viewpager2.widget.ViewPager2;@FragmentDestination(pageUrl = "main/module/community", asStarter = false)public class CommunityFragment extends BaseFragment<FragmentCommunityBinding> {    private static final String TAG = "CommunityFragment";    private ViewPager2 mPageContainer;    private TabLayout mTabContainer;    private CommunityTabData mTabData;    private ArrayList<CommunityTabData.Tabs> mTabItemData;    private TabLayoutMediator mTabMediator;    @Override    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {        super.onViewCreated(view, savedInstanceState);        Log.d(TAG,"onViewCreated");        initData();        initView();    }    public Fragment getTabFragment(int position) {        return HomeFragment.newInstance(mTabItemData.get(position).tag);    }    private void initData(){        mTabData = getTabConfig();        mTabItemData = new ArrayList<>();        for (CommunityTabData.Tabs tab : mTabData.tabs) {            if (tab.enable) {                mTabItemData.add(tab);            }        }    }    private void initView(){        mPageContainer = mViewBinding.viewPager;        mTabContainer = mViewBinding.tabLayout;        mPageContainer.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);        mPageContainer.setAdapter(new CommunityFragmentAdapter(getChildFragmentManager(),this.getLifecycle()));        mTabContainer.setTabGravity(mTabData.tabGravity);        mTabMediator = new TabLayoutMediator(mTabContainer, mPageContainer, true, new TabLayoutMediator.TabConfigurationStrategy() {            @Override            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {                tab.setCustomView(makeTabView(position));            }        });        mTabMediator.attach();        mPageContainer.registerOnPageChangeCallback(mPageChangeCallback);        mPageContainer.post(() -> mPageContainer.setCurrentItem(mTabData.select, false));    }    @Override    public void onDestroyView() {        super.onDestroyView();    }    private View makeTabView(int position) {        TextView tabView = new TextView(getContext());        int[][] states = new int[2][];        states[0] = new int[]{android.R.attr.state_selected};        states[1] = new int[]{};        int[] colors = new int[]{Color.parseColor(mTabData.activeColor), Color.parseColor(mTabData.normalColor)};        ColorStateList stateList = new ColorStateList(states, colors);        tabView.setTextColor(stateList);        tabView.setText(mTabItemData.get(position).title);        tabView.setTextSize(mTabData.normalSize);        return tabView;    }    ViewPager2.OnPageChangeCallback mPageChangeCallback = new ViewPager2.OnPageChangeCallback() {        @Override        public void onPageSelected(int position) {            int tabCount = mTabContainer.getTabCount();            for (int i = 0; i < tabCount; i++) {                TabLayout.Tab tab = mTabContainer.getTabAt(i);                TextView customView = (TextView) tab.getCustomView();                if (tab.getPosition() == position) {                    customView.setTextSize(mTabData.activeSize);                    customView.setTypeface(Typeface.DEFAULT_BOLD);                } else {                    customView.setTextSize(mTabData.normalSize);                    customView.setTypeface(Typeface.DEFAULT);                }            }        }    };    @Override    public String getPageName() {        return null;    }    @Override    public FragmentCommunityBinding inflate_Fragment(LayoutInflater layoutInflater, ViewGroup container, boolean at) {        return FragmentCommunityBinding.inflate(layoutInflater, container, false);    }    public CommunityTabData getTabConfig() {        return AppConfig.getSofaTabConfig();    }    private class CommunityFragmentAdapter extends FragmentStateAdapter {        public CommunityFragmentAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {            super(fragmentManager, lifecycle);        }        @NonNull        @Override        public Fragment createFragment(int position) {            return getTabFragment(position);        }        @Override        public int getItemCount() {            return mTabItemData.size();        }    }}
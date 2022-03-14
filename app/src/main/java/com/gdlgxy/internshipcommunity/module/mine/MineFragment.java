package com.gdlgxy.internshipcommunity.module.mine;import android.os.Bundle;import android.util.Log;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import com.gdlgxy.internshipcommunity.R;import com.gdlgxy.internshipcommunity.base.BaseFragment;import com.gdlgxy.internshipcommunity.databinding.FragmentMineBinding;import com.gdlgxy.internshipcommunity.login.UserManager;import com.gdlgxy.internshipcommunity.module.basepaging.User;import com.gdlgxy.navannotationmodule.FragmentDestination;import androidx.annotation.NonNull;import androidx.annotation.Nullable;import androidx.appcompat.app.AlertDialog;import androidx.lifecycle.Observer;import androidx.lifecycle.ViewModelProvider;@FragmentDestination(pageUrl = "main/module/mine", asStarter = false)public class MineFragment extends BaseFragment<FragmentMineBinding, MineViewModel> implements View.OnClickListener {    @Override    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {        super.onViewCreated(view, savedInstanceState);        Log.d("fragment-onViewCreated", "MineFragment");        mView.ivUserIcon.setOnClickListener(this);        mView.fragmentIvLogout.setOnClickListener(this);        mView.tvUserName.setOnClickListener(this);        UserManager.get().refresh().observe(getViewLifecycleOwner(), newUser -> {            if (newUser != null) {                mView.setUser(newUser);            }        });    }    @Override    public void onDestroyView() {        super.onDestroyView();    }    @Override    protected MineViewModel createdViewModel() {        return new ViewModelProvider(this).get(MineViewModel.class);    }    @Override    public String getPageName() {        return getClass().getName();    }    @Override    public FragmentMineBinding inflate_Fragment(LayoutInflater layoutInflater, ViewGroup container, boolean at) {        return FragmentMineBinding.inflate(layoutInflater, container, at);    }    @Override    public void onClick(View v) {        if (v.getId() == mView.tvUserName.getId() || v.getId() == mView.ivUserIcon.getId()) {            if (!UserManager.get().isLogin()) {                UserManager.get().login(getContext()).observe(this, new Observer<User>() {                    @Override                    public void onChanged(User user) {                        mView.setUser(user);                    }                });            }        } else if (v.getId() == mView.fragmentIvLogout.getId()) {            if (!UserManager.get().isLogin()) {                return;            }            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())                    .setMessage(getString(R.string.fragment_my_logout))                    .setPositiveButton(getString(R.string.fragment_my_logout_ok), (dialog, which) -> {                        dialog.dismiss();                        UserManager.get().logout();                        onResume();                    }).setNegativeButton(getString(R.string.fragment_my_logout_cancel), null);            builder.create().show();        }    }}
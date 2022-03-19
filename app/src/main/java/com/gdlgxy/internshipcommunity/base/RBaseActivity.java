package com.gdlgxy.internshipcommunity.base;import android.os.Bundle;import android.view.LayoutInflater;import java.lang.reflect.ParameterizedType;import java.lang.reflect.Type;import androidx.databinding.ViewDataBinding;import androidx.lifecycle.ViewModel;import androidx.lifecycle.ViewModelProvider;import me.imid.swipebacklayout.lib.app.SwipeBackActivity;public abstract class RBaseActivity<T extends ViewDataBinding, U extends RBaseViewModel> extends SwipeBackActivity {    protected T mViewBinding = null;    protected U mViewModel = null;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        mViewBinding = createViewBinding(getLayoutInflater());        mViewModel = createViewModel();        setContentView(mViewBinding.getRoot());    }    @Override    protected void onDestroy() {        super.onDestroy();    }    private U createViewModel() {        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();        Type[] arguments = parameterizedType.getActualTypeArguments();        Type argument0 = arguments[1];        Class clazz = ((Class) argument0).asSubclass(RBaseViewModel.class);        return (U) ViewModelProvider.NewInstanceFactory.getInstance().create(clazz);    }    protected abstract T createViewBinding(LayoutInflater inflater);}
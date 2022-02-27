package com.gdlgxy.internshipcommunity.base;import android.os.Bundle;import android.view.LayoutInflater;import java.lang.reflect.ParameterizedType;import java.lang.reflect.Type;import androidx.lifecycle.ViewModel;import androidx.lifecycle.ViewModelProvider;import androidx.viewbinding.ViewBinding;import io.reactivex.disposables.CompositeDisposable;import io.reactivex.disposables.Disposable;import me.imid.swipebacklayout.lib.app.SwipeBackActivity;import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;public abstract class BaseActivity<T extends ViewBinding, V extends ViewModel> extends SwipeBackActivity        implements IGetPageName, IViewBinding.inflate_Activity<T> {    protected T mView = null;    protected V mViewModel = null;    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        mView = inflate_Activity(getLayoutInflater());        mViewModel = createViewModel();        setContentView(mView.getRoot());        setSwipeBackEnable(swipeBackEnable());    }    @Override    protected void onStart() {        super.onStart();    }    @Override    protected void onStop() {        super.onStop();    }    @Override    protected void onPause() {        super.onPause();    }    @Override    protected void onDestroy() {        mCompositeDisposable.dispose();        super.onDestroy();    }    protected boolean swipeBackEnable() {        return true;    }    protected abstract V createViewModel();//    private void createViewModel() {//        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();//        Type typeArgument = type.getActualTypeArguments()[1];//        Class<? extends Type> clazz = null;//        clazz = typeArgument.getClass();//        mViewModel = (V) ViewModelProvider.NewInstanceFactory.getInstance().create(clazz);//    }    protected void addDisposable(Disposable disposable) {        mCompositeDisposable.add(disposable);    }}
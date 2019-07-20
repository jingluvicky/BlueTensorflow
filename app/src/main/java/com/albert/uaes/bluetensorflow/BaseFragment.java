package com.albert.uaes.bluetensorflow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.albert.uaes.bluetensorflow.service.MyService;

public abstract class BaseFragment extends Fragment {

    public MyService myService;

    private View mRootView;

    /**
     * 在这里实现Fragment数据的缓加载
     */
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        Log.i("lazyLaod","获取界面数据 --- setUserVisibleHint");
//        if(getUserVisibleHint()){
//            Log.i("lazyLaod","获取界面数据 --- setUserVisibleHinttrue");
//            isVisible = true;
//            onVisible();
//        }else{
//            Log.i("lazyLaod","获取界面数据 --- setUserVisibleHintfalse");
//            isVisible = false;
//            onInVisible();
//        }
//    }
//
//    private void onInVisible() {
//    }
//
//    protected  void onVisible(){
//        lazyLoad();
//    }

    /**
     * 加载数据
     */
//    protected abstract void lazyLoad();



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(setLayoutId(), container, false);
        init(mRootView,savedInstanceState);
        return mRootView;
    }

    protected abstract void init(View view ,Bundle savedInstanceState);

    protected abstract int setLayoutId();


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
         * 可以初始化一些页面的UI loadingDialog
         */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

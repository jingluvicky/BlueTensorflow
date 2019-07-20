package com.albert.uaes.bluetensorflow;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.albert.uaes.bluetensorflow.net.BleDisconnectEvent;
import com.albert.uaes.bluetensorflow.utils.ThreadPoolManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

public class LocationOutputFragment extends BaseFragment{

    private final static int UPDATE_UI = 0;

    private Runnable updateUiRunnable;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_UI:
                    updateUi();
                    break;
            }
        }
    };

    public static LocationOutputFragment newInstance() {

        Bundle args = new Bundle();

        LocationOutputFragment fragment = new LocationOutputFragment();
        fragment.setArguments(args);
        return fragment;
    }

    protected void init(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
    }

    private void doUpdateThread() {
        updateUiRunnable = new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(UPDATE_UI);
            }
        };
        ThreadPoolManager.getInstance(ThreadPoolManager.Scheduled_ThreadPool_Executor_Type).scheduleAtFixedRate(updateUiRunnable,0,20,TimeUnit.MILLISECONDS);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(BleDisconnectEvent bleDisconnectEvent) {
        if (bleDisconnectEvent.isConnect()){
            doUpdateThread();
        }else {

        }
    }
    private void updateUi(){

    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_locationoutput;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}

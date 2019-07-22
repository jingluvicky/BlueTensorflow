package com.albert.uaes.bluetensorflow;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.albert.uaes.bluetensorflow.net.BleDisconnectEvent;
import com.albert.uaes.bluetensorflow.service.MyService;
import com.albert.uaes.bluetensorflow.utils.ThreadPoolManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import static com.albert.uaes.bluetensorflow.service.MyService.START_RECORD;

public class SettingFragment extends BaseFragment {

    private ImageView zoneImage;
    private ImageView imgConnectlocation;
    private ImageView imgWalk;
    private ImageView imgPocket;
    private ImageView img_walk,img_pocket,img_lock,img_dynamic,img_trend;
    private ImageView img_zone0,img_zone1,img_zone3,img_zone4,img_zone5,img_zone6;
    private ImageView img_connect;
    private TextView txt_unlockdis,txt_lockdis,txt_zone,txt_trend;
    private Button btn_1,btn_2,btn_3,btn_4,btn_unlockminus,btn_unlockplus,btn_lockminus,btn_lockplus;
    private Switch switch_record;
    private final static int UPDATE_UI = 0;
    public static boolean isRecord=false;
    private Runnable updateUiRunnable;
    public static int sensorTag=0;


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


    public static SettingFragment newInstance() {

        Bundle args = new Bundle();

        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        zoneImage = view.findViewById(R.id.zoneimage);
        imgConnectlocation = view.findViewById(R.id.img_connectlocation);
        imgWalk = view.findViewById(R.id.img_walk);
        imgPocket = view.findViewById(R.id.img_pocket);
        switch_record=view.findViewById(R.id.switch_record);
        btn_1=view.findViewById(R.id.btn_1);
        btn_2=view.findViewById(R.id.btn_2);
        btn_3=view.findViewById(R.id.btn_3);
        btn_4=view.findViewById(R.id.btn_4);
        switch_record.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isRecord = true;
                 } else {
                    isRecord = false;
                }
            }
        });
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorTag=1;
                btn_1.setBackgroundColor(Color.parseColor("#FFE7BA"));
                btn_2.setBackgroundColor(0);
                btn_3.setBackgroundColor(0);
                btn_4.setBackgroundColor(0);
            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorTag=2;
                btn_2.setBackgroundColor(Color.parseColor("#FFE7BA"));
                btn_1.setBackgroundColor(0);
                btn_3.setBackgroundColor(0);
                btn_4.setBackgroundColor(0);
            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorTag=3;
                btn_3.setBackgroundColor(Color.parseColor("#FFE7BA"));
                btn_2.setBackgroundColor(0);
                btn_1.setBackgroundColor(0);
                btn_4.setBackgroundColor(0);
            }
        }); btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorTag=4;
                btn_4.setBackgroundColor(Color.parseColor("#FFE7BA"));
                btn_2.setBackgroundColor(0);
                btn_3.setBackgroundColor(0);
                btn_1.setBackgroundColor(0);
            }
        });


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
            imgPocket.setImageResource(R.drawable.pocket_out);
            imgWalk.setImageResource(R.drawable.walk_off);
            imgConnectlocation.setImageResource(R.drawable.connect_off);
            zoneImage.setImageResource(R.drawable.zone_unknown);
        }
    }


    private void updateUi(){
        int walk=ScanFragment.curMotion;
        int zone_temp=ScanFragment.curZone;
        int curLeftRight=ScanFragment.curLeftRight;
        int pocketState=ScanFragment.curPocketState;

        if (pocketState==1){
            imgPocket.setImageResource(R.drawable.pocket_in);
        }else{
            imgPocket.setImageResource(R.drawable.pocket_out);
        }
        if (walk==2){
            imgWalk.setImageResource(R.drawable.walk_on);
        }else{
            imgWalk.setImageResource(R.drawable.walk_off);
        }
        int blueState=ScanFragment.blueState;
        if (blueState == 2){
            imgConnectlocation.setImageResource(R.drawable.connect_on);
        }else{
            imgConnectlocation.setImageResource(R.drawable.connect_off);
        }
        if(blueState == 2) {
            //int zone =  MainTabConnectInfoFragment.readZone();
            if (zone_temp == 1) {
                if (curLeftRight == 1){
                    zoneImage.setImageResource(R.drawable.zone1_left);
                } else {
                    zoneImage.setImageResource(R.drawable.zone1_right);
                }

                //imv_Locksign.setImageResource(R.mipmap.greenicon);
            } else if (zone_temp == 2) {
                //    if (curLeftRight==1)
                //     location.setImageResource(R.mipmap.zone_unknown);
                // else  {location.setImageResource(R.mipmap.zone_unknown);}

            } else if (zone_temp == 3) {
                if (curLeftRight == 1){
                    zoneImage.setImageResource(R.drawable.zone3_left);
                } else {
                    zoneImage.setImageResource(R.drawable.zone3_right);
                }
                //imv_Locksign.setImageResource(R.mipmap.redicon2);
            } else if (zone_temp == 0) {
                zoneImage.setImageResource(R.drawable.zone_0);
            } else if (zone_temp == 255) {
                zoneImage.setImageResource(R.drawable.zone_unknown);
            }
        }else {
            zoneImage.setImageResource(R.drawable.zone_unknown);
        }
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fargment_setting;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}

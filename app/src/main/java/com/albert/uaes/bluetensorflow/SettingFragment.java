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
    private ImageView img_lock,img_dynamic,img_trend;
    private ImageView img_zone0,img_zone1,img_zone3,img_zone4,img_zone5,img_zone6;
    private ImageView img_connect;
    private TextView txt_unlockdis,txt_lockdis,txt_zone,txt_trend;
    private Button btn_1,btn_2,btn_3,btn_4,btn_rear,btn_front,btn_left,btn_right;
    Button btn_unlockminus,btn_unlockplus,btn_lockminus,btn_lockplus;

    private Switch switch_type;
    private Switch switch_record;
    private final static int UPDATE_UI = 0;

    public static boolean isRecord=false;
    private Runnable updateUiRunnable;
    public static int sensorTag=0;
    public static int uwbZone=0;
    public static int lockDis=25,unlockDis=15;


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
        switch_type=view.findViewById(R.id.switch_type);
        btn_1=view.findViewById(R.id.btn_1);
        btn_2=view.findViewById(R.id.btn_2);
        btn_3=view.findViewById(R.id.btn_3);
        btn_4=view.findViewById(R.id.btn_4);
        btn_rear=view.findViewById(R.id.btn_rear);

        btn_front=view.findViewById(R.id.btn_front);
        btn_left=view.findViewById(R.id.btn_left);
        btn_right=view.findViewById(R.id.btn_right);

        btn_lockminus = view.findViewById(R.id.btn_lockminus);
        btn_lockplus = view.findViewById(R.id.btn_lockplus);
        btn_unlockminus = view.findViewById(R.id.btn_unlockminus);
        btn_unlockplus = view.findViewById(R.id.btn_unlockplus);
        txt_lockdis = view.findViewById(R.id.txt_lockDis);
        txt_unlockdis = view.findViewById(R.id.txt_unlockDis);
        txt_zone=view.findViewById(R.id.txt_zone);
        img_zone0 = view.findViewById(R.id.img_zone0);
        img_zone1 = view.findViewById(R.id.img_zone1);
        img_zone3 = view.findViewById(R.id.img_zone3);
        img_zone4 = view.findViewById(R.id.img_zone4);
        img_zone5 = view.findViewById(R.id.img_zone5);
        img_zone6 = view.findViewById(R.id.img_zone6);

        btn_lockminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockDis = lockDis - 1;
                txt_lockdis.setText(" " + (float) lockDis / 10);
            }
        });
        btn_lockplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockDis = lockDis + 1;
                txt_lockdis.setText(" " + (float) lockDis / 10);
            }
        });
        btn_unlockminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockDis = unlockDis - 1;
                txt_unlockdis.setText(" " + (float) unlockDis / 10);
            }
        });
        btn_unlockplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockDis = unlockDis + 1;
                txt_unlockdis.setText(" " + (float) unlockDis / 10);
            }
        });
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
        switch_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ScanFragment.decisiontype=2;
                }else{
                    ScanFragment.decisiontype=1;
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
        });
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorTag=4;
                btn_4.setBackgroundColor(Color.parseColor("#FFE7BA"));
                btn_2.setBackgroundColor(0);
                btn_3.setBackgroundColor(0);
                btn_1.setBackgroundColor(0);
            }
        });
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uwbZone = 0;
                btn_left.setBackgroundColor(Color.parseColor("#FFE7BA"));
                btn_right.setBackgroundColor(0);
                btn_rear.setBackgroundColor(0);
                btn_front.setBackgroundColor(0);
            }
        });
        btn_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uwbZone = 1;
                btn_front.setBackgroundColor(Color.parseColor("#FFE7BA"));
                btn_right.setBackgroundColor(0);
                btn_rear.setBackgroundColor(0);
                btn_left.setBackgroundColor(0);
            }
        });
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uwbZone = 2;
                btn_right.setBackgroundColor(Color.parseColor("#FFE7BA"));
                btn_left.setBackgroundColor(0);
                btn_rear.setBackgroundColor(0);
                btn_front.setBackgroundColor(0);
            }
        });
        btn_rear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uwbZone = 3;
                btn_rear.setBackgroundColor(Color.parseColor("#FFE7BA"));
                btn_right.setBackgroundColor(0);
                btn_left.setBackgroundColor(0);
                btn_front.setBackgroundColor(0);
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
        int curZone=ScanFragment.curZoneDebounced;
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
            if (curZone == 1) {
                if (curLeftRight == 1){
                    zoneImage.setImageResource(R.drawable.zone1_left);
                } else {
                    zoneImage.setImageResource(R.drawable.zone1_right);
                }

                //imv_Locksign.setImageResource(R.mipmap.greenicon);
            } else if (curZone == 2) {
                //    if (curLeftRight==1)
                //     location.setImageResource(R.mipmap.zone_unknown);
                // else  {location.setImageResource(R.mipmap.zone_unknown);}

            } else if (curZone == 3) {
                if (curLeftRight == 1){
                    zoneImage.setImageResource(R.drawable.zone3_left);
                } else {
                    zoneImage.setImageResource(R.drawable.zone3_right);
                }
                //imv_Locksign.setImageResource(R.mipmap.redicon2);
            } else if (curZone == 0) {
                zoneImage.setImageResource(R.drawable.zone_0);
            } else if (curZone == 255) {
                zoneImage.setImageResource(R.drawable.zone_unknown);
            }
        }else {
            zoneImage.setImageResource(R.drawable.zone_unknown);
        }

        //region

        if (curZone<=6){
            img_zone6.setImageResource(R.drawable.greenicon);
            if (curZone<=5){
                img_zone5.setImageResource(R.drawable.greenicon);
                if (curZone<=4){
                    img_zone4.setImageResource(R.drawable.greenicon);
                    switch (curZone){
                        case (0):
                            img_zone0.setImageResource(R.drawable.greenicon);
                            img_zone1.setImageResource(R.drawable.redicon2);
                            img_zone3.setImageResource(R.drawable.redicon2);
                            break;
                        case(1):
                            img_zone1.setImageResource(R.drawable.greenicon);
                            img_zone0.setImageResource(R.drawable.redicon2);
                            img_zone3.setImageResource(R.drawable.redicon2);
                            break;
                        case(3):
                            img_zone3.setImageResource(R.drawable.greenicon);
                            img_zone0.setImageResource(R.drawable.redicon2);
                            img_zone1.setImageResource(R.drawable.redicon2);
                            break;
                    }
                }else{
                    img_zone4.setImageResource(R.drawable.redicon2);
                    img_zone0.setImageResource(R.drawable.grayicon);
                    img_zone1.setImageResource(R.drawable.grayicon);
                    img_zone3.setImageResource(R.drawable.grayicon);
                }
            }else{
                img_zone5.setImageResource(R.drawable.redicon2);
                img_zone4.setImageResource(R.drawable.grayicon);
                img_zone0.setImageResource(R.drawable.grayicon);
                img_zone1.setImageResource(R.drawable.grayicon);
                img_zone3.setImageResource(R.drawable.grayicon);
            }
        }else{
            img_zone6.setImageResource(R.drawable.redicon2);
            img_zone5.setImageResource(R.drawable.grayicon);
            img_zone4.setImageResource(R.drawable.grayicon);
            img_zone0.setImageResource(R.drawable.grayicon);
            img_zone1.setImageResource(R.drawable.grayicon);
            img_zone3.setImageResource(R.drawable.grayicon);
        }
        txt_zone.setText(curZone+"\n"+ScanFragment.curZoneDebounced);
        //endregion
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

package com.albert.uaes.bluetensorflow;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.albert.uaes.bluetensorflow.net.BleDisconnectEvent;
import com.albert.uaes.bluetensorflow.service.MyService;
import com.albert.uaes.bluetensorflow.service.WrapRssiData;
import com.albert.uaes.bluetensorflow.utils.RxPermissionUtils;
import com.albert.uaes.bluetensorflow.utils.ThreadPoolManager;
import com.albert.uaes.tensorflowlibrary.admin.PEPSBuilder;
import com.albert.uaes.tensorflowlibrary.admin.PEPSDirector;
import com.albert.uaes.tensorflowlibrary.admin.PEPSImplBuilder;
import com.albert.uaes.tensorflowlibrary.tf.KalmanFilter_distance;
import com.albert.uaes.tensorflowlibrary.tf.PocketDetector;
import com.albert.uaes.tensorflowlibrary.tf.PredictionTF_distance0711;
import com.albert.uaes.tensorflowlibrary.tf.PredictionTF_motion;
import com.albert.uaes.tensorflowlibrary.tf.PredictionTF_xy;
import com.albert.uaes.tensorflowlibrary.model.Node;
import com.albert.uaes.tensorflowlibrary.tf.LUTprediction_top;
import com.albert.uaes.tensorflowlibrary.tf.ZoneDebounce;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

/**
 * @author Albert
 */
public class ScanFragment extends BaseFragment {

    private String TAG="ScanFragment";

    private final static int REQUEST_ENABLE_BT = 1;
    private int BLE_OPENED = 0;

    private Switch aSwitch;
    private ImageView imgScan;
    private ImageView imgConnect;
    private TextView txt_curMotion;
    private TextView txt_curZone;
    private TextView textRssi;

    private BleConnectedReceiver bleConnectedReceiver;

    public float[] curMotionOutput,curZoneOutput;

    private PredictionTF_motion preTF_motion;
    private PredictionTF_distance0711 preTF_zone;
    PredictionTF_xy preTF_xy;
    LUTprediction_top luTpredictionTop =new LUTprediction_top(); //Lookup table
    ZoneDebounce zoneDebounce=new ZoneDebounce();
    KalmanFilter_distance distanceFilter=new KalmanFilter_distance();

    public int CMDCounter;
    public int CMDValue;
    public static int curMotion=255,curZone=255,curLeftRight=255,curZoneDebounced,curZone_filtered=255,curPocketState,awakeState=0,curX=0,curY=0,decisiontype=0;

    public static float[] linearAccValue,gravityValue,gyroValue,acceleroValue;

    public static float distance;
    public static WrapRssiData wrapRssiData;

    // Variables
    public volatile boolean exit = false;
    public static volatile boolean toConnect=true, isScan=false,isConnect=false;
    public static byte[] dataReceived=new byte[15];
    public static Node[] Nodes = new Node[13];
    public int
            DECISIONTYPE,MOTIONEABLE,
            ZONEBUFFERNUMBER,
            DEBOUNCEDBUFFER1to3,DEBOUNCEDBUFFER3to1,DEBOUNCEDBUFFER1toi,
            CARCONFIGTYPE;

    public static float distanceValue,light;

    public int []zonebuffer=new int[5],motionbuffer=new int[5];

    private boolean[] switches = new boolean[8];



    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final int MOTIONCHAGE = 0xabc0010;

    public static final int ZONECHANGE = 0xabc0011;

    public static volatile int blueState;

    public Runnable writeDataToMasterRunnable;
    public Runnable sportTFRunnable;
    public Runnable zoneTFRunnable;
    public Runnable uiUpdateRunnable;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MOTIONCHAGE:
                    if (curMotion==2){
                        txt_curMotion.setText("Current motion:  walking"   + " \n" + curMotionOutput[0] + " " + curMotionOutput[1] + " " + curMotionOutput[2]);
                    }else{
                        txt_curMotion.setText("Current motion: not walking" + " \n" + curMotionOutput[0] + " " + curMotionOutput[1] + " " + curMotionOutput[2]);

                    }
                    break;
                case ZONECHANGE:
                    textRssi.setText("M :  " + (int)Nodes[0].rssi_filtered + "\n" +
                            "A1:  " + (int) Nodes[1].rssi_filtered + "\n" +
                            "A2:  " + (int) (Nodes[2].rssi_filtered)+ "\n" +
                            "A3:  " + (int) (Nodes[3].rssi_filtered)+ "\n" +
                            "A4:  " + (int) Nodes[4].rssi_filtered + "\n" +
                            "A5:  " + (int) Nodes[5].rssi_filtered + "\n" +
                            "A6:  " + (int) Nodes[6].rssi_filtered + "\n" +
                            "A7:  " + (int) Nodes[7].rssi_filtered + "\n" +
                            "A8:  " + (int) Nodes[8].rssi_filtered + "\n" +
                            "A9:  " + (int) Nodes[9].rssi_filtered + "\n"
                    );
                    if (curZoneOutput != null) {
                        txt_curZone.setText("Current Zone:  " + curZone + "  \n" + +curZoneOutput[0] + "  " + curZoneOutput[1] + " " + curZoneOutput[2]);
                    }
                    break;
                    default:
                        break;
            }
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        /** 获取服务对象时的操作 */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            myService = ((MyService.LocalBinder) service).getService();

            if (myService.initialize() == 1){
                startConnect();
            }else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            myService.setSensors(switches);
        }

        /** 无法获取到服务对象时的操作 */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            myService = null;
        }

    };

    public static ScanFragment newInstance() {
        Bundle args = new Bundle();
        ScanFragment fragment = new ScanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        registerReceiver();

        initNode();

        aSwitch = view.findViewById(R.id.switch_connect);
        imgScan = view.findViewById(R.id.img_scan);
        imgConnect = view.findViewById(R.id.img_connect);
        txt_curMotion =view.findViewById(R.id.txt_curMotion);
        txt_curZone = view.findViewById(R.id.txt_curZone);
        textRssi = view.findViewById(R.id.text_rssi);

        DECISIONTYPE=getResources().getInteger(R.integer.DECISIONTYPE);
        ZONEBUFFERNUMBER=getResources().getInteger(R.integer.ZONEBUFFER);
        DEBOUNCEDBUFFER1to3=getResources().getInteger(R.integer.DEBOUNCEDBUFFER1to3);
        DEBOUNCEDBUFFER3to1=getResources().getInteger(R.integer.DEBOUNCEDBUFFER3to1);
        DEBOUNCEDBUFFER1toi=getResources().getInteger(R.integer.DEBOUNCEDBUFFER1toi);
        CARCONFIGTYPE=getResources().getInteger(R.integer.CARCONFIGTYPE);
        MOTIONEABLE=getResources().getInteger(R.integer.MOTIONENABLE);

        for (int i=0; i<switches.length; ++i) {
            switches[i] = true;
        }
        preTF_motion=new PredictionTF_motion(this.getContext().getAssets());
        preTF_zone = new PredictionTF_distance0711(this.getContext().getAssets());

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (myService!=null){
                        BLE_OPENED = myService.initialize();
                        switch (BLE_OPENED){
                            case 0:
                                Toast.makeText(getActivity(),"不支持蓝牙",Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                startConnect();
                                break;
                            case 2:
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                break;
                                default:
                                    break;
                        }
                    }else {
                        RxPermissionUtils.checkPermissionRequest(getActivity(), new RxPermissionUtils.ApplyPermissionCallback() {
                            @Override
                            public void onPermissionsGranted() {
                                Intent intent = new Intent(getActivity(),MyService.class);
                                getActivity().bindService(intent,conn,Context.BIND_AUTO_CREATE);
                            }

                            @Override
                            public void onPermissionsDenied() {
                                Toast.makeText(getActivity(),"请允许相关权限",Toast.LENGTH_SHORT).show();
                            }
                        },PERMISSIONS_STORAGE);
                    }
                }else {
                    // TODO: 2019/4/10 取消扫描
                    stopConenct(false);
                    removeRunnable();
                }
            }
        });

        if (myService != null){
            blueState = myService.getBlueState();
            switch (blueState){
                case 0:
                    //不在扫描中
                    imgScan.setImageResource(R.mipmap.redicon2);
                    imgConnect.setImageResource(R.mipmap.redicon2);
                    break;
                case 2:
                    //连接中
                    imgScan.setImageResource(R.mipmap.redicon2);
                    imgConnect.setImageResource(R.mipmap.greenicon);
                    doTfAndShowResult();
                    break;
                    default:
                        //不在扫描中
                        imgScan.setImageResource(R.mipmap.greenicon);
                        imgConnect.setImageResource(R.mipmap.redicon2);
                        break;
            }
        }else {
            blueState = 0;
        }
    }

    private void initNode() {
        for(int i=0;i<12;i++){
            Nodes[i]=new Node();
            Nodes[i].rssi=100;
            Nodes[i].rssi_filtered=100;
        }
    }

    public void doTfAndShowResult(){
        /**
         * 运动模型计算
         */

        if (sportTFRunnable == null){
            sportTFRunnable = new Runnable() {
                @Override
                public void run() {

                    PEPSBuilder pepsBuilder=new PEPSImplBuilder();
                    PEPSDirector pepsDirector= PEPSDirector.getInstance(pepsBuilder);

                    pepsDirector.setPredictionTF_motion(preTF_motion)
                            .getMotionState(getActivity());


                        // init sensor values
                        if (gyroValue == null) {
                            gyroValue = new float[3];
                        }
                        if (gravityValue == null) {
                            gravityValue = new float[3];
                        }
                        if (acceleroValue == null) {
                            acceleroValue = new float[3];
                        }
                        if (linearAccValue == null) {
                            linearAccValue = new float[3];
                        }

                        if (PocketDetector.inPocket(gravityValue, distanceValue, light)) {
                            curPocketState = 1;
                        } else {
                            curPocketState = 0;
                        }
                        preTF_motion.Storage(gyroValue, linearAccValue, acceleroValue);
                        float[] outputs = preTF_motion.getPredict();
                        float tempMax = -10;
                        int tempMaxNumber = 0;
                        for (int i = 0; i < 3; i++) {
                            if (outputs[i] > tempMax) {
                                tempMax = outputs[i];
                                tempMaxNumber = i;
                            }
                        }
                        tempMaxNumber = tempMaxNumber + 1;

                        if (tempMaxNumber == 2 && outputs[1] > 2) {
                            curMotion = tempMaxNumber;
                        } else if (tempMaxNumber != 2) {
                            curMotion = tempMaxNumber;
                        }
                        curMotionOutput = outputs;
                        //通知页面绘制结果
                        if (curMotionOutput != null) {
                            handler.sendEmptyMessage(MOTIONCHAGE);
                        }
                        for (int i = 0; i < 4; i++) {
                            motionbuffer[i] = motionbuffer[i + 1];

                        }
                        motionbuffer[4] = curZone;
                    }
            };
        }
        ThreadPoolManager.getInstance(ThreadPoolManager.Scheduled_ThreadPool_Executor_Type).scheduleAtFixedRate(sportTFRunnable,0,100,TimeUnit.MILLISECONDS);


        /**
         * 位置模型计算
         */

        if (zoneTFRunnable == null){
            zoneTFRunnable = new Runnable() {
                @Override
                public void run() {
                        // perform the tensorflow model
                    curZone=6; //状态为连接态
                    initNode();
                    if (awakeState == 0) {//判断是否唤醒节点
                        if( Nodes[0].rssi_filtered<80 ){
                            curZone=5;
                            awakeState=1;
                        }
                    }

                    if (awakeState==1) {//唤醒态可判断位置

                        //region  利用定位算法判断位置
                        // 1. perform the tensorflow model
                        // 2. perform Lookup table
                        preTF_xy.Storage(Nodes);
                        float []a = preTF_xy.getPredict();
                        curX=(int)(a[0]*800);
                        curY=(int)(a[1]*800);
                        switch (DECISIONTYPE) {
                            case 1:
                                float[] outputs = new float[1];
                                switch (CARCONFIGTYPE) {
                                    case 1:
                                        preTF_zone.Storage(Nodes);
                                        outputs = preTF_zone.getPredict();
                                        if (distanceFilter == null)
                                            distanceFilter = new KalmanFilter_distance();

                                        break;
                                    case 2:


                                        break;

                                    default:
                                        outputs[0] = 0;
                                        break;

                                }
                                outputs[0] = (float) distanceFilter.FilteredRSSI(outputs[0], true);

                                distance = outputs[0]*5;
                                if (distance < (float) SettingFragment.unlockDis / 10)
                                    curZone = 1;
                                else if (distance > (float) SettingFragment.lockDis / 10)
                                    curZone = 3;
                                else curZone = 2;
                                if (Nodes[0].rssi_filtered < 60) curZone = 1;
                                int temp = luTpredictionTop.PEPS_s32CaliFunction(Nodes,curPocketState);
                                if (temp <= 1)
                                    curZone = temp;
                                break;
                            case 2:
                                switch (CARCONFIGTYPE) {
                                    case (1):
                                }
                                curZone = luTpredictionTop.PEPS_s32CaliFunction(Nodes,curPocketState);
                                break;
                        }
                        //endregion
                    }
                    curZoneDebounced=zoneDebounce.DebouncedZone(curZone);


                    }
            };
        }

        ThreadPoolManager.getInstance(ThreadPoolManager.Scheduled_ThreadPool_Executor_Type).scheduleAtFixedRate(zoneTFRunnable,0,100,TimeUnit.MILLISECONDS);

        /**
         * 更新页面UI
         */
        if (uiUpdateRunnable == null){
            uiUpdateRunnable = new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(ZONECHANGE);
                }
            };
        }
        ThreadPoolManager.getInstance(ThreadPoolManager.Scheduled_ThreadPool_Executor_Type).scheduleAtFixedRate(uiUpdateRunnable,0,100,TimeUnit.MILLISECONDS);


    }

    @Override
    protected int setLayoutId() {
        return R.layout.fargment_scan;
    }


    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyService.ACTION_GATT_CONNECTED);
        filter.addAction(MyService.ACTION_GATT_CONNECTING);
        filter.addAction(MyService.ACTION_GATT_DISCONNECTED);
        filter.addAction(MyService.ACTION_GATT_DISCONNECTING);
        filter.addAction(MyService.ACTION_GATT_SERVICE_DISCOVERED);

        filter.addAction(MyService.ACTION_RSSI_VALUE);

        bleConnectedReceiver = new BleConnectedReceiver();
        //注册广播接收
        getActivity().registerReceiver(bleConnectedReceiver,filter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode==Activity.RESULT_OK){
            // TODO: 2019/4/10 蓝牙开启成功 开始扫描
            startConnect();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (conn !=null){
            getActivity().unbindService(conn);
        }
        getActivity().unregisterReceiver(bleConnectedReceiver);
    }


    class BleConnectedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyService.ACTION_GATT_CONNECTED)){
                // TODO: 2019/4/11 连接上蓝牙
                setUIBleState(false,true);
                blueState = 2;
               doTfAndShowResult();
                EventBus.getDefault().post(new BleDisconnectEvent(true));

                sendCmdConfirm();
            }else if (intent.getAction().equals(MyService.ACTION_GATT_CONNECTING)){
                // TODO: 2019/4/11 正在连接蓝牙 
            }else if (intent.getAction().equals(MyService.ACTION_GATT_DISCONNECTED)){
                // TODO: 2019/4/11 蓝牙断开
                if (aSwitch.isChecked()){
                    stopConenct(true);
                }else {
                    stopConenct(false);
                }
            }else if (intent.getAction().equals(MyService.ACTION_GATT_DISCONNECTING)){
                // TODO: 2019/4/11 蓝牙断开中
            } else if (intent.getAction().equals(MyService.ACTION_RSSI_VALUE)){
                wrapRssiData = (WrapRssiData) intent.getSerializableExtra("data");
                readWrapRssiData();
            }else if (intent.getAction().equals(MyService.ACTION_GATT_SERVICE_DISCOVERED)){
                writeDataToMaster();
            }
        }
    }

    private void sendCmdConfirm() {

//        byte[] data = CmdDataUtils.createData(0x0101);
//
//        myService.writeValue(data);
    }

    private void writeDataToMaster() {

        if (writeDataToMasterRunnable == null){
            writeDataToMasterRunnable = new Runnable() {
                @Override
                public void run() {
                    // create new byte array
                    byte[] atemp = new byte[]{0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0};

                    Integer[] I=new Integer[15];
                    int tempCMD;
                    if(CMDCounter>0){
                        tempCMD=(CMDValue);
                        CMDCounter--;}
                    else{
                        CMDValue=0;
                        tempCMD=0;
                    }

                    // Command
                    I[0]=tempCMD;
                    // current Zone
                    I[1] = curZone_filtered;
                    I[2] =curLeftRight;
                    I[3] = 0;

                    if (curMotion==2){
                        I[4] = 1;
                    }else{
                        I[4]=0;
                    }


                    I[5] = (int)Nodes[0].rssi_filtered;
                    I[6]=(int)Nodes[1].rssi_filtered;
                    I[7] = (int)Nodes[2].rssi_filtered;
                    I[8]=(int)Nodes[3].rssi_filtered;
                    I[9]=(int)Nodes[4].rssi_filtered;
                    I[10]=(int)Nodes[5].rssi_filtered;
                    I[11]=(int)Nodes[6].rssi_filtered;
                    I[12]=(int)Nodes[7].rssi_filtered;
                    I[13]=(int)Nodes[8].rssi_filtered;
                    I[14]=(int)Nodes[9].rssi_filtered;

                    for (int i=0;i<=14;i++){
                        atemp[i]=I[i].byteValue();// convert Integer to byte
                    }
                    // write the characteristic
                    if (myService!=null){
                        myService.writeValue(atemp);
                    }
                    Log.i("writeData***",atemp.toString());
                }
            };
        }
        ThreadPoolManager.getInstance(ThreadPoolManager.Scheduled_ThreadPool_Executor_Type).scheduleAtFixedRate(writeDataToMasterRunnable,0,50,TimeUnit.MILLISECONDS);
    }

    private void removeRunnable(){
        if (writeDataToMasterRunnable != null){
            ThreadPoolManager.getInstance(ThreadPoolManager.Scheduled_ThreadPool_Executor_Type).remove(writeDataToMasterRunnable);
        }
        if (zoneTFRunnable!=null){
            ThreadPoolManager.getInstance(ThreadPoolManager.Scheduled_ThreadPool_Executor_Type).remove(zoneTFRunnable);
        }
        if(sportTFRunnable!=null){
            ThreadPoolManager.getInstance(ThreadPoolManager.Scheduled_ThreadPool_Executor_Type).remove(sportTFRunnable);
        }
    }


    public void setCMDCounter(int value) {
        //Set the send times
        CMDCounter = 6;
        //Specify the control value
        CMDValue = value;
    }

    private void readWrapRssiData() {
        Integer mainRssi = wrapRssiData.getMainRssi();
        int[] nodeRssi = wrapRssiData.getRssi();
        if (mainRssi != null){
            if (Nodes[0]==null) {
                Nodes[0]=new Node();
            }
            Nodes[0].saveRSSIMaster(Math.abs(mainRssi));
        }

        //Byte 2-11 current assist BLE RSSI
        for (int i=1;i<=10;i++) {
            if (Nodes[i] == null) {
                Nodes[i] = new Node();
            }
            Nodes[i].saveRSSI(-nodeRssi[i]);
        }



        if(Math.abs(Nodes[2].rssi_filtered+Nodes[3].rssi_filtered-Nodes[5].rssi_filtered-Nodes[6].rssi_filtered)>5) {
            if (Nodes[2].rssi_filtered + Nodes[3].rssi_filtered > Nodes[5].rssi_filtered + Nodes[6].rssi_filtered) {
                curLeftRight = 0;
            } else {
                curLeftRight = 1;
            }
        }
    }

    /**
     * 开始直连 并改变状态
     */
    public void startConnect(){
        Log.d(TAG,"start connect");
        if (myService != null){
            myService.scanBleDevice();
            setUIBleState(true,false);
        }
    }

    /**
     * 关闭直连 更新状态
     * @param canScan
     */
    public void stopConenct(boolean canScan){
        Log.d(TAG,"stop connect");
        if (myService !=null){
            if (canScan){
                setUIBleState(true,false);
                startConnect();
                blueState = 1;

            }else {
                myService.stopConnect();
                setUIBleState(false,false);
                blueState= 0;
            }
//            myService.stopAdvertise();
            myService.closeBlueGatt();
        }
        // inte node for next connection
        initNode();
        EventBus.getDefault().post(new BleDisconnectEvent(false));
        removeRunnable();
    }

    /**
     * @param bleScanState
     * @param bleConenctState
     */
    public void setUIBleState(boolean bleScanState,boolean bleConenctState){

        if(bleScanState){
            imgScan.setImageResource(R.drawable.greenicon);
        }else {
            imgScan.setImageResource(R.drawable.redicon2);
        }
        if (bleConenctState){
            imgConnect.setImageResource(R.drawable.greenicon);
        }else {
            imgConnect.setImageResource(R.drawable.redicon2);
        }
    }
}

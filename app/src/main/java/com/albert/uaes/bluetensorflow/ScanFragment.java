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
import com.albert.uaes.tensorflowlibrary.tf.PocketDetector;
import com.albert.uaes.tensorflowlibrary.tf.PredictionTF_motion;
import com.albert.uaes.tensorflowlibrary.tf.PredictionTF_zone;
import com.albert.uaes.tensorflowlibrary.model.Node;

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
    private PredictionTF_zone preTF_zone;

    public int CMDCounter;
    public int CMDValue;
    public static int curMotion=255,curZone=255,curLeftRight=255,curZone_filtered=255,curPocketState;

    public static float[] linearAccValue,gravityValue,gyroValue,acceleroValue;

    public static WrapRssiData wrapRssiData;


    public static Node[] Nodes=new Node[13];

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
        for (int i=0; i<switches.length; ++i) {
            switches[i] = true;
        }
        preTF_motion=new PredictionTF_motion(this.getContext().getAssets());
        preTF_zone = new PredictionTF_zone(this.getContext().getAssets());

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
                        preTF_zone.Storage(Nodes);
                        float[] outputs = preTF_zone.getPredict();
                        // find the maximum of output
                        float tempMax = -10;
                        int tempMaxNumber = 0;
                        for (int i = 0; i < 3; i++) {
                            if (outputs[i] > tempMax) {tempMax = outputs[i];tempMaxNumber = i; }
                        }
                        tempMaxNumber=tempMaxNumber+1;
                        // decide when to change the current zone
                        if ((outputs[0]>8&tempMaxNumber==1)
                                ||(outputs[1]>0&tempMaxNumber==2)
                                ||(outputs[2]>0)&tempMaxNumber==3){
                            curZone=tempMaxNumber;
                        }
                        // some strict conditions to change the zone
                        if(Nodes[0].rssi_filtered<58){
                            curZone=1;
                        }

                        for(int i=1;i<7;i++){
                            if (Nodes[i].rssi_filtered<55 & i!=3){
                                curZone=1;
                            }
                            if (Nodes[i].rssi_filtered<50 & i==3) {
                                curZone=1;
                            }
                        }

                        // determine whether the phone in the car
                        double max_inside=0;
                        int max_inside_node=0;

                        for (int i=7;i<=8;i++){
                            // find the maximal value of anchors inside the car and the mininal value of anchors outside the car,
                            // if the maximal value is still smaller than  minimal value, the phone is in the car
                            if (Nodes[i].rssi_filtered>max_inside){
                                max_inside_node=i;
                                max_inside=Nodes[i].rssi_filtered;
                            }
                        }

                        double min_outside=100;
                        int max_outside_node=0;

                        for (int i=1;i<=6;i++) {
                            if (Nodes[i].rssi_filtered < min_outside) {
                                max_outside_node = i;
                                min_outside = Nodes[i].rssi_filtered;
                            }
                        }
                        if (Nodes[0].rssi_filtered<=48 || (Nodes[0].rssi_filtered>48 &&(Nodes[7].rssi_filtered<=56 ||
                                Nodes[8].rssi_filtered<=56|| Nodes[9].rssi_filtered<=54)) ||
                                (max_inside<min_outside && distanceValue==0&&curZone<=1))
                        {
                            curZone=0;
                        }

                        // create a buffer for zone change
                        curZoneOutput=outputs;
                        for (int i=0;i<4;i++){
                            zonebuffer[i]=zonebuffer[i+1];

                        }
                        zonebuffer[4]=curZone;
                        int zonesum= zonebuffer[0]+zonebuffer[1]+zonebuffer[2]+zonebuffer[3]+zonebuffer[4];
                        if (zonesum==15 ||zonesum==5 ||zonesum==10||curZone==0){
                            curZone_filtered=curZone;
                        }
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
                    I[5]=(int)gravityValue[0]*10+100;
                    I[6]=(int)gravityValue[1]*10+100;
                    I[7]=(int)gravityValue[2]*10+100;

                    //I[5] = (int)Nodes[0].RSSI_filtered;
                    //I[6]=(int)Nodes[1].RSSI_filtered;
                    //I[7] = (int)Nodes[2].RSSI_filtered;
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
            imgScan.setImageResource(R.mipmap.greenicon);
        }else {
            imgScan.setImageResource(R.mipmap.redicon2);
        }
        if (bleConenctState){
            imgConnect.setImageResource(R.mipmap.greenicon);
        }else {
            imgConnect.setImageResource(R.mipmap.redicon2);
        }
    }
}

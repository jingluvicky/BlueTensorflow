package com.albert.uaes.bluetensorflow.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.albert.uaes.bluetensorflow.LocationOutputFragment;
import com.albert.uaes.bluetensorflow.MainActivity;
import com.albert.uaes.bluetensorflow.R;
import com.albert.uaes.bluetensorflow.ScanFragment;
import com.albert.uaes.bluetensorflow.SettingFragment;
import com.albert.uaes.bluetensorflow.utils.FileUtils;
import com.albert.uaes.bluetensorflow.utils.ThreadPoolManager;
import com.albert.uaes.tensorflowlibrary.model.Node;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MyService extends Service implements SensorEventListener {

    public final static String TAG = MyService.class.getName();

    public static String MAC_ADDRESS = "00:60:37:81:6C:57";
    private final  String TARGET_DEVICE = "SMART PEPS DEMO";


    private final IBinder mBinder = new LocalBinder();

    private BluetoothAdapter mbluetoothAdapter;
    private BluetoothManager mbluetoothManager;

    private PowerManager.WakeLock mWakeLock;

    private NotificationManager notifManager;

    public final static String ACTION_GATT_CONNECTED =
            "com.wangjing.processlive.service.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.wangjing.processlive.service.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_CONNECTING =
            "com.wangjing.processlive.service.ACTION_GATT_DISCONNECTING";
    public final static String  ACTION_GATT_DISCONNECTING =
            "com.wangjing.processlive.service.ACTION_GATT_DISCONNECTING";
    public final static String  ACTION_GATT_SERVICE_DISCOVERED =
            "com.wangjing.processlive.service.ACTION_GATT_SERVICE_DISCOVERED";

    public final static String ACTION_RSSI_VALUE =
            "com.wangjing.processlive.service.ACTION_RSSI_VALUE";


    private final String NEW_WRITE_UUID = "02362A10-CF3A-11E1-EFDE-0002A5D5C51B";
    private final String INDCATE_UUID ="02362A11-CF3A-11E1-EFDE-0002A5D5C51B";
    private final String CYCLE_INDCATE_UUID ="02362A13-CF3A-11E1-EFDE-0002A5D5C51B";


    private final String service_uuid = "0000fff0-0000-1000-8000-00805f9b34fb";
    private final String charac_uuid =  "0000fff4-0000-1000-8000-00805f9b34fb";
    private final String WRITE_UUID =  "0000fff3-0000-1000-8000-00805f9b34fb";
    private final static UUID config_uuid =  UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final int GET_BET_DATA = 0xabc0001;

    public static final int BLE_ADVERTISE_START = 0xabc0004;

    public static final int BLE_ADVERTISE_CHANGE = 0xabc0006;

    public static final int BLE_OLD_ADVERTISE_START = 0xabc0007;

    public static final int BLE_OLD_ADVERTISE_STOP = 0xabc0008;

    public static final int START_RECORD = 0xabc0009;



    private List<BluetoothDevice> bluetoothDevices;

    private File file,connectFile;
    private FileOutputStream fos;
    private FileOutputStream connectFos;

    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattService mbluetoothgattservice;
    private BluetoothGattCharacteristic mbluetoothgattcharacteristic;
    private BluetoothGattCharacteristic mBluetoothGattCharacNotify;
    private BluetoothLeScanner mbluetoothlescanner;

    private AdvertiseData advertiseData;
    private AdvertisingSet currentAdvertisingSet;

    public byte[] dataReceived=new byte[13];
    public WrapRssiData wrapRssiData;


    private SensorManager sensorManager;
    private Sensor sensor;
    public float[] RMatrix=new float[9];
    public float walk;
    public float[] orientationValue;
    //加速度传感器 x y z m/s^2
    public float[] accelerometerValues;
    //陀螺仪 rad/s x y z
    public float[] gyroscopeValues;
    //磁场强度   x y z uH
    public float[] magneticValues;
    //重力传感器 x y z m/s^2
    public float[] gravityValues;
    //障碍物距离 cm
    public float proximityValues;
    //光强  lx
    public float lightValues;
    //方向 x y z m/s^2
    public float[] rotationValues;
    //线性加速度传感器 x y z
    public float[] linearaccelerationValues;

    private static final int FILTERWND = 10;
    public float[][] gravityBuffer;
    public float[]   proximityBuffer;
    public float[] filteredGravity;
    public float  filteredProximity;
    private Integer mainRssi;

    private Long startTimestamp = 0L;
    public long serialNumber;
    //1--scanning 2--connected 0--none
    private int blueState;

    //设置蓝牙扫描过滤器集合
    private List<ScanFilter> scanFilterList;
    //设置蓝牙扫描过滤器
    private ScanFilter.Builder scanFilterBuilder;
    //设置蓝牙扫描设置
    private ScanSettings.Builder  scanSettingBuilder;

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BluetoothDevice device = result.getDevice();
            Log.d(TAG, "onScanResult: "+device.getName() +":"+ device.getAddress());


            if(device.getName()!= null && device.getName().equals(TARGET_DEVICE)) {
                connectBle(device.getAddress());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


//    private AdvertisingSetCallback advertisingSetCallback = new AdvertisingSetCallback() {
//        @Override
//        public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
//            super.onAdvertisingSetStarted(advertisingSet, txPower, status);
//            Log.i(TAG, "onAdvertisingSetStarted(): txPower:" + txPower + " , status: "
//                    + status);
//            currentAdvertisingSet = advertisingSet;
//        }
//
//        @Override
//        public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
//            Log.i(TAG, "onAdvertisingSetStopped():");
//            super.onAdvertisingSetStopped(advertisingSet);
//        }
//
//        @Override
//        public void onAdvertisingDataSet(AdvertisingSet advertisingSet, int status) {
//            super.onAdvertisingDataSet(advertisingSet, status);
//            Log.i(TAG, "onAdvertisingDataSet() :status:" + status);
//        }
//
//        @Override
//        public void onScanResponseDataSet(AdvertisingSet advertisingSet, int status) {
//            super.onScanResponseDataSet(advertisingSet, status);
//        }
//    };

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            if (settingsInEffect != null) {
                Log.d(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode()
                        + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.e(TAG, "onStartSuccess, settingInEffect is null");
            }
            Log.e(TAG, "onStartSuccess settingsInEffect" + settingsInEffect);

        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.e(TAG, "onStartFailure errorCode" + errorCode);

            if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                //	Toast.makeText(Main4Activity.this, "R.string.advertise_failed_data_too_large", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
            } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
                //Toast.makeText(Main4Activity.this, "R.string.advertise_failed_too_many_advertises", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to start advertising because no advertising instance is available.");
            } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
                //	Toast.makeText(Main4Activity.this, "R.string.advertise_failed_already_started", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to start advertising as the advertising is already started");
            } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
                //Toast.makeText(Main4Activity.this, "R.string.advertise_failed_internal_error", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Operation failed due to an internal error");
            } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
                //	Toast.makeText(Main4Activity.this, "R.string.advertise_failed_feature_unsupported", Toast.LENGTH_LONG).show();
                Log.e(TAG, "This feature is not supported on this platform");
            }
        }
    };



    public Handler handlerConnect = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_BET_DATA:
//                    connectBle();
                    scanBleDevice();
                    break;
                case BLE_ADVERTISE_START:
                    //                   startAdvertise();
                    break;
                case BLE_ADVERTISE_CHANGE:
//                    chageAdvertiseData();
                    break;
                case BLE_OLD_ADVERTISE_START:
                    startOldAdvertise();
                    break;
                case BLE_OLD_ADVERTISE_STOP:
                    stopOldAdvertise();
                    break;

                default:
                    break;
            }
        }
    };

    public void stopConnect() {
        handlerConnect.removeMessages(GET_BET_DATA);
        handlerConnect.removeMessages(BLE_OLD_ADVERTISE_START);
        handlerConnect.removeMessages(BLE_OLD_ADVERTISE_STOP);
        blueState = 0;//停止扫描
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        initSensorDataGroup();

        try {
            SimpleDateFormat format = new SimpleDateFormat("MM.dd HH_mm_ss");
            file = FileUtils.getInstance().createFile(format.format(new Date()) + ".txt");
            connectFile = FileUtils.getInstance().createFile(format.format(new Date())+"connect.txt");
            connectFos = new FileOutputStream(connectFile);
            writeConnectDataTofile(MAC_ADDRESS+"onConnectionStateChange: Disconnecting");

            fos = new FileOutputStream(file);
            writeSensorDataTofile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (SettingFragment.isRecord){

        }
        bluetoothDevices = new ArrayList<>();

//        initialize();

        notification("bluetooth is scanning");

        getLock(this);

    }

    private void initSensorDataGroup() {
        //加速度传感器 x y z m/s^2
        accelerometerValues=new float[3];
        //陀螺仪 rad/s x y z
        gyroscopeValues=new float[3];
        //磁场强度   x y z uH
        magneticValues=new float[3];
        //重力传感器 x y z m/s^2
        gravityValues=new float[3] ;
        orientationValue=new float[3];
        //障碍物距离 cm
        proximityValues=0 ;
        //光强  lx
        lightValues=0;
        //方向 x y z m/s^2
        rotationValues=new float[3] ;
        //线性加速度传感器 x y z
        linearaccelerationValues=new float[3] ;

        gravityBuffer=new float[50][3] ;
        proximityBuffer=new float[50];
        filteredGravity=new float[3] ;
        filteredProximity=5;
    }

    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    public void scanBleDevice(){
        mbluetoothlescanner = mbluetoothAdapter.getBluetoothLeScanner();

        Set<BluetoothDevice> bluetoothDeviceSet = mbluetoothAdapter.getBondedDevices();

        if (bluetoothDeviceSet.isEmpty()){
            mbluetoothlescanner.startScan(buildScanFilters(), buildScanSettings(), scanCallback);
        }else {
            Iterator<BluetoothDevice> it = bluetoothDeviceSet.iterator();
            boolean isConnected = false;
            while (it.hasNext()) {
                BluetoothDevice bluetoothDevice = it.next();
                String tempName=bluetoothDevice.getName();
                if (bluetoothDevice.getName()!=null){
                    if(bluetoothDevice.getName().equals(TARGET_DEVICE)){
                        connectBle(bluetoothDevice.getAddress());
                        isConnected = true;
                        handlerConnect.sendEmptyMessage(BLE_OLD_ADVERTISE_START);

                        break;
                    }
                }
            }
            if (!isConnected){
                mbluetoothlescanner.startScan(buildScanFilters(), buildScanSettings(), scanCallback);
            }
        }
    }

    private List<ScanFilter> buildScanFilters() {
        scanFilterList = new ArrayList<>();
        // 通过服务 uuid 过滤自己要连接的设备   过滤器搜索GATT服务UUID
        scanFilterBuilder = new ScanFilter.Builder();
//        ParcelUuid parcelUuidMask = ParcelUuid.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");
//        ParcelUuid parcelUuid = ParcelUuid.fromString(service_uuid);
//        scanFilterBuilder.setServiceUuid(parcelUuid, parcelUuidMask);
//        scanFilterBuilder.setDeviceAddress("00:60:37:68:11:C2");
//        scanFilterBuilder.setDeviceName(TARGET_DEVICE);
        scanFilterList.add(scanFilterBuilder.build());
        return scanFilterList;
    }

    private ScanSettings buildScanSettings() {
        scanSettingBuilder = new ScanSettings.Builder();
        //设置蓝牙LE扫描的扫描模式。
        scanSettingBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        //设置蓝牙LE扫描滤波器硬件匹配的匹配模式
        //在主动模式下，即使信号强度较弱，hw也会更快地确定匹配.在一段时间内很少有目击/匹配。
        scanSettingBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
        //设置蓝牙LE扫描的回调类型
        //为每一个匹配过滤条件的蓝牙广告触发一个回调。如果没有过滤器是活动的，所有的广告包被报告
        scanSettingBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        return scanSettingBuilder.build();
    }

    /**
     * connect ble
     * @param macAddress
     */
    public void connectBle(String macAddress) {
        BluetoothDevice bluetoothDevice = mbluetoothAdapter.getRemoteDevice(macAddress);
        if (bluetoothDevice != null){
            Log.d("******",bluetoothDevice.getAddress());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                mBluetoothGatt = bluetoothDevice.connectGatt(this, false, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        if (newState == BluetoothGatt.STATE_CONNECTING){
                            Log.d("--->", "onConnectionStateChange: Connecting");
                            broadcastUpdate(ACTION_GATT_CONNECTING);

                        } else if(newState == BluetoothGatt.STATE_CONNECTED) {
                            Log.d("--->", "onConnectionStateChange: Connected");

//                            handlerConnect.removeMessages(GET_BET_DATA);
                            broadcastUpdate(ACTION_GATT_CONNECTED);

                            blueState = 2;

                            mbluetoothlescanner.stopScan(scanCallback);

//                            handlerConnect.sendEmptyMessage(BLE_ADVERTISE_START);

                            //8.0以下动态更改广播内容
                            handlerConnect.sendEmptyMessage(BLE_OLD_ADVERTISE_START);

                            gatt.discoverServices();

                        } else if(newState == BluetoothGatt.STATE_DISCONNECTED) {
                            Log.d("--->", "onConnectionStateChange: Disconnect");

//                            handlerConnect.sendEmptyMessageDelayed(GET_BET_DATA,5000);

//                            handlerConnect.removeMessages(BLE_ADVERTISE_START);

                            broadcastUpdate(ACTION_GATT_DISCONNECTED);

                            handlerConnect.removeMessages(BLE_OLD_ADVERTISE_START);
                            handlerConnect.removeMessages(BLE_OLD_ADVERTISE_STOP);

                            //mbluetoothlescanner.startScan(scanCallback);

                        }else if (newState == BluetoothGatt.STATE_DISCONNECTING){
                            Log.d("--->", "onConnectionStateChange: Disconnecting");
                            broadcastUpdate(ACTION_GATT_DISCONNECTING);
//                            writeConnectDataTofile(MAC_ADDRESS+"onConnectionStateChange: Disconnecting");
                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        super.onServicesDiscovered(gatt, status);

                        Log.d(TAG, "onServicesDiscovered: 发现服务");
                        mbluetoothgattservice = mBluetoothGatt.getService(UUID.fromString(service_uuid));
                        Log.d(TAG, "onServicesDiscovered: " + mbluetoothgattservice.getUuid().toString());
                        mbluetoothgattcharacteristic = mbluetoothgattservice.getCharacteristic(UUID.fromString(WRITE_UUID));
                        mBluetoothGattCharacNotify=mbluetoothgattservice.getCharacteristic(UUID.fromString(charac_uuid));
                        if(mBluetoothGattCharacNotify != null) {
                            setCharacteristicNotification(mBluetoothGattCharacNotify, true);
                            Log.d(TAG, "onEnableNotificationNotify: 使能成功"+mBluetoothGattCharacNotify.getUuid().toString());
                        } else {
                            Log.d(TAG, "onServicesDiscoveredNotify: 发现服务失败");
                        }


                        if(mbluetoothgattcharacteristic!=null) {
                            //广播发现写入服务，通知
                            broadcastUpdate(ACTION_GATT_SERVICE_DISCOVERED);
                            Log.d(TAG, "onEnableNotificationWrite: 使能成功"+mbluetoothgattcharacteristic.getUuid().toString());
                        }else {
                            Log.d(TAG, "onServicesDiscoveredWrite: 发现服务失败");
                        }

                    }

                    @Override
                    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        super.onCharacteristicRead(gatt, characteristic, status);
                    }

                    @Override
                    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        super.onCharacteristicWrite(gatt, characteristic, status);
                        Log.i("writeData***receive",characteristic.getValue().toString());

                    }

                    @Override
                    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                        super.onCharacteristicChanged(gatt, characteristic);
                        dataReceived=characteristic.getValue();
                        Log.d(TAG,"Data received"+dataReceived[1]);
                        // TODO: 2019/4/9 变化后的操作
                        if (gatt.readRemoteRssi()){
                            broadcastRssiData(ACTION_RSSI_VALUE,mainRssi,dataReceived);
                        }
                    }

                    @Override
                    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                        super.onDescriptorRead(gatt, descriptor, status);
                    }

                    @Override
                    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                        super.onDescriptorWrite(gatt, descriptor, status);
                    }

                    @Override
                    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                        super.onReadRemoteRssi(gatt, rssi, status);
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            // Log.i("remomteRssi***receive",rssi+"");
                            mainRssi = rssi;
                        }else {
                            // Log.i("remomteRssi***receive",status+""+rssi+"");
                        }
                    }

                    @Override
                    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                        super.onMtuChanged(gatt, mtu, status);
                    }
                },BluetoothDevice.TRANSPORT_LE);

            }
        }
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastRssiData(final String action,int mainRssi, byte[] dataReceived){
        final Intent intent = new Intent(action);
        int[]dataReceivedInt=new int[15];
        for (int i=0;i<dataReceived.length;i++){
            dataReceivedInt[i]=(int)dataReceived[i];
        }
        WrapRssiData wrapRssiData = new WrapRssiData(mainRssi,dataReceivedInt);
        intent.putExtra("data",wrapRssiData);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseLock();
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void notification(String title) {
        final int NOTIFY_ID = 1003;
        String name = "IBC_SERVICE_CHANNEL";
        // The user-visible name of the channel.
        String id = "IBC_SERVICE_CHANNEL_1";
        // The user-visible description of the channel.
        String description = "IBC_SERVICE_CHANNEL_SHOW";

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        if (notifManager == null) {
            notifManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(false);
                mChannel.enableLights(false);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this);
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(title)
                    .setSmallIcon(R.mipmap.ico)
                    .setContentText(this.getString(R.string.app_name))
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setChannelId(id)
                    .setTicker(title);
            builder.build().sound = null;
            builder.build().vibrate = null;
        } else {
            builder = new NotificationCompat.Builder(this);
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            builder.setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(this.getString(R.string.app_name))
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(title)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVibrate(new long[]{0L});
        }
        Notification notification = builder.build();
        notification.sound = null;
        notification.vibrate = null;
        startForeground(NOTIFY_ID, notification);
    }


    synchronized private void getLock(Context context){
        if(mWakeLock==null){
            PowerManager mgr=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
            mWakeLock=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,MyService.class.getName());
            mWakeLock.setReferenceCounted(true);
            Calendar c=Calendar.getInstance();
            c.setTimeInMillis((System.currentTimeMillis()));
            int hour =c.get(Calendar.HOUR_OF_DAY);
            if(hour>=23||hour<=6){
                mWakeLock.acquire(5000);
            }else{
                mWakeLock.acquire(300000);
            }
        }
        Log.v(TAG,"get lock");
    }

    synchronized private void releaseLock() {
        if(mWakeLock!=null){
            if(mWakeLock.isHeld()) {
                mWakeLock.release();
                Log.v(TAG,"release lock");
            }

            mWakeLock=null;
        }
        handlerConnect.removeMessages(GET_BET_DATA);
        handlerConnect.removeMessages(BLE_ADVERTISE_START);
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void startAdvertise(){
//        advertiseData = createAdvertiseData();
//        mbluetoothAdapter.getBluetoothLeAdvertiser().startAdvertisingSet(createAdvertisingSetParameters(),advertiseData,null,null,null,advertisingSetCallback);
//        handlerConnect.sendEmptyMessageDelayed(BLE_ADVERTISE_CHANGE,100);
//    }


    private void startOldAdvertise(){
        advertiseData = createAdvertiseData();
        mbluetoothAdapter.getBluetoothLeAdvertiser().startAdvertising(createAdvSettings(false, 0),advertiseData, mAdvertiseCallback);
        handlerConnect.sendEmptyMessageDelayed(BLE_OLD_ADVERTISE_STOP,1000);
    }

    public void stopOldAdvertise(){
        if (mbluetoothAdapter!=null && mbluetoothAdapter.isEnabled()){
            mbluetoothAdapter.getBluetoothLeAdvertiser().stopAdvertising(mAdvertiseCallback);
            handlerConnect.sendEmptyMessage(BLE_OLD_ADVERTISE_START);
//            handlerConnect.removeMessages(BLE_OLD_ADVERTISE_START);
//            handlerConnect.removeMessages(BLE_OLD_ADVERTISE_STOP);
        }
    }


//    public void stopAdvertise(){
//        if (mbluetoothAdapter!=null && mbluetoothAdapter.isEnabled()){
//            mbluetoothAdapter.getBluetoothLeAdvertiser().stopAdvertisingSet(advertisingSetCallback);
//            handlerConnect.removeMessages(BLE_ADVERTISE_CHANGE);
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void chageAdvertiseData(){
        advertiseData = createAdvertiseData();
        if (currentAdvertisingSet != null){
            currentAdvertisingSet.setAdvertisingData(advertiseData);
        }
        handlerConnect.sendEmptyMessageDelayed(BLE_ADVERTISE_CHANGE,100);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AdvertisingSetParameters createAdvertisingSetParameters(){
        AdvertisingSetParameters advertisingSetParameters = new AdvertisingSetParameters.Builder()
                .setLegacyMode(true)
                .setConnectable(false)
                .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_HIGH)
                .setInterval(AdvertisingSetParameters.INTERVAL_LOW)
                .build();
        return advertisingSetParameters;
    }

    public AdvertiseSettings createAdvSettings(boolean connectAble, int timeoutMillis) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        builder.setConnectable(connectAble);
        builder.setTimeout(timeoutMillis);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        AdvertiseSettings mAdvertiseSettings = builder.build();
        if (mAdvertiseSettings == null) {
            Toast.makeText(this, "mAdvertiseSettings == null", Toast.LENGTH_LONG).show();
            Log.e(TAG, "mAdvertiseSettings == null");
        }
        return mAdvertiseSettings;
    }

    public AdvertiseData createAdvertiseData() {

        int num = new Random().nextInt(10) + 1;

        AdvertiseData.Builder mDataBuilder = new AdvertiseData.Builder();
        //广播名称也需要字节长度
        mDataBuilder.setIncludeDeviceName(true);
        mDataBuilder.setIncludeTxPowerLevel(true);
        mDataBuilder.addServiceData(ParcelUuid.fromString("0000fff0-0000-1000-8000-00805f9b34fb"),new byte[]{(byte) num,(byte)(num+1)});
        AdvertiseData mAdvertiseData = mDataBuilder.build();
        if (mAdvertiseData == null) {
            Log.e(TAG, "mAdvertiseSettings == null");
        }
        Log.e("*******",num+"");
        return mAdvertiseData;
    }


    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mbluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        boolean isEnableNotification =  mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if(isEnableNotification) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(config_uuid);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public void closeBlueGatt() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
    }

    public int initialize() {
        if (mbluetoothManager == null) {
            mbluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mbluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return 0;
            }
        }
        mbluetoothAdapter = mbluetoothManager.getAdapter();
        if (mbluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return 0;
        }

        if (mbluetoothAdapter.isEnabled()){
            return 1;
        }else {
            return 2;
        }
    }

    public void writeConnectDataTofile(final String content){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("<------>","recordConnectData start");
                byte [] buffer = (content+"\n").getBytes();
                try {
                    connectFos.write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("<------>","recordConnectData end");
            }
        }).start();

    }

    public void writeSensorDataTofile(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if (SettingFragment.isRecord) {
                        Log.d("<------>", "record start");
                        if (startTimestamp == 0) {
                            startTimestamp = System.currentTimeMillis();
                        }

                        long tTemp = System.currentTimeMillis() - startTimestamp;
                        //serialNumber|Time|
                        String ss = Long.toString(serialNumber) + '\t'; //SerialNumber
                        ss += Long.toString(tTemp) + "\t"; //Timestamp
                        //加速度传感器 x y z m/s^2
                        ss += Float.toString(accelerometerValues[0]) + '\t' + Float.toString(accelerometerValues[1]) + '\t' + Float.toString(accelerometerValues[2]) + '\t';
                        //陀螺仪 rad/s x y z
                        ss += Float.toString(gyroscopeValues[0]) + '\t' + Float.toString(gyroscopeValues[1]) + '\t' + Float.toString(gyroscopeValues[2]) + '\t';
                        //磁场强度   x y z uH
                        ss += Float.toString(magneticValues[0]) + '\t' + Float.toString(magneticValues[1]) + '\t' + Float.toString(magneticValues[2]) + '\t';
                        //重力传感器 x y z m/s^2
                        ss += Float.toString(gravityValues[0]) + '\t' + Float.toString(gravityValues[1]) + '\t' + Float.toString(gravityValues[2]) + '\t';
                        //障碍物距离 cm
                        ss += Float.toString(proximityValues) + '\t';
                        //光强  lx
                        ss += Float.toString(lightValues) + '\t';
                        //方向 x y z m/s^2
                        ss += Float.toString(rotationValues[0]) + '\t' + Float.toString(rotationValues[1]) + '\t' + Float.toString(rotationValues[2]) + '\t';
                        //线性加速度传感器 x y z
                        ss += Float.toString(linearaccelerationValues[0]) + '\t' + Float.toString(linearaccelerationValues[1]) + '\t' + Float.toString(linearaccelerationValues[2]) + '\t';
                        //主蓝牙模块场强
                        ss += Integer.toString((int) ScanFragment.Nodes[0].rssi_filtered) + '\t';
                        //辅模块1场强
                        ss += Integer.toString((int) ScanFragment.Nodes[1].rssi_filtered) + '\t';
                        //辅模块2场强
                        ss += Integer.toString((int) ScanFragment.Nodes[2].rssi_filtered) + '\t';
                        //辅模块3场强
                        ss += Integer.toString((int) ScanFragment.Nodes[3].rssi_filtered) + '\t';
                        //辅模块4场强
                        ss += Integer.toString((int) ScanFragment.Nodes[4].rssi_filtered) + '\t';
                        //辅模块5场强
                        ss += Integer.toString((int) ScanFragment.Nodes[5].rssi_filtered) + '\t';
                        //辅模块6场强
                        ss += Integer.toString((int) ScanFragment.Nodes[6].rssi_filtered) + '\t';
                        //辅模块7场强
                        ss += Integer.toString((int) ScanFragment.Nodes[7].rssi_filtered) + '\t';
                        //辅模块8场强
                        ss += Integer.toString((int) ScanFragment.Nodes[8].rssi_filtered) + '\t';
                        //辅模块9场强
                        ss += Integer.toString((int) ScanFragment.Nodes[9].rssi_filtered) + '\t';
                        //辅模块2场强
                        ss+=Integer.toString(SettingFragment.uwbZone)+'\t';
                        ss+=Integer.toString(SettingFragment.sensorTag)+ '\n';
                        //Zone
                        byte[] buffer = ss.getBytes();
                        Log.d(TAG," write to folie");
                        try {
                            fos.write(buffer);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Log.d(TAG, "stop write to folie");
                    }
                try{
                    Thread.sleep(20);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                Log.d("<------>","recordConnectData end");
            }
            }
        }).start();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues[0]=event.values[0];
                accelerometerValues[1]=event.values[1];
                accelerometerValues[2]=event.values[2];
                SensorManager.getRotationMatrix(RMatrix,null,accelerometerValues,magneticValues);
                SensorManager.getOrientation(RMatrix,orientationValue);
                orientationValue[0]=(float)Math.toDegrees(orientationValue[0])+180;

                ScanFragment.acceleroValue=accelerometerValues;
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroscopeValues[0]=event.values[0];
                gyroscopeValues[1]=event.values[1];
                gyroscopeValues[2]=event.values[2];

                ScanFragment.gyroValue=gyroscopeValues;
                break;
            case Sensor.TYPE_STEP_COUNTER:
                walk=event.values[0];
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticValues[0]=event.values[0];
                magneticValues[1]=event.values[1];
                magneticValues[2]=event.values[2];
                break;
            case Sensor.TYPE_GRAVITY:
                gravityValues[0]=event.values[0];
                gravityValues[1]=event.values[1];
                gravityValues[2]=event.values[2];
                ScanFragment.gravityValue=gravityValues;

                for(int i=FILTERWND-1;i>0;i--)
                {
                    //x
                    gravityBuffer[i][0]=gravityBuffer[i-1][0];
                    //y
                    gravityBuffer[i][1]=gravityBuffer[i-1][1];
                    //z
                    gravityBuffer[i][2]=gravityBuffer[i-1][2];

                }
                gravityBuffer[0][0]=event.values[0];
                gravityBuffer[0][1]=event.values[1];
                gravityBuffer[0][2]=event.values[2];

                break;
            case Sensor.TYPE_PROXIMITY:
                proximityValues=event.values[0];
                for(int i=FILTERWND-1;i>0;i--)
                {
                    proximityBuffer[i]=proximityBuffer[i-1];

                }
                proximityBuffer[0]=event.values[0];


                ScanFragment.distanceValue=proximityValues;
                break;
            case Sensor.TYPE_LIGHT:
                lightValues=event.values[0];
                ScanFragment.light=lightValues;
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                rotationValues[0]=event.values[0];
                rotationValues[1]=event.values[1];
                rotationValues[2]=event.values[2];
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                linearaccelerationValues[0]=event.values[0];
                linearaccelerationValues[1]=event.values[1];
                linearaccelerationValues[2]=event.values[2];

                ScanFragment.linearAccValue=linearaccelerationValues;
                break;

            default:
                break;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public int getBlueState() {
        return blueState;
    }

    public void setBlueState(int blueState) {
        this.blueState = blueState;
    }

    public void setSensors(boolean[] switches) {
        Log.d("Func", "setSensors()");
        for (int i=0; i<switches.length; ++i){
            if (switches[i]) {
                switch(i) {
                    case 0:
                        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                        break;
                    case 1:
                        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                        break;
                    case 2:
                        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                        break;
                    case 3:
                        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
                        break;
                    case 4:
                        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                        break;
                    case 5:
                        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
                        break;
                    case 6:
                        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                        break;
                    case 7:
                        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                        break;
                    default:
                        break;
                }
                sensorManager.registerListener(MyService.this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }
        Log.d("Func", "setSensors() Finish");
    }




    public void writeValue(byte[] atemp) {
        mbluetoothgattcharacteristic.setValue(atemp);
        if (mBluetoothGatt!=null){
            mBluetoothGatt.writeCharacteristic(mbluetoothgattcharacteristic);
        }
    }
}

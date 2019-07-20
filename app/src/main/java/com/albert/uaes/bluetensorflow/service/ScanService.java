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
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.albert.uaes.bluetensorflow.MainActivity;
import com.albert.uaes.bluetensorflow.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.albert.uaes.bluetensorflow.service.MyService.ACTION_GATT_CONNECTED;
import static com.albert.uaes.bluetensorflow.service.MyService.ACTION_GATT_CONNECTING;
import static com.albert.uaes.bluetensorflow.service.MyService.ACTION_GATT_DISCONNECTED;
import static com.albert.uaes.bluetensorflow.service.MyService.ACTION_GATT_DISCONNECTING;
import static com.albert.uaes.bluetensorflow.service.MyService.ACTION_GATT_SERVICE_DISCOVERED;
import static com.albert.uaes.bluetensorflow.service.MyService.BLE_OLD_ADVERTISE_START;
import static com.albert.uaes.bluetensorflow.service.MyService.BLE_OLD_ADVERTISE_STOP;
import static com.albert.uaes.bluetensorflow.service.MyService.GET_BET_DATA;

public class ScanService extends Service {

    public final static String TAG = ScanService.class.getName();
    private final  String TARGET_DEVICE = "SMART PEPS DEMO";

    private final IBinder mBinder = new LocalBinder();

    private MyHandler myHandler;

    private BluetoothGatt mBluetoothGatt;
    private BluetoothLeScanner mbluetoothlescanner;
    private BluetoothAdapter mbluetoothAdapter;
    private BluetoothManager mbluetoothManager;
    //设置蓝牙扫描过滤器集合
    private List<ScanFilter> scanFilterList;
    //设置蓝牙扫描过滤器
    private ScanFilter.Builder scanFilterBuilder;
    //设置蓝牙扫描设置
    private ScanSettings.Builder  scanSettingBuilder;

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


    private NotificationManager notifManager;

    public class LocalBinder extends Binder {
        public ScanService getService() {
            return ScanService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myHandler = new MyHandler(this);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        initSensorDataGroup();

        notification("bluetooth is scanning");

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

    private void initSensorDataGroup() {
        //加速度传感器 x y z m/s^2
        accelerometerValues=new float[3] ;
        //陀螺仪 rad/s x y z
        gyroscopeValues=new float[3] ;
        //磁场强度   x y z uH
        magneticValues=new float[3] ;
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


    public void scanBleDevice(){
        mbluetoothlescanner = mbluetoothAdapter.getBluetoothLeScanner();

        mbluetoothlescanner.startScan(buildScanFilters(), buildScanSettings(), new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice bluetoothDevice = result.getDevice();
                if (bluetoothDevice.getName().equals(TARGET_DEVICE)){
                    connectBle(bluetoothDevice);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        });
    }

    public void connectBle(BluetoothDevice bluetoothDevice) {
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

//                            writeConnectDataTofile(MAC_ADDRESS+"onConnectionStateChange: Connecting");

                        } else if(newState == BluetoothGatt.STATE_CONNECTED) {
                            Log.d("--->", "onConnectionStateChange: Connected");

                            broadcastUpdate(ACTION_GATT_CONNECTED);

                            //8.0以下动态更改广播内容
                            myHandler.sendEmptyMessage(BLE_OLD_ADVERTISE_START);

                            gatt.discoverServices();

                        } else if(newState == BluetoothGatt.STATE_DISCONNECTED) {
                            Log.d("--->", "onConnectionStateChange: Disconnect");

//                            myHandler.sendEmptyMessageDelayed(GET_BET_DATA,5000);
//                            writeConnectDataTofile(MAC_ADDRESS+"onConnectionStateChange: Disconnect");

                            broadcastUpdate(ACTION_GATT_DISCONNECTED);

                            myHandler.removeMessages(BLE_OLD_ADVERTISE_START);
                            myHandler.removeMessages(BLE_OLD_ADVERTISE_STOP);

                        }else if (newState == BluetoothGatt.STATE_DISCONNECTING){
                            Log.d("--->", "onConnectionStateChange: Disconnecting");
                            broadcastUpdate(ACTION_GATT_DISCONNECTING);
//                            writeConnectDataTofile(MAC_ADDRESS+"onConnectionStateChange: Disconnecting");
                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        super.onServicesDiscovered(gatt, status);

//                        Log.d(TAG, "onServicesDiscovered: 发现服务");
//                        mbluetoothgattservice = mBluetoothGatt.getService(UUID.fromString(service_uuid));
//                        Log.d(TAG, "onServicesDiscovered: " + mbluetoothgattservice.getUuid().toString());
////                        mbluetoothgattservice.getCharacteristics().get(0).get
//                        mbluetoothgattcharacteristic = mbluetoothgattservice.getCharacteristic(UUID.fromString(WRITE_UUID));
//                        mBluetoothGattCharacNotify=mbluetoothgattservice.getCharacteristic(UUID.fromString(charac_uuid));
//                        if(mBluetoothGattCharacNotify != null) {
//                            setCharacteristicNotification(mBluetoothGattCharacNotify, true);
//                            Log.d(TAG, "onEnableNotificationNotify: 使能成功"+mBluetoothGattCharacNotify.getUuid().toString());
//                        } else {
//                            Log.d(TAG, "onServicesDiscoveredNotify: 发现服务失败");
//                        }
//
//
//                        if(mbluetoothgattcharacteristic!=null) {
//                            //广播发现写入服务，通知
//                            broadcastUpdate(ACTION_GATT_SERVICE_DISCOVERED);
//                            Log.d(TAG, "onEnableNotificationWrite: 使能成功"+mbluetoothgattcharacteristic.getUuid().toString());
//                        }else {
//                            Log.d(TAG, "onServicesDiscoveredWrite: 发现服务失败");
//                        }

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
//                        dataReceived=characteristic.getValue();
//                        // TODO: 2019/4/9 变化后的操作
//                        if (gatt.readRemoteRssi()){
//                            broadcastRssiData(ACTION_RSSI_VALUE,mainRssi,dataReceived);
//                        }
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
                            Log.i("remomteRssi***receive",rssi+"");
//                            mainRssi = rssi;
                        }else {
                            Log.i("remomteRssi***receive",status+""+rssi+"");
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

    public static class MyHandler extends Handler {
        private final WeakReference<ScanService> scanServiceWeakReference;
        public MyHandler(ScanService scanService) {
            scanServiceWeakReference = new WeakReference<ScanService>(scanService);
        }

        @Override
        public void handleMessage(Message msg) {
            ScanService scanService = scanServiceWeakReference.get();
            if (scanService != null) {

            }
        }
    }

}

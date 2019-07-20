package com.albert.uaes.tensorflowlibrary.admin;

import android.content.Context;

import com.albert.uaes.tensorflowlibrary.tf.PredictionTF_motion;
import com.albert.uaes.tensorflowlibrary.tf.PredictionTF_zone;

/**
 * buider 类用于设置数据 和 获取机器学习算法结果
 */
public abstract class PEPSBuilder {

    protected BT_Location_Info bt_location_info = new BT_Location_Info();

    public abstract void setRssi(int[] rssi);

    public abstract void setPredictionTF_motion(PredictionTF_motion predictionTF_motion);
    public abstract void setPredictionTF_zone(PredictionTF_zone predictionTF_zone);


    public abstract void setGyroscopeValue(float[][] gyroscopeValue);

    public abstract void setAccelerometerValue(float[][] accelerometerValue);

    public abstract void setLinearAccelerationValue(float[][] linearAccelerationValue);

    public abstract void setProximityValue(float proximityValue);

    public abstract void setLightValue(float lightValue);

    public abstract void setGravityValue(float[][] gravityValue);

    public abstract void setRessiValue(int[][] ressiValue);


    public abstract int getMotionState(Context context);
    public abstract int getZoneState(Context context);
}

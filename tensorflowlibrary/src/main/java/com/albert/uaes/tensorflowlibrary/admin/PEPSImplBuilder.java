package com.albert.uaes.tensorflowlibrary.admin;

import android.content.Context;

import com.albert.uaes.tensorflowlibrary.tf.PredictionTF_motion;
import com.albert.uaes.tensorflowlibrary.tf.PredictionTF_zone;

public class PEPSImplBuilder extends PEPSBuilder {


    @Override
    public void setRssi(int[] rssi) {
        bt_location_info.setRssi(rssi);
    }

    @Override
    public void setPredictionTF_motion(PredictionTF_motion predictionTF_motion) {
        bt_location_info.setPredictionTF_motion(predictionTF_motion);
    }

    @Override
    public void setPredictionTF_zone(PredictionTF_zone predictionTF_zone) {
        bt_location_info.setPredictionTF_zone(predictionTF_zone);

    }

    @Override
    public void setGyroscopeValue(float[][] gyroscopeValue) {
        bt_location_info.setGyroscopeValue(gyroscopeValue);
    }

    @Override
    public void setAccelerometerValue(float[][] accelerometerValue) {
        bt_location_info.setAccelerometerValue(accelerometerValue);
    }

    @Override
    public void setLinearAccelerationValue(float[][] linearAccelerationValue) {
        bt_location_info.setLinearAccelerationValue(linearAccelerationValue);
    }

    @Override
    public void setProximityValue(float proximityValue) {
        bt_location_info.setProximityValue(proximityValue);
    }

    @Override
    public void setLightValue(float lightValue) {
        bt_location_info.setLightValue(lightValue);
    }

    @Override
    public void setGravityValue(float[][] gravityValue) {
        bt_location_info.setGravityValue(gravityValue);
    }

    @Override
    public void setRessiValue(int[][] rssiValue) {
        bt_location_info.setRssiValue(rssiValue);
    }

    @Override
    public int getMotionState(Context context) {
        return 0;
    }

    @Override
    public int getZoneState(Context context) {
        return 0;
    }
}

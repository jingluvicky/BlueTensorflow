package com.albert.uaes.tensorflowlibrary.admin;

import com.albert.uaes.tensorflowlibrary.tf.PredictionTF_motion;
import com.albert.uaes.tensorflowlibrary.tf.PredictionTF_zone;

public class BT_Location_Info {
    /**
     * 蓝牙主节点，从节点RSSI值 ，机器学习区域判定传递参数
     */
    public int[] rssi;
    /**
     * 陀螺仪，运动特征提取必传 & 手机参数标定必传
     */
    public float[][] gyroscopeValue;
    /**
     * 加速度传感器，运动特征提取必传
     */
    public float[][] accelerometerValue;
    /**
     * 线性加速度传感器，运动特征提取必传 & 手机参数标定必传
     */
    public float[][] linearAccelerationValue;
    /**
     * 障碍物距离传感器，运动特征提取必传
     */
    public float proximityValue;
    /**
     * 光强传感器，运动特征提取必传
     */
    public float lightValue;
    /**
     * 重力传感器，手机参数标定必传
     */
    public float[][] gravityValue;
    /**
     * 蓝牙主节点，从节点RSSI值，手机参数标定必传
     */
    public int[][] rssiValue;

    /**
     * 运动特征提取必传
     */
    public PredictionTF_motion predictionTF_motion;

    /**
     * 机器学习区域判定传递参数
     */
    public PredictionTF_zone predictionTF_zone;


    public void setRssi(int[] rssi) {
        this.rssi = rssi;
    }

    public void setGyroscopeValue(float[][] gyroscopeValue) {
        this.gyroscopeValue = gyroscopeValue;
    }

    public void setAccelerometerValue(float[][] accelerometerValue) {
        this.accelerometerValue = accelerometerValue;
    }

    public void setLinearAccelerationValue(float[][] linearAccelerationValue) {
        this.linearAccelerationValue = linearAccelerationValue;
    }

    public void setProximityValue(float proximityValue) {
        this.proximityValue = proximityValue;
    }

    public void setLightValue(float lightValue) {
        this.lightValue = lightValue;
    }

    public void setGravityValue(float[][] gravityValue) {
        this.gravityValue = gravityValue;
    }

    public void setRssiValue(int[][] rssiValue) {
        this.rssiValue = rssiValue;
    }

    public void setPredictionTF_motion(PredictionTF_motion predictionTF_motion) {
        this.predictionTF_motion = predictionTF_motion;
    }

    public void setPredictionTF_zone(PredictionTF_zone predictionTF_zone) {
        this.predictionTF_zone = predictionTF_zone;
    }
}

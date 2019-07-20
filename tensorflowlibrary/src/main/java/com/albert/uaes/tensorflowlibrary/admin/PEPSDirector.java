package com.albert.uaes.tensorflowlibrary.admin;

import android.content.Context;

import com.albert.uaes.tensorflowlibrary.tf.PredictionTF_motion;
import com.albert.uaes.tensorflowlibrary.tf.PredictionTF_zone;

public class PEPSDirector {
    private static PEPSDirector pepsDirector = null;

    private PEPSBuilder pepsBuilder = null;

    private PEPSDirector(PEPSBuilder pepsBuilder) {
        this.pepsBuilder = pepsBuilder;
    }

    public static PEPSDirector getInstance(PEPSBuilder pepsBuilder) {
        if (pepsDirector == null) {
            synchronized (PEPSDirector.class){
                if (pepsDirector == null){
                    pepsDirector = new PEPSDirector(pepsBuilder);
                }
            }
        }
        return pepsDirector;
    }

    public int getMotionState(Context context){
        return pepsBuilder.getMotionState(context);
    }

    public int getZoneState(Context context){
        return pepsBuilder.getZoneState(context);
    }

    public PEPSDirector setPredictionTF_motion(PredictionTF_motion predictionTF_motion){
        this.pepsBuilder.setPredictionTF_motion(predictionTF_motion);
        return pepsDirector;
    }

    public PEPSDirector setPredictionTF_zone(PredictionTF_zone predictionTF_zone){
        this.pepsBuilder.setPredictionTF_zone(predictionTF_zone);
        return pepsDirector;
    }


    public PEPSDirector setGyroscopeValue(float[][] gyroscopeValue){
        this.pepsBuilder.setGyroscopeValue(gyroscopeValue);
        return pepsDirector;
    }

    public PEPSDirector setAccelerometerValue(float[][] accelerometerValue){
        this.pepsBuilder.setAccelerometerValue(accelerometerValue);
        return pepsDirector;
    }


    public PEPSDirector setGravityValue(float[][] gravityValue){
        this.pepsBuilder.setGravityValue(gravityValue);
        return pepsDirector;
    }


    public PEPSDirector setLinearAccelerationValue(float[][] linearAccelerationValue){
        this.pepsBuilder.setLinearAccelerationValue(linearAccelerationValue);
        return pepsDirector;
    }

    public PEPSDirector setProximityValue(float proximityValue){
        this.pepsBuilder.setProximityValue(proximityValue);
        return pepsDirector;
    }

    public PEPSDirector setLightValue(float lightValue){
        this.pepsBuilder.setLightValue(lightValue);
        return pepsDirector;
    }

    public PEPSDirector setRessiValue(int[][] ressiValue){
        this.pepsBuilder.setRessiValue(ressiValue);
        return pepsDirector;
    }

    public PEPSDirector setRessi(int[] ressi){
        this.pepsBuilder.setRssi(ressi);
        return pepsDirector;
    }
}

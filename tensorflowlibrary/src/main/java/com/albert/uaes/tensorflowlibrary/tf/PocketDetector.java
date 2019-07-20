package com.albert.uaes.tensorflowlibrary.tf;

import android.util.Log;

public class PocketDetector {
    float[] gravity;
    PocketDetector(){

    }
    public static boolean inPocket(float[]gravity,float distance,float light){
        if (Math.abs(gravity[2])<=4 && distance<3&& light<5) {
            return true;
        } else{
            return false;
        }
    }

}

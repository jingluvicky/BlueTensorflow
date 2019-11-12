package com.albert.uaes.tensorflowlibrary.tf;

import android.util.Log;

import com.albert.uaes.tensorflowlibrary.model.Node;

public class PhoneStateDetector {
    float[] gravity;
    PhoneStateDetector(){

    }
    public static int inPocket(float[]gravity,float distance,float light){
        if (Math.abs(gravity[2])<=4 && distance<3&& light<5&&gravity[1]<-8) {
            Log.d("gravity",gravity[2]+" ");
            return 1;
        } else{
            return 0;
        }
    }

    public static int onEar(float[] gravity, float distance, float light, Node[] nodes){
        if (gravity[2]<=4 && distance<3&& light<5  &&gravity[1]>8) {

            Log.d("gravity",gravity[2]+" "+gravity[0]+" "+gravity[1]);
            return 1;
        } else{
            return 0;
        }
    }

}

package com.albert.uaes.tensorflowlibrary.tf;

public class KalmanFilter_distance {
    double A=1;
    double Q=0.05;
    //double Q=0.005;
    double H=1;
    double R=3^2/100;
    double B=0;
    double U=0;
    double x=0;
    double P;
    double K;
    double z;
    public KalmanFilter_distance(){

    }

    public double FilteredRSSI(double newRSSIValue)

    {
        z=newRSSIValue;
        if (0==x ){
            x=newRSSIValue;
            P=R;
        }
        z=newRSSIValue;
        if (z<x)x=z;
        else if(0!=x){
            x=x+B*U;
            P=P+Q;
            K=P/(P+R);
            x=x+K*(z-x);
            P=P-K*P;
        }
        return x;
    }

}


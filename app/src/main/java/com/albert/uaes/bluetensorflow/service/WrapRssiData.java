package com.albert.uaes.bluetensorflow.service;

import java.io.Serializable;

public class WrapRssiData implements Serializable {
    private int mainRssi=0;
    private int[] rssi={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    public WrapRssiData(int mainRssi, int[] rssi) {
        this.mainRssi = mainRssi;

        this.rssi = rssi;
    }

    public int getMainRssi() {
        return mainRssi;
    }

    public void setMainRssi(int mainRssi) {
        this.mainRssi = mainRssi;
    }

    public int[] getRssi() {
        return rssi;
    }

    public void setRssi(int[] rssi) {
        this.rssi = rssi;
    }
}

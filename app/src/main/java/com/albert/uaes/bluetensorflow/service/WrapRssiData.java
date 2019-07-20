package com.albert.uaes.bluetensorflow.service;

import java.io.Serializable;

public class WrapRssiData implements Serializable {
    private int mainRssi;
    private byte[] rssi;

    public WrapRssiData(int mainRssi, byte[] rssi) {
        this.mainRssi = mainRssi;
        this.rssi = rssi;
    }

    public int getMainRssi() {
        return mainRssi;
    }

    public void setMainRssi(int mainRssi) {
        this.mainRssi = mainRssi;
    }

    public byte[] getRssi() {
        return rssi;
    }

    public void setRssi(byte[] rssi) {
        this.rssi = rssi;
    }
}

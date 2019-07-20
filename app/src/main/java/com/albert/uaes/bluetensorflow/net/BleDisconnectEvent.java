package com.albert.uaes.bluetensorflow.net;


public class BleDisconnectEvent {

    private boolean connect;

    public BleDisconnectEvent(boolean connect) {
        this.connect = connect;
    }

    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }
}

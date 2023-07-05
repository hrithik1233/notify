package com.example.myapplication;

public class BatchPinConfigure {
    String name;
    boolean isaccessible=false;
    String pin;

    public BatchPinConfigure(String name, String pin,boolean b) {
        this.name = name;
        this.pin = pin;
        isaccessible=b;
    }

    public boolean isIsaccessible() {
        return isaccessible;
    }

    public BatchPinConfigure(String name, String pin) {
        this.name = name;
        this.pin = pin;
    }

    public BatchPinConfigure(){}

    public String getName() {
        return name;
    }

    public String getPin() {
        return pin;
    }
}

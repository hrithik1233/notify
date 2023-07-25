package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class BatchPinConfigure implements Parcelable {
    String name;
    boolean isaccessible=false;
    String pin;

    public BatchPinConfigure(String name, String pin,boolean b) {
        this.name = name;
        this.pin = pin;
        isaccessible=b;
    }

    protected BatchPinConfigure(Parcel in) {
        name = in.readString();
        isaccessible = in.readByte() != 0;
        pin = in.readString();
    }

    public static final Creator<BatchPinConfigure> CREATOR = new Creator<BatchPinConfigure>() {
        @Override
        public BatchPinConfigure createFromParcel(Parcel in) {
            return new BatchPinConfigure(in);
        }

        @Override
        public BatchPinConfigure[] newArray(int size) {
            return new BatchPinConfigure[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeByte((byte) (isaccessible ? 1 : 0));
        parcel.writeString(pin);
    }
}

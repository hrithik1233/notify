package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DataIntentShare implements Parcelable {
    String number,message;


    protected DataIntentShare(Parcel in) {
        number = in.readString();
        message = in.readString();
    }

    public static final Creator<DataIntentShare> CREATOR = new Creator<DataIntentShare>() {
        @Override
        public DataIntentShare createFromParcel(Parcel in) {
            return new DataIntentShare(in);
        }

        @Override
        public DataIntentShare[] newArray(int size) {
            return new DataIntentShare[size];
        }
    };

    public DataIntentShare(String number, String message) {
        this.number = number;
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(number);
        parcel.writeString(message);
    }

    public String getNumber() {
        return number;
    }

    public String getMessage() {
        return message;
    }
}

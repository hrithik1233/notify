package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateAndTime  implements Parcelable  {
   String date;
   String time;



    public DateAndTime(String date, String time) {
        this.date = date;
        this.time = time;
    }
    public DateAndTime(){
        date=getCurrentDate();
        time=getCurrentTime();


    }

    protected DateAndTime(Parcel in) {
        date = in.readString();
        time = in.readString();
    }


    public static final Creator<DateAndTime> CREATOR = new Creator<DateAndTime>() {
        @Override
        public DateAndTime createFromParcel(Parcel in) {
            return new DateAndTime(in);
        }

        @Override
        public DateAndTime[] newArray(int size) {
            return new DateAndTime[size];
        }
    };

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    @NonNull
    @Override
    public String toString() {
        return  date+" "+time;
    }

    public static String getCurrentDate(){
        String res="";
        try {
        Calendar calendar = Calendar.getInstance(); // For Calendar class
        Date currentDate = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        res= dateFormat.format(currentDate);
        }catch (Exception e){}
        return res;
    }
    public static String getCurrentTime(){
        String res="";
        try {
            Calendar calendar = Calendar.getInstance(); // For Calendar class
            long minute=calendar.get(Calendar.MINUTE);
            long hr=calendar.get(Calendar.HOUR);
            int AMorPM=calendar.get(Calendar.AM_PM);
            Log.i("timecon",calendar.get(Calendar.AM_PM)+"");
            String amorpm=(String)(AMorPM==1?" PM":" AM");
            res=(hr==0?"12":hr)+":"+(minute<=9?"0"+minute:minute)+amorpm;
            Log.i("timecon",res);
        }catch (Exception e){}
        return res;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(time);
    }
}

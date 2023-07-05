package com.example.myapplication;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateAndTime {
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

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
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
}

package com.example.myapplication;

import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public interface StudentResponseInterface {
     void onTouchDelete(int pos);
     void onTouchEdit(int pos);
     void onLongpress(int pos,ArrayList<StudentData> studentData);
     void onClick(int pos,ArrayList<StudentData> studentData);
}

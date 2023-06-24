package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class Notifications extends AppCompatActivity {
    ArrayList<MessagesReceiver> recievedMessage;
    ImageView backFinishBtn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        backFinishBtn=findViewById(R.id.backclickNotificationActivity);
        //finishActivity
        backFinishBtn.setOnClickListener(view -> finish());

        recievedMessage=getIntent().getParcelableArrayListExtra("messages");
        Toast.makeText(this, recievedMessage.size()+"", Toast.LENGTH_SHORT).show();

    }
}
package com.example.myapplication;

import static com.example.myapplication.SignUpActivity.APP_LOGIN;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); // Hide the action bar
        }
     if (Build.VERSION.SDK_INT >=   Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
           // w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "MyChannel";
            String channelDescription = "My Channel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(getResources().getString(R.string.NotificationChannelId), channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
              FirebaseUser user=auth.getCurrentUser();
              Intent intent=null;
                if(user==null){
                    SharedPreferences pre=getSharedPreferences(APP_LOGIN,MODE_PRIVATE);
                    SharedPreferences.Editor editor=pre.edit();
                    editor.putBoolean("isaccessed",false);
                    editor.commit();
                    intent=new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }else{
                   // Toast.makeText(MainActivity.this, user.getUid(), Toast.LENGTH_SHORT).show();
                    intent=new Intent(MainActivity.this,Home.class);
                    startActivity(intent);
                }

              finish();
          }
      },500);

    }
}
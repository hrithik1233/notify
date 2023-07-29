package com.example.myapplication;

import static com.example.myapplication.SignUpActivity.APP_LOGIN;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Boolean imgSet=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
          @SuppressLint("ResourceType")
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
                    SharedPreferences settings=getSharedPreferences("Setting",MODE_PRIVATE);
                    if(!settings.getBoolean("isPasswordEnable",false)){
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        finish();
                    }else{
                        Dialog passwordDialog=new Dialog(MainActivity.this);
                        passwordDialog.setContentView(R.layout.password_screen_layout);
                        Window window = passwordDialog.getWindow();
                        if (window != null) {
                            View decorView = window.getDecorView();
                            int flags = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                            decorView.setSystemUiVisibility(flags);
                        }
                       passwordDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                       passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                       passwordDialog.show();
                       passwordDialog.setOnCancelListener(dialogInterface -> finish());
                        EditText text=passwordDialog.findViewById(R.id.getpasswordtext);
                        ImageView img=passwordDialog.findViewById(R.id.getpasswordimage);
                        Button btn=passwordDialog.findViewById(R.id.getpasswordbutton);
                        text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                        img.setOnClickListener(view -> {
                            imgSet=!imgSet;
                            if(!imgSet){
                                img.setImageResource(R.drawable.eye_24);
                                text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            }else{
                                img.setImageResource(R.drawable.eye_close);
                                text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                            }

                        });
                        btn.setOnClickListener(view -> {
                           String password= settings.getString("passwordM","null");
                           if(password.equals(text.getText().toString())){
                              Intent intent1=new Intent(MainActivity.this,Home.class);
                               startActivity(intent1);
                               finish();
                           }else{
                               text.setError("invalid password");
                               text.requestFocus();
                           }
                        });


                    }

                }


          }
      },500);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
}
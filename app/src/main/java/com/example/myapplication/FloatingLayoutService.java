package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.List;

public class FloatingLayoutService extends Service {
    View viewRoot;
    int len=0;
    int s=0;
    WindowManager windowManager;
    String check="";
    WindowManager.LayoutParams rootParams;
    ImageView imageView, close;
    TextView textView;
    String[] numbers;
    String message="";
    int width;

    public FloatingLayoutService() {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();
            return START_NOT_STICKY;
        }else{
            String val=intent.getStringExtra("message");
            message=val;
           numbers=intent.getStringArrayExtra("Numbers");
            Log.d("test",numbers[0]);
            Log.d("test",val);
         len=numbers.length;
            Toast.makeText(this, numbers[0], Toast.LENGTH_SHORT).show();
            Toast.makeText(this, val, Toast.LENGTH_SHORT).show();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, check, Toast.LENGTH_SHORT).show();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        width = metrics.widthPixels;

        if (rootParams == null) {
            int LAYOUT_FLAG;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            }
            rootParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("com.example.floatinglayout", "Floating Layout Service", NotificationManager.IMPORTANCE_LOW);
                channel.setLightColor(Color.BLUE);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "com.example.floatinglayout");
                Notification notification = builder.setOngoing(true)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Floating Layout Service is Running")
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .build();
                startForeground(2, notification);
            }

            if (viewRoot == null) {
                viewRoot = LayoutInflater.from(this).inflate(R.layout.floating_layout, null);
                rootParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.START;
                rootParams.x = 0;
                rootParams.y = 0;

                windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                windowManager.addView(viewRoot, rootParams);

                textView = viewRoot.findViewById(R.id.textView);
                imageView = viewRoot.findViewById(R.id.imageView);
                close = viewRoot.findViewById(R.id.close);

                viewRoot.findViewById(R.id.root).setOnTouchListener(new View.OnTouchListener() {
                    private int initialX;
                    private int initialY;
                    private int initialTouchX;
                    private int initialTouchY;
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                initialX = rootParams.x;
                                initialY = rootParams.y;

                                initialTouchX = (int) motionEvent.getRawX();
                                initialTouchY = (int) motionEvent.getRawY();
                                return true;
                            case MotionEvent.ACTION_UP:
                                if (motionEvent.getRawX() < width / 2) {
                                    rootParams.x = 0;
                                } else {
                                    rootParams.x = width;
                                }
                                rootParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                                windowManager.updateViewLayout(viewRoot, rootParams);

                                int xDiff = (int) (motionEvent.getRawX() - initialTouchX);
                                int yDiff = (int) (motionEvent.getRawY() - initialTouchY);

                                if (xDiff < 20 && yDiff < 20) {
                                    if (textView.getVisibility() == View.GONE) {
                                        textView.setVisibility(View.VISIBLE);
                                        close.setVisibility(View.VISIBLE);
                                    } else {
                                        textView.setVisibility(View.GONE);
                                        close.setVisibility(View.GONE);
                                    }
                                }
                                return true;
                                case MotionEvent.ACTION_MOVE:
                                    rootParams.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                                    rootParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);

                                    windowManager.updateViewLayout(viewRoot, rootParams);
                                    return true;
                        }
                        return false;
                    }
                });

                imageView.setOnClickListener(view -> stopService());

                close.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View view) {


                           if(s<len){
                               String contact = numbers[s];
                               Log.d("tets1",numbers[s]);
                               Intent whts = new Intent(Intent.ACTION_VIEW);
                               whts.setPackage("com.whatsapp");
                               Toast.makeText(FloatingLayoutService.this, s+"", Toast.LENGTH_SHORT).show();
                               Log.d("res",s+"");
                               whts.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               whts.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + contact + "&text=" + Uri.encode(message)));
                               getApplicationContext().startActivity(whts);
                               Log.d("sendsize",contact);
                               s++;
                               try {
                                   Thread.sleep(200);
                               } catch (InterruptedException e) {
                                   throw new RuntimeException(e);
                               }
                           }
                           if(s==len){
                               new Handler().postDelayed(new Runnable() {
                                   @Override
                                   public void run() {
                                       stopService();
                                   }
                               },4000);

                           }

                    }
                });
            }
        }
    }

    private void stopService() {
        try {
            stopForeground(true);
            stopSelf();
            windowManager.removeViewImmediate(viewRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
        s=0;
    }

}
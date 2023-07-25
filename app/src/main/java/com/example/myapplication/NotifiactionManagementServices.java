package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class NotifiactionManagementServices {

    public static final String NOTIFICATIONACCESS="Notificationaccess";
    public static final String ISNOTIFICATION_AVAILABLE="hasanynotification";
    public static void makeNotification(RemoteMessage remoteMessage, Context context) {
        String mess = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();

        String contextText=remoteMessage.getNotification().getBody();

         NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"NotificationId")
                .setSmallIcon(R.drawable.iconsbell24)
                .setContentTitle(mess)
                .setContentText(contextText)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);


        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());

    }
}

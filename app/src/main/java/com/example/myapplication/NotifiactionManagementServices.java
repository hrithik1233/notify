package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
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
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("This is a test example"))
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);



// notificationId is a unique int for each notification that you must define

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());

    }
}

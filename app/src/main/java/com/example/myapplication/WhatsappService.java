package com.example.myapplication;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import java.util.ArrayList;
import java.util.List;

public class WhatsappService extends AccessibilityService {
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Handle accessibility events here
        // For example, perform automatic clicks when certain conditions are met
        if(isAppInForeground()){
            performAutomaticClicks(event);
        }




    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        // Configure your accessibility service here
        configureAccessibilityService();
    }

    private void configureAccessibilityService() {
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();

        // Set the event types you want to listen for
        serviceInfo.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_VIEW_CLICKED;

        // Set the feedback type(s) you want to receive
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        // Set additional service configuration options if needed
        // ...

        // Set the package(s) you want to monitor
        serviceInfo.packageNames = new String[]{"com.whatsapp"};

        // Enable your service
        setServiceInfo(serviceInfo);
    }

    private void performAutomaticClicks(AccessibilityEvent event) {
        try{


            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                // Perform automatic clicks based on the UI hierarchy
                // Find the desired UI elements using AccessibilityNodeInfo methods (e.g., findAccessibilityNodeInfosByViewId)

                // Example: Click the "Send" button in a WhatsApp chat
                List<AccessibilityNodeInfo> sendButtonNodes = rootNode.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");
                if (!sendButtonNodes.isEmpty()) {
                    AccessibilityNodeInfo sendButtonNode = sendButtonNodes.get(0);
                    sendButtonNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }else{
                    Toast.makeText(this, "err", Toast.LENGTH_SHORT).show();
                    stopSelf();
                }



                // Close the app after performing the necessary actions

                Thread.sleep(1000);



            }
        }catch (Exception e){}
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }



    private boolean isAppInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);

        if (runningTasks != null && runningTasks.size() > 0) {
            ComponentName topActivity = runningTasks.get(0).topActivity;
            String packageName = getPackageName();

            return topActivity != null && topActivity.getPackageName().equals(packageName);
        }

        return false;
    }
}

package com.example.myapplication;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import java.util.List;

public class WhatsappService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if(getRootInActiveWindow()==null) return;
        AccessibilityNodeInfoCompat rootNodeinfo=AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());

        List<AccessibilityNodeInfoCompat> messageNodeList=rootNodeinfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry");
        if(messageNodeList==null || messageNodeList.isEmpty()) return;

        AccessibilityNodeInfoCompat messageField=messageNodeList.get(0);
        if(messageField==null ||messageField.getText().toString().length()==0|| !messageField.getText().toString().endsWith("   "))
         return;
        List<AccessibilityNodeInfoCompat> sendmessageNodeList=rootNodeinfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");
        if(sendmessageNodeList==null || sendmessageNodeList.isEmpty()) return;

        AccessibilityNodeInfoCompat sendMessage=sendmessageNodeList.get(0);
        if(!sendMessage.isVisibleToUser()) return;
        sendMessage.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        try{

            Thread.sleep(2000);
            performGlobalAction(GLOBAL_ACTION_BACK);
            Thread.sleep(2000);
        }catch (Exception e){}
         performGlobalAction(GLOBAL_ACTION_BACK);
    }

    @Override
    public void onInterrupt() {

    }
}

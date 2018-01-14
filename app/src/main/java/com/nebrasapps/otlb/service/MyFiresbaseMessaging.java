package com.nebrasapps.otlb.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nebrasapps.otlb.R;
import com.nebrasapps.otlb.ServiceProviderHome;
import com.nebrasapps.otlb.utils.Constants;

import java.util.List;

/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */


public class MyFiresbaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

    // check activity is opened or not when new request available
    if(!isActivityRunning()) {
        Intent intent = new Intent(this, ServiceProviderHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }else
    {
        Intent intent = new Intent(Constants.NEW_REQUEST);
        intent.putExtra("reuqst", "new");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
        pushNotif();
    }
    private void pushNotif() {

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentTitle("OTLB")
                .setContentText("New Request available")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_snd))
                .setPriority(Notification.PRIORITY_MAX); //requires API 16

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
    public boolean isActivityRunning() {

        ActivityManager activityManager = (ActivityManager)this.getSystemService (Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> activitys = activityManager.getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;
        // for (int i = 0; i < activitys.size(); i++) {
        if (activitys.get(0).topActivity.toString().equalsIgnoreCase("ComponentInfo{com.nebrasapps.otlb/com.nebrasapps.otlb.ServiceProviderHome}")) {
            isActivityFound = true;
        }
        // }
        return isActivityFound;
    }
}

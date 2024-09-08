package com.example.lockerapp.Services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.lockerapp.Services.ServiceApplock;

public class BackgroundManager {

    private static BackgroundManager instance;
    private Context context;
    private static final int ALARM_REQUEST_CODE = 1001;
    private static final long INTERVAL_MS = 60000; // Example: 1 minute interval

    public static BackgroundManager getInstance() {
        if (instance == null) {
            instance = new BackgroundManager();
        }
        return instance;
    }

    public BackgroundManager init(Context context) {
        this.context = context;
        return this;
    }

    public boolean isBackgroundRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isBackgroundRunning(ServiceApplock.class)) {
                Intent intent = new Intent(context, ServiceApplock.class);
                context.startForegroundService(intent);
            }
        } else {
            if (!isBackgroundRunning(ServiceApplock.class)) {
                context.startService(new Intent(context, ServiceApplock.class));
            }
        }
    }

    public void stopService(Class<?> serviceClass) {
        if (isBackgroundRunning(serviceClass)) {
            context.stopService(new Intent(context, serviceClass));
        }
    }

    // Start AlarmManager to trigger service periodically
    public void startAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ServiceApplock.class); // or any other receiver/service you want to trigger
        PendingIntent pendingIntent = PendingIntent.getService(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        // Set a repeating alarm
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + INTERVAL_MS,
                INTERVAL_MS,
                pendingIntent
        );
    }

    // Stop the AlarmManager
    public void stopAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ServiceApplock.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        // Cancel the alarm
        alarmManager.cancel(pendingIntent);
    }



}

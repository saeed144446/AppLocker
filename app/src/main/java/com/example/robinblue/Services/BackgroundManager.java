package com.example.robinblue.Services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.robinblue.Broadcast.ReciverApplock;

import java.util.List;

public class BackgroundManager {

    private static BackgroundManager instance;
    private Context context;
    private static final int ALARM_REQUEST_CODE = 1001;
    private static final long INTERVAL_MS = 1; // Example: 1 minute interval

    private BackgroundManager() {
        // Private constructor to enforce singleton pattern
    }
    public static synchronized BackgroundManager getInstance() {
        if (instance == null) {
            instance = new BackgroundManager();
        }
        return instance;
    }
    public BackgroundManager init(Context context) {
        this.context = context.getApplicationContext(); // Ensure context is application context
        return this;
    }

    public boolean isAppInBackground() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) return true;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                return true;
            }
        }
        return false;
    }
    public void startService() {
        Intent intent = new Intent(context, ServiceApplock.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isBackgroundRunning(ServiceApplock.class)) {
                context.startForegroundService(intent);
            }
        } else {
            if (!isBackgroundRunning(ServiceApplock.class)) {
                context.startService(intent);
            }
        }
    }
    public void stopService(Class<?> serviceClass) {
        if (isBackgroundRunning(serviceClass)) {
            context.stopService(new Intent(context, serviceClass));
        }
    }

    private boolean isBackgroundRunning(Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : services) {
            if (service.service.getClassName().equals(serviceClass.getName())) {
                return true;
            }
        }
        return false;
    }

    public void startAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReciverApplock.class); // Use an AlarmReceiver to handle the alarm event
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set a repeating alarm
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + INTERVAL_MS,
                INTERVAL_MS,
                pendingIntent
        );
    }
    public void stopAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReciverApplock.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancel the alarm
        alarmManager.cancel(pendingIntent);
    }
}

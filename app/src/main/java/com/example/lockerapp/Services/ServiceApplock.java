package com.example.lockerapp.Services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.lockerapp.Activity.LockActivity;
import com.example.lockerapp.Broadcast.ReciverApplock;

import com.example.lockerapp.R;

import com.example.lockerapp.Utils.MyApplication;
import com.example.lockerapp.Utils.Utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
public class ServiceApplock extends Service {

    private static final String CHANNEL_ID = "AppLockServiceChannel";
    private Timer timer;
    private static final long CHECK_INTERVAL_MS = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ServiceApplock", "Service onCreate called");
        startForegroundService();
        startAppLockChecking();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ServiceApplock", "Service started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ServiceApplock", "Service destroyed");
        stopAppLockChecking();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "App Lock Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }

        Intent notificationIntent = new Intent(this, LockActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("App Lock Service")
                .setContentText("App Lock is running in the background")
                .setSmallIcon(R.drawable.ic_lock)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(1, notification);
    }

    private void startAppLockChecking() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runAppLock();
            }
        }, 0, CHECK_INTERVAL_MS); // Use a more reasonable interval
    }

    private void stopAppLockChecking() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void runAppLock() {
        Log.d("ServiceApplock", "Checking for locked apps");

        // Check if the app is unlocked
        if (MyApplication.isAppUnlocked()) {
            Log.d("ServiceApplock", "App is already unlocked, skipping lock");
            return; // Exit early if the app is unlocked
        }

        String currentApp = new Utils(this).getLauncherTopApp();
        if (TextUtils.isEmpty(currentApp)) {
            return; // No current app detected
        }

        // Check if the app is locked and not unlocked yet
        if (isAppLocked(currentApp)) {

            Log.d("ServiceApplock", "App is locked, sending broadcast to lock");
            Intent intent = new Intent(this, ReciverApplock.class);
            intent.putExtra("currentApp", currentApp);
            sendBroadcast(intent);
        }
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Paper.book().delete("isUnlocked");
        super.onTaskRemoved(rootIntent);
    }

    public boolean isAppLocked(String packageName) {
        return Paper.book().contains(packageName);
    }

    public void unlockApp(String packageName) {
        Paper.book().write("isUnlocked", true);
        Log.d("ServiceApplock", "App unlocked: " + packageName);
    }

    public void lockApp(String packageName) {
        Paper.book().delete("isUnlocked");
        Log.d("ServiceApplock", "App relocked: " + packageName);
    }

    }



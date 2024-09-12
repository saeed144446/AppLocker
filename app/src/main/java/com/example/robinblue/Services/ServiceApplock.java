package com.example.robinblue.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.robinblue.Activity.PatternLockAct;
import com.example.robinblue.Broadcast.ReciverApplock;
import com.example.robinblue.Utils.MyApplication;
import com.robinblue.applockpro.R;
import com.example.robinblue.Utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class ServiceApplock extends Service {

    private static final String CHANNEL_ID = "AppLockServiceChannel";
    private Timer timer;
    private static final long CHECK_INTERVAL_MS = 1; // Check every second
    private String lastUnlockedApp = null;

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

        Intent notificationIntent = new Intent(this, PatternLockAct.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("App Lock Service")
                .setContentText("App Lock is running in the background")
                .setSmallIcon(R.drawable.ic_lock) // Ensure you have a valid icon
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(1, notification);
    }


    private void startAppLockChecking() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runAppLock();

            }
        }, 0, CHECK_INTERVAL_MS);
    }

    private void stopAppLockChecking() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void runAppLock() {
        Log.d("ServiceApplock", "Checking for locked apps");

        String currentApp = new Utils(this).getLauncherTopApp();
        if (TextUtils.isEmpty(currentApp)) {
            return; // No current app detected
        }

// Check if the current app is locked


        // Check if the current app is locked
        if (isAppLocked(currentApp)) {
            if (!isAppUnlocked(currentApp)) {
                Log.d("ServiceApplock", "App is locked and not unlocked, sending broadcast to lock");
                Intent intent = new Intent(this, ReciverApplock.class);
                intent.putExtra("currentApp", currentApp);
                sendBroadcast(intent);
            } else {
                // Set a flag to lock the app again
                Paper.book().write("relock_" + currentApp, true);
            }
        }

// If switching apps, relock the previous one
        if (lastUnlockedApp != null && !lastUnlockedApp.equals(currentApp)) {
            Log.d("ServiceApplock", "Relocking previous app: " + lastUnlockedApp);
            lockApp(lastUnlockedApp); // Relock the previous app
        }

// Set the last unlocked app to the current one
        lastUnlockedApp = currentApp;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Paper.book().delete("isUnlocked"); // Reset unlock state when the app is closed
        super.onTaskRemoved(rootIntent);
    }

    public boolean isAppLocked(String packageName) {
        return Paper.book().contains(packageName);
    }
    public boolean isAppUnlocked(String packageName) {
        return Paper.book().read("isUnlocked_" + packageName, false);
    }

    public void unlockApp(String packageName) {
        Paper.book().write("isUnlocked_" + packageName, true); // Store unlock state for individual apps
        lastUnlockedApp = packageName; // Track the last unlocked app
        Log.d("ServiceApplock", "App unlocked: " + packageName);
    }

    public void lockApp(String packageName) {

        Paper.book().delete("isUnlocked_" + packageName); // Delete unlock state for individual apps
        Log.d("ServiceApplock", "App relocked: " + packageName);
    }

}
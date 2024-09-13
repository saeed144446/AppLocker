package com.example.robinblue.Utils;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;

import java.util.List;

import io.paperdb.Paper;

public class Utils {

    private final Context context;
    private UsageStatsManager usageStatsManager;

    public Utils(Context context) {
        this.context = context;
    }

    // Method to get the current top app
    public String getLauncherTopApp() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // For Android 5.0 and above
            usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 86400000; // 1 day in milliseconds

            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = usageStatsManager.queryEvents(beginTime, endTime);

            String result = "";
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                    result = event.getPackageName();
                }
            }

            if (!TextUtils.isEmpty(result)) {
                return result;
            }

        } else {
            // For Android versions below 5.0
            List<ActivityManager.RunningTaskInfo> taskInfos = manager.getRunningTasks(1);
            if (taskInfos != null && !taskInfos.isEmpty()) {
                return taskInfos.get(0).topActivity.getPackageName();
            }
        }

        // Fallback or empty result
        return "";
    }


    // Method to check if the app is locked
    public boolean isLocked(String packageName) {
        return Paper.book().contains(packageName);
    }

    // Save the last app that was locked
    public void setLastApp(String packageName) {
        Paper.book().write("lastApp", packageName);
    }

    // Retrieve the last app that was locked
    public String getLastApp() {
        return Paper.book().read("lastApp", null);
    }

    // Clear the last app that was locked
    public void clearLastApp() {
        Paper.book().delete("lastApp");
    }


    // Lock the app

    public void lockApp(String packageName) {
        Paper.book().write(packageName, true);  // Mark the app as locked
    }

    public void unlockApp(String packageName) {
        Paper.book().delete(packageName);  // Unlock the app by removing the entry
    }


    // Method to check if the app has usage stats permission
    public static boolean checkPermission(Context context) {
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
    public String getLockedApp() {
        return Paper.book().read("lockedApp", "");
    }

    public boolean isDeviceLocked() {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

        if (keyguardManager != null) {
            // Check if the device is locked
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                return keyguardManager.isDeviceLocked();
            } else {
                return keyguardManager.inKeyguardRestrictedInputMode();
            }
        }

        return false; // Return false if we can't determine the lock state
    }
}

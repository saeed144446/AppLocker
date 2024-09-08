package com.example.lockerapp.Utils;

import static android.app.AppOpsManager.MODE_ALLOWED;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.paperdb.Paper;

public class Utils {

    private String EXTRA_LAST_APP = "EXTRA_LAST_APP";
    private Context context;
  //  private static final long RELOCK_DELAY_MS = 10000; // 10 seconds delay
  //  private Map<String, Long> lastUnlockTimes = new HashMap<>();


    public Utils(Context context) {
        this.context = context;
        Paper.init(context);
    }

    public boolean isLocked(String packageName) {
        return Paper.book().contains(packageName);
    }


    public void Lock(String pk) {
        Paper.book().write(pk, pk);
        Log.d("AppLock", "Locked " + pk);
    }

    public void unLock(String pk) {
        Paper.book().delete(pk);
        Log.d("AppLock", "Unlocked " + pk);
    }


    public void setLastApp(String pk) {
        Paper.book().write(EXTRA_LAST_APP, pk);
    }

    public String getLastApp() {
        return Paper.book().read(EXTRA_LAST_APP);
    }

    public void clearLastApp() {
        Paper.book().delete(EXTRA_LAST_APP);
    }


    public static boolean checkPermission(Context context) {
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }


    UsageStatsManager usageStatsManager;

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

}

package com.example.lockerapp.Utils;

import android.app.Application;
import android.content.Context;

import com.example.lockerapp.R;
import com.example.lockerapp.Utils.AppLockLifecycleObserver;
import com.github.omadahealth.lollipin.lib.managers.LockManager;

import io.paperdb.Paper;

public class MyApplication extends Application {
    private static final long CHECK_INTERVAL_MS = 1;



    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);

        AppLockLifecycleObserver.init(this);

    }

    public static boolean isAppUnlocked() {
        return Paper.book().read("isUnlocked", false);
    }
    public static boolean isAppLocked(Context context, String packageName) {
        return Paper.book().contains(packageName);
    }
}

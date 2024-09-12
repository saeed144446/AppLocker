package com.example.robinblue.Utils;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.robinblue.Broadcast.ReciverApplock;

import io.paperdb.Paper;

public class MyApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Paper for persistent storage
        Paper.init(this);
        AppLockLifecycleObserver.init(this);


        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        registerReceiver(new ReciverApplock(), filter);

        AppLockLifecycleObserver appLifecycleObserver = new AppLockLifecycleObserver(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    }

    // Helper method to check if the app is unlocked
    public static boolean isAppUnlocked() {
        return Paper.book().read("isUnlocked", false);
    }

    // Helper method to check if a specific app is locked
    public static boolean isAppLocked(String packageName) {
        return Paper.book().contains(packageName);
    }
}

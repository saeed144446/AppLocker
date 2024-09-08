package com.example.lockerapp.Utils;

import android.app.Application;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import io.paperdb.Paper;

public class AppLockLifecycleObserver implements LifecycleObserver {

     Application application;

    public AppLockLifecycleObserver(Application application) {
        this.application = application;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        // App goes to background, reset the unlock state
        Paper.book().write("isUnlocked", false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // App comes to foreground, no action needed here
    }

    public static void init(Application application) {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLockLifecycleObserver(application));
    }
}

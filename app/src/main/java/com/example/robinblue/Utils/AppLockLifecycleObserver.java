package com.example.robinblue.Utils;

import android.app.Application;
import android.content.Intent;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.robinblue.Activity.PatternLockAct;

import io.paperdb.Paper;

public class AppLockLifecycleObserver implements LifecycleObserver {

    private final Application application;

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
        if (!Paper.book().read("isUnlocked", false)) {
            Intent intent = new Intent(application, PatternLockAct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            application.startActivity(intent);
        }
    }



    public static void init(Application application) {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLockLifecycleObserver(application));
    }
}

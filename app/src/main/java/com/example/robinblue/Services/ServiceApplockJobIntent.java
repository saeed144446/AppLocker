package com.example.robinblue.Services;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.example.robinblue.Broadcast.ReciverApplock;

public class ServiceApplockJobIntent extends JobIntentService {


    public static final int JOB_ID = 1000;

    public static void enqueueWork(Intent intent, Context context) {
        enqueueWork(context, ServiceApplockJobIntent.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        runAppLock();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        BackgroundManager.getInstance().init(this).startAlarmManager();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        BackgroundManager.getInstance().init(this).startAlarmManager();
        super.onDestroy();
    }

    private void runAppLock() {
        Intent intent = new Intent(this, ReciverApplock.class);
        sendBroadcast(intent);
    }
}

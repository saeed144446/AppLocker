package com.example.lockerapp.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.lockerapp.Activity.LockActivity;
import com.example.lockerapp.Services.BackgroundManager;
import com.example.lockerapp.Utils.Utils;

public class RestartServiesWhenStoped extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        BackgroundManager.getInstance().init(context).startService();

    }
}

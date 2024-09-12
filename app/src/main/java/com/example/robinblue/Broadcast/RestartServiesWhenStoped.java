package com.example.robinblue.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.robinblue.Services.BackgroundManager;

public class RestartServiesWhenStoped extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        BackgroundManager.getInstance().init(context).startService();

    }
}

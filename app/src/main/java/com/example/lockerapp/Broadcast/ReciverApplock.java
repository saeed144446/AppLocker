package com.example.lockerapp.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.lockerapp.Utils.Utils;
import com.example.lockerapp.Activity.LockActivity;

public class ReciverApplock extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Utils utils = new Utils(context);
        String appRunning = utils.getLauncherTopApp();

        // Check if the current app is locked
        if (utils.isLocked(appRunning) ) {
            if (!appRunning.equals(utils.getLastApp())) {
                // Only launch LockActivity if it’s a new locked app
                utils.clearLastApp();
                utils.setLastApp(appRunning);

                Intent lockIntent = new Intent(context, LockActivity.class);
                lockIntent.putExtra("broad_cast", true);
                lockIntent.putExtra("lockedApp", appRunning); // Pass the locked app’s package name
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear top stack
                context.startActivity(lockIntent);
            }
        }
    }
}

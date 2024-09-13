
package com.example.robinblue.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.robinblue.Activity.PatternLockAct;
import com.example.robinblue.Services.ServiceApplock;
import com.example.robinblue.Utils.Utils;

import io.paperdb.Paper;

public class ReciverApplock extends BroadcastReceiver {
    public boolean isAppBackground = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        Utils utils = new Utils(context);
        String appRunning = utils.getLauncherTopApp();


        // Handle screen on and off events
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_OFF:
                    Log.d("ReciverApplock", "Screen turned off");
                    // Reset any unlock state when the screen turns off
                    utils.clearLastApp();  // Clear last app state
                    break;

                case Intent.ACTION_SCREEN_ON:
                    Log.d("ReciverApplock", "Screen turned on");
                    // You can handle any logic here when the screen turns on
                    break;
            }
        }

        // Check if the current app is locked (this part is unchanged)
        if (utils.isLocked(appRunning)) {
            if (!appRunning.equals(utils.getLastApp())) {
                // Only launch PatternLockAct if it’s a new locked app
                utils.clearLastApp();
                utils.setLastApp(appRunning);

                Intent lockIntent = new Intent(context, PatternLockAct.class);
                lockIntent.putExtra("broad_cast", true);
                lockIntent.putExtra("lockedApp", appRunning); // Pass the locked app’s package name
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear top stack
                context.startActivity(lockIntent);
            } else {
                // Check if the app needs to be relocked
                if (Paper.book().read("relock_" + appRunning, false)) {
                    // Lock the app
                    Intent lockIntent = new Intent(context, PatternLockAct.class);
                    lockIntent.putExtra("broad_cast", true);
                    lockIntent.putExtra("lockedApp", appRunning); // Pass the locked app’s package name
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear top stack
                    context.startActivity(lockIntent);
                    Paper.book().delete("relock_" + appRunning); // Delete relock flag after locking
                }
            }
        }
    }
}

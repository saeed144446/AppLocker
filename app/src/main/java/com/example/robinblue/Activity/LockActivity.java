package com.example.robinblue.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.robinblue.Broadcast.ReciverApplock;
import com.robinblue.applockpro.R;
import com.example.robinblue.Services.BackgroundManager;
import com.example.robinblue.Services.ServiceApplock;
import com.example.robinblue.Utils.Utils;
import com.guardanis.applock.AppLock;
import com.guardanis.applock.activities.LockCreationActivity;
import com.guardanis.applock.dialogs.UnlockDialogBuilder;


public class LockActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CREATE_LOCK = 101;
    private static final int REQUEST_CODE_UNLOCK = 102;
    private String lockedApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        lockedApp = getIntent().getStringExtra("lockedApp"); // Get the locked app's package name

        Button buttonSetLock = findViewById(R.id.buttonSetLock);
        BackgroundManager.getInstance().init(this).startService();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, ServiceApplock.class));
        } else {
            startService(new Intent(this, ServiceApplock.class));
        }

        buttonSetLock.setOnClickListener(v -> {
            if (!AppLock.isEnrolled(this)) {
                openLockCreationActivity();  // Start the PIN creation process
            } else {
                openUnlockDialog();  // If the PIN is already created, open the unlock dialog
            }
        });

        initIconApp();
    }

    private void initIconApp() {
        if (getIntent().getStringExtra("broad_cast") != null) {
            ImageView icon = findViewById(R.id.app_icon);
            String currentApp = new Utils(this).getLastApp();
            if (currentApp != null) {
                try {
                    ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(currentApp, 0);
                    if (applicationInfo != null) {
                        icon.setImageDrawable(applicationInfo.loadIcon(getPackageManager()));
                    } else {
                        icon.setImageResource(R.drawable.ic_lock); // Set a default icon if the app icon is unavailable
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    icon.setImageResource(R.drawable.ic_lock); // Set a default icon in case of an error
                }
            } else {
                icon.setImageResource(R.drawable.ic_lock); // Set a default icon if no app is found
            }
        }
    }

    private void openLockCreationActivity() {
        Intent intent = new Intent(this, LockCreationActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CREATE_LOCK);
    }

    private void openUnlockDialog() {
        new UnlockDialogBuilder(this)
                .onUnlocked(() -> {
                    Toast.makeText(this, "Unlocked successfully!", Toast.LENGTH_SHORT).show();
                    proceedToNextActivity();  // Redirect to the locked app after unlocking
                })
                .onCanceled(() -> Toast.makeText(this, "Unlock canceled", Toast.LENGTH_SHORT).show())
                .show();
    }

    private void proceedToNextActivity() {
        // Intent to unlock the app
        Intent serviceIntent = new Intent(this, ServiceApplock.class);
       // serviceIntent.setAction(ServiceApplock.ACTION_UNLOCK_APP); // Set an action to indicate the unlock request
        serviceIntent.putExtra("lockedApp", lockedApp); // Pass the locked app package name
        startService(serviceIntent);
        // Start the service

        if (lockedApp != null && !lockedApp.isEmpty()) {
            // Redirect to the locked app after successful unlock
            PackageManager packageManager = getPackageManager();
            Intent launchIntent = packageManager.getLaunchIntentForPackage(lockedApp);
            if (launchIntent != null) {
                startActivity(launchIntent);
            } else {
                Toast.makeText(this, "Unable to open the locked app.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // If no locked app, proceed to the main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();  // Close the current activity
    }




    @Override
    protected void onResume() {
        super.onResume();
        if (AppLock.isEnrolled(this)) {
            AppLock.onActivityResumed(this);  // Ensure the activity is locked if needed
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startCurrentHomepage();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        String currentApp = new Utils(this).getLauncherTopApp();
        if (!TextUtils.isEmpty(currentApp)) {
            Intent intent = new Intent(this, ReciverApplock.class);
            intent.putExtra("currentApp", currentApp);
            sendBroadcast(intent);
        }
    }


    public void startCurrentHomepage() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        ActivityInfo activityInfo = resolveInfo.activityInfo;

        ComponentName componentName = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }
}





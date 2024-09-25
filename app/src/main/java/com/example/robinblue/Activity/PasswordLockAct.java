package com.example.robinblue.Activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.robinblue.PasswordLockView.IndicatorDots;
import com.example.robinblue.PasswordLockView.PinLockListener;
import com.example.robinblue.PasswordLockView.PinLockView;
import com.example.robinblue.Model.Password;
import com.example.robinblue.Services.BackgroundManager;
import com.example.robinblue.Services.ResourceUtils;
import com.example.robinblue.Services.ServiceApplock;
import com.example.robinblue.Utils.Utils;
import com.robinblue.applockpro.R;

import java.util.concurrent.Executor;

import io.paperdb.Paper;

public class PasswordLockAct extends AppCompatActivity {

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private Password utilspassword;
    private String userPin;
    private String lockedApp;
    TextView status_password, app_name;
    private ImageView app_icon,fingerprint_icon;
    PinLockListener mPinLockListener;
    private String userpassword;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_lock);

        status_password = findViewById(R.id.status_password);
        mPinLockView = findViewById(R.id.pinlock);
        mIndicatorDots = findViewById(R.id.indicator_dots);
        utilspassword = new Password();
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        fingerprint_icon=findViewById(R.id.fingerprint);

        mPinLockView.setPinLockListener(mPinLockListener);
        app_name = findViewById(R.id.app_title);
        app_icon = findViewById(R.id.imageicon);

        initIconApp();
        initPinLockListener();


        boolean isFingerPrint = Paper.book().read("fingerprint_enabled", false);
        if (isFingerPrint==true) {
            fingerprint_icon.setVisibility(View.VISIBLE);
            fingerPrint();
        }else {
            fingerprint_icon.setVisibility(View.INVISIBLE);

        }

        lockedApp = getIntent().getStringExtra("lockedApp");
        BackgroundManager.getInstance().init(this).startService();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, ServiceApplock.class));
        } else {
            startService(new Intent(this, ServiceApplock.class));
        }
        ServiceApplock serviceApplock = new ServiceApplock();
        serviceApplock.lockApp(lockedApp);
        if (utilspassword.getPassword() == null) {
            status_password.setText("Create Password");
        } else {
            status_password.setText("Enter Password");
        }

        fingerprint_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerPrint();
            }
        });
    }

    private void initIconApp() {
        // Check if lockedApp is not null or empty
        if (lockedApp != null && !lockedApp.isEmpty()) {
            try {
                PackageManager packageManager = getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(lockedApp, 0);

                // Set the app icon
                app_icon.setImageDrawable(applicationInfo.loadIcon(packageManager));
                app_name.setText(applicationInfo.loadLabel(packageManager));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                // Set a default icon if the app info is not found
                app_icon.setImageResource(R.drawable.ic_applock_icon);
                app_name.setText(R.string.app_name);
            }
        } else {
            // Set a default icon if lockedApp is null or empty
            app_icon.setImageResource(R.drawable.ic_applock_icon);
            app_name.setText(R.string.app_name);
        }
    }

    private void initPinLockListener() {
        mPinLockListener = new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                if (utilspassword.getPassword() == null) {
                    if (utilspassword.isFirststep()) {
                        userPin = pin;
                        utilspassword.setFirststep(false);
                        mPinLockView.resetPinLockView();
                        status_password.setText("Confirm Passoword");
                    } else {
                        if (userPin.equals(pin)) {
                            utilspassword.setPassword(userPin);
                            status_password.setText(utilspassword.STATUS_PASSWORD_CORRECT);
                            proceedToNextActivity();
                        } else {
                            status_password.setText("Incorrect Passoword");
                            mPinLockView.resetPinLockView();
                        }
                    }
                } else {
                    if (utilspassword.isCorrect(pin)) {
                        status_password.setText("Correct Passoword");
                        proceedToNextActivity();
                    } else {
                        status_password.setText("Incorrect Passoword");
                    }
                    mPinLockView.resetPinLockView();

                }
            }

            @Override
            public void onEmpty() {
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
            }
        };

        mPinLockView.setPinLockListener(mPinLockListener);
    }


    private void proceedToNextActivity() {
        ServiceApplock serviceApplock = new ServiceApplock();
        // serviceApplock.unlockApp(lockedApp);
        if (lockedApp != null && !lockedApp.isEmpty()) {
            // Redirect to the locked app after successful unlock
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(lockedApp);
            if (launchIntent != null) {
                startActivity(launchIntent);
            } else {
                // Toast.makeText(this, "Unable to open the locked app.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // If no locked app, proceed to the main activity
            if (getIntent().getStringExtra("broad_cast") == null) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
        finish();  // Close the current activity
    }
    private void fingerPrint() {
        Executor executor = ContextCompat.getMainExecutor(this);
        androidx.biometric.BiometricPrompt biometricPrompt = new androidx.biometric.BiometricPrompt(this, executor, new androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Toast.makeText(LockCheckActivity.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(androidx.biometric.BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //   Toast.makeText(LockCheckActivity.this, "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                proceedToNextActivity();  // If authenticated, proceed to the next activity
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //Toast.makeText(LockCheckActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        androidx.biometric.BiometricPrompt.PromptInfo promptInfo = new androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Use your biometric credential to log in")
                .setNegativeButtonText("Use Pattern")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}



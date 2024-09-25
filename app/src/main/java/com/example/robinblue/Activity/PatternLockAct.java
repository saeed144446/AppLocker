package com.example.robinblue.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.robinblue.Interface.PatternLockViewListener;
import com.example.robinblue.Model.AppItem;
import com.example.robinblue.Model.Password;
import com.example.robinblue.Services.ResourceUtils;
import com.example.robinblue.Utils.SelfieIntruderCapture;
import com.guardanis.applock.AppLock;
import com.robinblue.applockpro.R;
import com.example.robinblue.Services.BackgroundManager;
import com.example.robinblue.Services.ServiceApplock;
import com.example.robinblue.PatternLockView.PatternLockUtils;
import com.example.robinblue.PatternLockView.PatternLockView;
import com.example.robinblue.Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import io.paperdb.Paper;

public class PatternLockAct extends AppCompatActivity {

    Password utilspassword;
    String userpassword;
    TextView status_password, app_name;
    private String lockedApp;
    private boolean isBound = false;
    private Vibrator vibrator;
    private boolean isVibrationEnabled;
    private PatternLockView patternLockView;
    private SelfieIntruderCapture selfieIntruderCapture;
    private boolean isSelfieIntruder;
    private ImageView app_icon, fingerprint_icon;
    private ArrayList<AppItem> appList;

    // Variables for tracking failed attempts
    private int failedAttempts = 0;
    private static final int MAX_ATTEMPTS = 5;
    private static final long DISABLE_TIME = 60000; // 1 minute in milliseconds

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_lock);
        status_password = findViewById(R.id.status_password);
        patternLockView = findViewById(R.id.patternLockView);
        app_icon = findViewById(R.id.imageicon);
        app_name = findViewById(R.id.app_title);
        fingerprint_icon = findViewById(R.id.fingerprint);

        selfieIntruderCapture = new SelfieIntruderCapture(this);
        selfieIntruderCapture.startCamera();

        selfieIntruderCapture = new SelfieIntruderCapture(this);
        selfieIntruderCapture.startCamera();




        utilspassword = new Password();
        status_password.setText(utilspassword.STATUS_FIRST_STEP);

        boolean changePattern = getIntent().getBooleanExtra("changePattern", false);
        if (changePattern) {
            status_password.setText("Draw your new pattern");
        }

        isSelfieIntruder = Paper.book().read("selfie_intruder_enabled", false);

        boolean isFingerPrint = Paper.book().read("fingerprint_enabled", false);
        if (isFingerPrint) {
            fingerprint_icon.setVisibility(View.VISIBLE);
            fingerPrint();
        } else {
            fingerprint_icon.setVisibility(View.INVISIBLE);
        }

        boolean isPatternHide = Paper.book().read("hide_enabled", false);
        if (isPatternHide) {
            patternLockView.setInStealthMode(true);
        } else {
            patternLockView.setInStealthMode(false);
        }

        lockedApp = getIntent().getStringExtra("lockedApp");
        BackgroundManager.getInstance().init(this).startService();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Retrieve the vibration state from PaperDB
        isVibrationEnabled = Paper.book().read("vibration_enabled", false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, ServiceApplock.class));
        } else {
            startService(new Intent(this, ServiceApplock.class));
        }
        ServiceApplock serviceApplock = new ServiceApplock();
        serviceApplock.lockApp(lockedApp);

        if (utilspassword.getPassword() == null) {
            status_password.setText("Create Password");
        }

        fingerprint_icon.setOnClickListener(v -> fingerPrint());
        initIconApp();
        initPatternListeners();
    }

    private void initIconApp() {
        if (lockedApp != null && !lockedApp.isEmpty()) {
            try {
                PackageManager packageManager = getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(lockedApp, 0);
                app_icon.setImageDrawable(applicationInfo.loadIcon(packageManager));
                app_name.setText(applicationInfo.loadLabel(packageManager));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                app_icon.setImageResource(R.drawable.ic_applock_icon);
                app_name.setText(R.string.app_name);
            }
        } else {
            app_icon.setImageResource(R.drawable.ic_applock_icon);
            app_name.setText(R.string.app_name);
        }
    }

    private void initPatternListeners() {
        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {
                // Optionally handle the start of the pattern drawing
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
                // Optionally handle progress updates
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                String pwd = PatternLockUtils.patternToString(patternLockView, pattern);

                // Check if the pattern length is sufficient
                if (pwd.length() < 4) {
                    patternLockView.clearPattern();
                    status_password.setText("Pattern must be at least 4 dots.");
                    return;
                }

                // Handling the change pattern case
                if (getIntent().getBooleanExtra("changePattern", false)) {
                    // Check if user is in the first step of setting a new pattern
                    if (utilspassword.isFirststep()) {
                        userpassword = pwd; // Save the new pattern
                        utilspassword.setFirststep(false); // Move to confirmation step
                        patternLockView.clearPattern();
                        status_password.setText("Confirm your new pattern");
                    } else {
                        // Confirm the pattern
                        if (userpassword.equals(pwd)) {
                            utilspassword.setPassword(userpassword); // Save new confirmed pattern
                            status_password.setText(utilspassword.STATUS_PASSWORD_CORRECT);
                            finish(); // Finish activity after successful pattern change
                        } else {
                            status_password.setText(utilspassword.STATUS_PASSWORD_INCORRECT);
                            patternLockView.clearPattern(); // Reset pattern lock for retry
                        }
                    }
                    return;
                }

                // Handling first-time pattern setup
                if (utilspassword.getPassword() == null) {
                    if (utilspassword.isFirststep()) {
                        userpassword = pwd; // Store the pattern for confirmation
                        utilspassword.setFirststep(false);
                        patternLockView.clearPattern();
                        status_password.setText(utilspassword.STATUS_NEXT_STEP); // Prompt for confirmation
                    } else {
                        // Confirm pattern setup
                        if (userpassword.equals(pwd)) {
                            utilspassword.setPassword(userpassword); // Save the new pattern
                            status_password.setText(utilspassword.STATUS_PASSWORD_CORRECT);
                            proceedToNextActivity(); // Move to the next activity
                        } else {
                            status_password.setText(utilspassword.STATUS_PASSWORD_INCORRECT);
                            patternLockView.clearPattern(); // Retry pattern confirmation
                        }
                    }
                } else {
                    // Verifying existing pattern during login/unlock
                    if (utilspassword.isCorrect(pwd)) {
                        status_password.setText(utilspassword.STATUS_PASSWORD_CORRECT);
                        patternLockView.setCorrectStateColor(ResourceUtils.getColor(PatternLockAct.this, R.color.colorPrimary));
                        proceedToNextActivity();
                    } else {
                        failedAttempts++;
                        if (failedAttempts >= MAX_ATTEMPTS) {
                            disablePatternLock(); // Disable after max attempts
                            if (isSelfieIntruder) {
                                selfieIntruderCapture.captureSelfie(); // Capture selfie if intruder detected
                            }
                        } else {
                            status_password.setText(utilspassword.STATUS_PASSWORD_INCORRECT);
                            patternLockView.clearPattern(); // Retry pattern
                        }
                    }
                }
            }


            @Override
            public void onCleared() {
            }
        });
    }

    private void disablePatternLock() {
        status_password.setText("Too many attempts. Try again in 1 minute.");
        patternLockView.setEnabled(false);

        // Start a countdown timer to re-enable the pattern lock after 1 minute
        new CountDownTimer(DISABLE_TIME, 1000) { // DISABLE_TIME is 60,000 milliseconds (1 minute)
            public void onTick(long millisUntilFinished) {
                // Update the status text to show the remaining time in seconds
                long secondsRemaining = millisUntilFinished / 1000;
                status_password.setText("Try again in " + secondsRemaining + " seconds.");
                patternLockView.clearPattern();
            }

            public void onFinish() {
                patternLockView.setEnabled(true);
                failedAttempts = 0;
                status_password.setText("You can try unlocking again.");
            }
        }.start();
    }


    private void proceedToNextActivity() {
        ServiceApplock serviceApplock = new ServiceApplock();
        serviceApplock.unlockApp(lockedApp);

        if (lockedApp != null && !lockedApp.isEmpty()) {
            PackageManager packageManager = getPackageManager();
            Intent launchIntent = packageManager.getLaunchIntentForPackage(lockedApp);
            if (launchIntent != null) {
                startActivity(launchIntent);
            } else {
                Toast.makeText(this, "Unable to open the locked app.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (getIntent().getStringExtra("broad_cast") == null) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (utilspassword.getPassword() == null && !utilspassword.isFirststep()) {
            utilspassword.setFirststep(true);
            status_password.setText(utilspassword.STATUS_FIRST_STEP);
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppLock.isEnrolled(this)) {
            AppLock.onActivityResumed(this);  // Ensure the activity is locked if needed
        }
        isVibrationEnabled = Paper.book().read("vibration_enabled", false);  // Reload the state
    }

    private void vibrate(int duration) {
        if (isVibrationEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(duration);
            }
        }
    }

    private void fingerPrint() {
        Executor executor = ContextCompat.getMainExecutor(this);
        androidx.biometric.BiometricPrompt biometricPrompt = new androidx.biometric.BiometricPrompt(PatternLockAct.this, executor, new androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            //    Toast.makeText(PatternLockAct.this, "Fingerprint authentication error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull androidx.biometric.BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                proceedToNextActivity();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
           //     Toast.makeText(PatternLockAct.this, "Fingerprint authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        androidx.biometric.BiometricPrompt.PromptInfo promptInfo = new androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Unlock the app using your fingerprint")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}

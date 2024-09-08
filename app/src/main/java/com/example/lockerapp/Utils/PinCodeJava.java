package com.example.lockerapp.Utils;

import android.widget.Toast;
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;

public class PinCodeJava extends AppLockActivity {

    // Implement forgot pin logic
    @Override
    public void showForgotDialog() {
        Toast.makeText(this, "Implement your forgot password logic here.", Toast.LENGTH_LONG).show();
    }

    // Handle pin failure events
    @Override
    public void onPinFailure(int attempts) {
        Toast.makeText(this, "Pin entered is incorrect. Attempts: " + attempts, Toast.LENGTH_LONG).show();
    }

    // Handle pin success events
    @Override
    public void onPinSuccess(int attempts) {
        Toast.makeText(this, "Correct Pin", Toast.LENGTH_LONG).show();
    }

    // Override default pin length (e.g., 4 digits)
    @Override
    public int getPinLength() {
        return 4;
    }
}

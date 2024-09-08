package com.example.lockerapp.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lockerapp.R;
import com.suke.widget.SwitchButton;

public class SettingsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RelativeLayout topLayout;
    private SwitchButton appLockSwitch,lockScreenSwitch,intruderSelfieSwitch,showHidePatternSwitch,vibrateSwitch;
    private RelativeLayout lockWhenLayout,changePwdLayout,aboutMeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        btnBack = findViewById(R.id.btn_back);
        topLayout = findViewById(R.id.top_layout);
        appLockSwitch = findViewById(R.id.checkbox_app_lock_on_off);
        lockScreenSwitch = findViewById(R.id.checkbox_lock_screen_switch_on_phone_lock);
        intruderSelfieSwitch = findViewById(R.id.checkbox_intruder_selfie);
        showHidePatternSwitch = findViewById(R.id.checkbox_show_hide_pattern);
        vibrateSwitch = findViewById(R.id.checkbox_vibrate);
        lockWhenLayout = findViewById(R.id.lock_when);
        changePwdLayout = findViewById(R.id.btn_change_pwd);
        aboutMeLayout = findViewById(R.id.about_me);

        btnBack.setOnClickListener(v -> {
            // Handle back button press
            finish();
        });
    }
}

package com.example.robinblue.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.robinblue.PatternLockView.PatternLockView;
import com.example.robinblue.Services.ServiceApplock;
import com.robinblue.applockpro.R;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class SettingsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RelativeLayout topLayout;
    private ImageView lockWhenLayout;
    private PowerMenu powerMenu;
    private SwitchButton vibrationSwitch, hidepatternSwitch,fingerPrintSwitch,selfieIntruderSwitch;
    private int selectedPosition = 0;
    private PatternLockView patternLockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Initialize PaperDB
        Paper.init(this);

        btnBack = findViewById(R.id.btn_back);
        topLayout = findViewById(R.id.top_layout);
        lockWhenLayout = findViewById(R.id.lock_when);
        vibrationSwitch = findViewById(R.id.checkbox_vibrate);
        hidepatternSwitch = findViewById(R.id.checkbox_show_hide_pattern);
        fingerPrintSwitch = findViewById(R.id.fingerprint_switch);
        selfieIntruderSwitch = findViewById(R.id.checkbox_intruder_selfie);

        boolean isSelfieIntruderEnabled = Paper.book().read("selfie_intruder_enabled", false);
        selfieIntruderSwitch.setChecked(isSelfieIntruderEnabled);

        boolean isPatternHide = Paper.book().read("hide_enabled", false);
        hidepatternSwitch.setChecked(isPatternHide);

        boolean isVibrationEnabled = Paper.book().read("vibration_enabled", false);
        vibrationSwitch.setChecked(isVibrationEnabled);
        boolean isFingerPrintEnabled = Paper.book().read("fingerprint_enabled", false);// Set the switch state based on the saved value
        fingerPrintSwitch.setChecked(isFingerPrintEnabled);
        // PowerMenu setup
        List<PowerMenuItem> list = new ArrayList<>();
        list.add(new PowerMenuItem("Immediately", false));
        list.add(new PowerMenuItem("When device is locked", false));
        list.add(new PowerMenuItem("When device is locked or after exiting app", false));
        list.add(new PowerMenuItem("When device is locked or in 1 minute after exiting app", false));

        powerMenu = new PowerMenu.Builder(this)
                .addItemList(list)
                .setAnimation(MenuAnimation.DROP_DOWN)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setWidth(500)
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.font_deep_gray))
                .setTextGravity(Gravity.CENTER)
                .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setSelectedMenuColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setOnMenuItemClickListener(onMenuItemClickListener)
                .build();

        // Set OnClickListener for lockWhenLayout to show PowerMenu
        lockWhenLayout.setOnClickListener(v -> powerMenu.showAsDropDown(lockWhenLayout));
        // Load the previously saved lock condition (if any) from PaperDB
        selectedPosition = Paper.book().read("lock_condition", 0);  // Default to "Immediately" (position 0)
        powerMenu.setSelectedPosition(selectedPosition);
        btnBack.setOnClickListener(v -> finish());


        vibrationSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                if (isChecked) {
                    Paper.book().write("vibration_enabled", true);
                } else {
                    Paper.book().write("vibration_enabled", false);
                }
            }
        });

        hidepatternSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    Paper.book().write("hide_enabled", true);
                } else {
                    Paper.book().write("hide_enabled", false);
                }
            }
        });
        fingerPrintSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked){
                    Paper.book().write("fingerprint_enabled", false);
                }else{
                    Paper.book().write("fingerprint_enabled", true);
                }
            }
        });

        selfieIntruderSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked){
                    Paper.book().write("selfie_intruder_enabled", true);
                }else{
                    Paper.book().write("selfie_intruder_enabled", false);
                }
            }
        });

    }
    // Listener for PowerMenu item clicks
    private final OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener = (position, item) -> {
        selectedPosition = position;  // Save the selected position
        Paper.book().write("lock_condition", selectedPosition);  // Save to PaperDB
        powerMenu.setSelectedPosition(position);  // Update the PowerMenu's selected item
        powerMenu.dismiss();
    };

}

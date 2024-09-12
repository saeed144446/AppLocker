package com.example.robinblue.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.robinblue.applockpro.R;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RelativeLayout topLayout;
    private SwitchButton appLockSwitch, lockScreenSwitch, intruderSelfieSwitch, showHidePatternSwitch, vibrateSwitch;
    private RelativeLayout  changePwdLayout, aboutMeLayout;

    private ImageView lockWhenLayout;
    private PowerMenu powerMenu;

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


        // Spinner setup
        String[] lockTimes = {"Immediately", "When device is locked", "When device is unlocked or after exiting app", "When device is locked or in 1 minute after exiting app"};


        // PowerMenu setup
        List<PowerMenuItem> list = new ArrayList<>();
        list.add(new PowerMenuItem("Immediately", false));
        list.add(new PowerMenuItem("When device is locked", false));
        list.add(new PowerMenuItem("When device is unlocked or after exiting app", false));
        list.add(new PowerMenuItem("When device is locked or in 1 minute after exiting app", false));


        powerMenu = new PowerMenu.Builder(this)
                .addItemList(list) // Menu items
                .setAnimation(MenuAnimation.DROP_DOWN) // Animation
                .setMenuRadius(10f) // Corner radius
                .setMenuShadow(10f) // Shadow
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
        lockWhenLayout.setOnClickListener(v -> {
            powerMenu.showAsDropDown(lockWhenLayout); // Show PowerMenu below the layout
        });

        btnBack.setOnClickListener(v -> {
            // Handle back button press
            finish();
        });

        changePwdLayout.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, PatternLockAct.class);
            intent.putExtra("change_pattern", true);
            startActivity(intent);
        });

        showHidePatternSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle switch state change
            if (isChecked) {
                // Switch is on
            } else {
                // Switch is off
            }
        });
    }

    // Listener for PowerMenu item clicks
    private final OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener = (position, item) -> {
       // Toast.makeText(SettingsActivity.this, item., Toast.LENGTH_SHORT).show();
        powerMenu.setSelectedPosition(position); // Change selected item
        powerMenu.dismiss();
    };
}

package com.example.lockerapp.Activity;

import static com.google.android.material.tabs.TabLayout.GRAVITY_START;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.lockerapp.Adapter.MyAdapter;
import com.example.lockerapp.Model.AppItem;
import com.example.lockerapp.R;
import com.example.lockerapp.Utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 100;
    private static final int USAGE_ACCESS_PERMISSION_REQUEST_CODE = 101;

    private SearchView mEditSearch;
    public ArrayList<AppItem> appListArrayList;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyAdapter adapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menu_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditSearch = findViewById(R.id.search_view);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        menu_button = findViewById(R.id.btn_back);

        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.menu_settings){
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));

                } else if (id==R.id.menu_remove_ads) {


                } else if (id==R.id.menu_privacy_policy) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/digitalgraceapp/home"));
                    startActivity(intent);

                } else if (id==R.id.menu_more_apps) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=pub:Digital+Grace+App")));

                }else if (id==R.id.menu_share) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT,
                            "Check This App: https://play.google.com/store/apps/details?id=" + getPackageName());
                    shareIntent.setType("text/plain");
                    startActivity(shareIntent);

                }else if (id==R.id.menu_rate_us){
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("market://details?id=" + getPackageName()))));
                    } catch (ActivityNotFoundException e1) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("http://play.google.com/store/apps/details?id=" + getPackageName()))));
                        } catch (ActivityNotFoundException e2) {
                            Toast.makeText(MainActivity.this, "You don't have any app that can open this link", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                return false;
            }
        });


        tabLayout.addTab(tabLayout.newTab().setText("SYSTEM APPS"));
        tabLayout.addTab(tabLayout.newTab().setText("USER APPS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        // Initialize the adapter
        adapter = new MyAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No action needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No action needed
            }
        });

        // Check and request necessary permissions
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        // Check if Usage Access permission is granted
        if (!Utils.checkPermission(this)) {
            requestUsageAccessPermission();
        }

        // Check if Overlay (Draw over other apps) permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            requestOverlayPermission();
        }
    }

    private void requestUsageAccessPermission() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent, USAGE_ACCESS_PERMISSION_REQUEST_CODE);
    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            // Check if the overlay permission is granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Overlay permission is required for app lock functionality", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == USAGE_ACCESS_PERMISSION_REQUEST_CODE) {
            // Check if the usage access permission is granted
            if (!Utils.checkPermission(this)) {
                Toast.makeText(this, "Usage Access permission is required for app lock functionality", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure that permissions are granted before proceeding with app logic
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!Utils.checkPermission(this)) {
                requestUsageAccessPermission();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                requestOverlayPermission();
            }
        }
    }


}

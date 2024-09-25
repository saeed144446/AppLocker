package com.example.robinblue.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.robinblue.Adapter.MyAdapter;
import com.example.robinblue.Model.AppItem;
import com.robinblue.applockpro.R;
import com.example.robinblue.Utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 100;
    private static final int USAGE_ACCESS_PERMISSION_REQUEST_CODE = 101;

    private EditText search_view;
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

        search_view = findViewById(R.id.search_app);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        menu_button = findViewById(R.id.btn_back);

        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        search_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        // Initialize other components, adapters, and tabs as before

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }

                int id = item.getItemId();

                if (id == R.id.menu_settings) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));

                } else if (id == R.id.menu_remove_ads) {
                    // TODO: Handle removing ads logic here

                } else if (id == R.id.menu_privacy_policy) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://robinbluepage.blogspot.com/p/privacy-policy.html"));
                    startActivity(intent);

                } else if (id == R.id.menu_more_apps) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=pub:Robin+Blue+Gaming")));

                } else if (id == R.id.menu_share) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Check This App: https://play.google.com/store/apps/details?id=" + getPackageName());
                    shareIntent.setType("text/plain");
                    startActivity(shareIntent);

                } else if (id == R.id.menu_rate_us) {
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
        if (!Utils.checkPermission(this)) {
            requestUsageAccessPermission();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            if (isXiaomiDevice()) {
                requestOverlayPermissionXiaomi();
            } else {
                requestOverlayPermission();
            }
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

    private void requestOverlayPermissionXiaomi() {
        try {
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", getPackageName());
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            // Fallback if Xiaomi-specific intent fails
            requestOverlayPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Overlay permission is required for app lock functionality", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == USAGE_ACCESS_PERMISSION_REQUEST_CODE) {
            if (!Utils.checkPermission(this)) {
                Toast.makeText(this, "Usage Access permission is required for app lock functionality", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!Utils.checkPermission(this)) {
                requestUsageAccessPermission();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                if (isXiaomiDevice()) {
                    requestOverlayPermissionXiaomi();
                } else {
                    requestOverlayPermission();
                }
            }
        }
    }

    private void filter(String text) {
        if (appListArrayList == null) {
            // Avoid NullPointerException by checking if the list is null
            return;
        }

        ArrayList<AppItem> filteredList = new ArrayList<>();
        for (AppItem item : appListArrayList) {
            // If app name contains the search query
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        // Update RecyclerView or ViewPager Adapter with filtered data
        adapter.updateList(filteredList);
    }

    private boolean isXiaomiDevice() {
        return "Xiaomi".equalsIgnoreCase(Build.MANUFACTURER);
    }
}

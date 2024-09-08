package com.example.lockerapp.Fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lockerapp.Adapter.MainAdapter;
import com.example.lockerapp.R;
import com.example.lockerapp.Model.AppItem;

import java.util.ArrayList;
import java.util.List;

public class UserAppFrag extends Fragment {
    private MainAdapter adapter;
    private RecyclerView recyclerView;
    public ArrayList<AppItem> userAppList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_app, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        adapter = new MainAdapter(getContext(), getAllApps());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }

    // Add the getAdapter() method


    private ArrayList<AppItem> getAllApps() {
        ArrayList<AppItem> results = new ArrayList<>();

        PackageManager packageManager = getContext().getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfoList) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            String packageName = activityInfo.packageName;

            // Check if the app is a system app
            boolean isSystemApp = (activityInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0;

            // Only add non-system apps (user-installed apps)
            if (!isSystemApp) {
                results.add(new AppItem(activityInfo.loadIcon(packageManager), activityInfo.loadLabel(packageManager).toString(), packageName));
            }
        }
        return results;
    }

    public void updateAppList(ArrayList<AppItem> newSystemAppList) {
        userAppList.clear();
        userAppList.addAll(newSystemAppList);
        adapter.notifyDataSetChanged();  // Notify the adapter of data change
    }
}

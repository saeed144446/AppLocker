package com.example.robinblue.Adapter;


import android.content.Context;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.robinblue.Fragment.SystemAppFrag;
import com.example.robinblue.Fragment.UserAppFrag;
import com.example.robinblue.Model.AppItem;

import java.util.ArrayList;

public class MyAdapter extends FragmentPagerAdapter {
    private ArrayList<AppItem> appList;
    private Context context;
    private int totalTabs;

    // Constructor
    public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
        this.appList = new ArrayList<>(); // Initialize with an empty list or some default list
    }

    // Add the updateList() method to update the adapter's data
    public void updateList(ArrayList<AppItem> newList) {
        this.appList.clear();
        this.appList.addAll(newList);
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    @Override
    public Fragment getItem(int position) {
        // Logic to return the fragment based on the position
        switch (position) {
            case 0:
                // Return fragment for SYSTEM APPS
                return new SystemAppFrag();
            case 1:
                // Return fragment for USER APPS
                return new UserAppFrag();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}

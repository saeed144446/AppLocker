package com.example.robinblue.Adapter;


import android.content.Context;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.robinblue.Fragment.SystemAppFrag;
import com.example.robinblue.Fragment.UserAppFrag;

public class MyAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;
    public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }
    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                SystemAppFrag systemAppFrag = new SystemAppFrag();
                return systemAppFrag;
            case 1:
                UserAppFrag userAppFrag = new UserAppFrag();
                return userAppFrag;

            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}

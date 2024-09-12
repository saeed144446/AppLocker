package com.example.robinblue.Model;

import android.graphics.drawable.Drawable;

public class AppItem {

    private Drawable icon;
    private String name ;
    private String packagename;

    public AppItem(Drawable icon, String name, String packagename) {
        this.icon = icon;
        this.name = name;
        this.packagename = packagename;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }
}

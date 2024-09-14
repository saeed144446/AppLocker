package com.example.robinblue.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robinblue.Model.AppItem;
import com.robinblue.applockpro.R;
import com.example.robinblue.Utils.Utils;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<AppItem> appItemsList;
    private ArrayList<AppItem> filteredAppItemsList;  // For storing filtered results
    private Utils utils;

    public MainAdapter(Context mContext, ArrayList<AppItem> appItemsList) {
        this.mContext = mContext;
        this.appItemsList = appItemsList;
        this.filteredAppItemsList = new ArrayList<>(appItemsList); // Initialize with full list
        this.utils = new Utils(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_main_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppItem appItem = filteredAppItemsList.get(position);  // Use filtered list
        holder.mAppIcon.setImageDrawable(appItem.getIcon());
        holder.mAppName.setText(appItem.getName());

        String appPackageName = appItem.getPackagename();
        boolean isLocked = utils.isLocked(appPackageName);
        holder.mSwitchCompat.setOnCheckedChangeListener(null);
        holder.mSwitchCompat.setChecked(isLocked);

        holder.mSwitchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                utils.lockApp(appPackageName);
                Toast.makeText(mContext, appItem.getName() + " is Locked", Toast.LENGTH_SHORT).show();
            } else {
                utils.unlockApp(appPackageName);
                Toast.makeText(mContext, appItem.getName() + " is Unlocked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredAppItemsList.size();  // Use filtered list size
    }
    // Method to filter apps based on search text
    public void filter(String query) {
        query = query.toLowerCase();
        filteredAppItemsList.clear();
        if (query.isEmpty()) {
            filteredAppItemsList.addAll(appItemsList);  // If no query, show full list
        } else {
            for (AppItem appItem : appItemsList) {
                if (appItem.getName().toLowerCase().contains(query)) {
                    filteredAppItemsList.add(appItem);
                }
            }
        }
        notifyDataSetChanged();  // Refresh the adapter
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mAppIcon;
        public TextView mAppName;
        public SwitchButton mSwitchCompat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAppIcon = itemView.findViewById(R.id.app_icon);
            mAppName = itemView.findViewById(R.id.app_name);
            mSwitchCompat = itemView.findViewById(R.id.switch_compat);
        }
    }
}

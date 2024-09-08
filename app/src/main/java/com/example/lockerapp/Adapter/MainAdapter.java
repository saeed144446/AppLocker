package com.example.lockerapp.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lockerapp.Interface.AppOnClickListner;
import com.example.lockerapp.Model.AppItem;
import com.example.lockerapp.R;
import com.example.lockerapp.Utils.Utils;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<AppItem> appItemsList;
    private Utils utils;
    private SharedPreferences preferences;

    public MainAdapter(Context mContext, ArrayList<AppItem> appItemsList) {
        this.mContext = mContext;
        this.appItemsList = appItemsList;
        preferences = mContext.getSharedPreferences("AppLockPrefs", Context.MODE_PRIVATE);
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
        AppItem appItem = appItemsList.get(position);
        holder.mAppIcon.setImageDrawable(appItem.getIcon());
        holder.mAppName.setText(appItem.getName());
        String appPackageName = appItem.getPackagename();

        // Correctly set the switch state based on the current lock status
        boolean isLocked = utils.isLocked(appPackageName);
        holder.mSwitchCompat.setOnCheckedChangeListener(null); // Remove any existing listener
        holder.mSwitchCompat.setChecked(isLocked); // Set the correct state

        holder.mSwitchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                utils.Lock(appPackageName);
                Toast.makeText(mContext, appItem.getName()+" is Locked", Toast.LENGTH_SHORT).show();
            } else {
                utils.unLock(appPackageName);
                Toast.makeText(mContext, appItem.getName()+" is Unlocked", Toast.LENGTH_SHORT).show();
            }
        });

        holder.setAppOnClickListner(pos -> {
            if (utils.isLocked(appPackageName)) {
                holder.mSwitchCompat.setChecked(false);
                utils.unLock(appPackageName);
                Toast.makeText(mContext, appItem.getName()+" is Locked", Toast.LENGTH_SHORT).show();
            } else {
                holder.mSwitchCompat.setChecked(true);
                utils.Lock(appPackageName);
                Toast.makeText(mContext, appItem.getName()+" is Unlocked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return appItemsList.size();
    }

    // Inner ViewHolder Class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mAppIcon;
        public TextView mAppName;
        public SwitchButton mSwitchCompat;

        public AppOnClickListner appOnClickListner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAppIcon = itemView.findViewById(R.id.app_icon);
            mAppName = itemView.findViewById(R.id.app_name);
            mSwitchCompat = itemView.findViewById(R.id.switch_compat);

            itemView.setOnClickListener(v -> {
                if (appOnClickListner != null) {
                    appOnClickListner.onClick(getAdapterPosition());
                } else {
                    throw new NullPointerException("AppOnClickListner is not set for this ViewHolder.");
                }
            });
        }

        public void setAppOnClickListner(AppOnClickListner appOnClickListner) {
            this.appOnClickListner = appOnClickListner;
        }
    }
}

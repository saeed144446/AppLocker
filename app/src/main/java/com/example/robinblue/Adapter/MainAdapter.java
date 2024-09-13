package com.example.robinblue.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robinblue.Interface.AppOnClickListner;
import com.example.robinblue.Model.AppItem;
import com.robinblue.applockpro.R;
import com.example.robinblue.Utils.Utils;
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

        holder.setAppOnClickListner(pos -> {
            if (utils.isLocked(appPackageName)) {
                holder.mSwitchCompat.setChecked(false);
                utils.unlockApp(appPackageName);
                Toast.makeText(mContext, appItem.getName() + " is Unlocked", Toast.LENGTH_SHORT).show();
            } else {
                holder.mSwitchCompat.setChecked(true);
                utils.lockApp(appPackageName);
                Toast.makeText(mContext, appItem.getName() + " is Locked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return appItemsList.size();
    }

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

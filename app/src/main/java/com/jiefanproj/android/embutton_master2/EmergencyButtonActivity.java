package com.jiefanproj.android.embutton_master2;

import com.jiefanproj.android.embutton_master2.alert.EmergencyAlert;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class EmergencyButtonActivity extends Activity {
    public static final int ADD_TO_TOP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(">>>>>", "Registering Restart Install receiver");
        registerRestartInstallReceiver();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        View alertStatusStrip = LayoutInflater.from(this).inflate(R.layout.alert_status_strip, getRootLayout(), false);
        getRootLayout().addView(alertStatusStrip, ADD_TO_TOP);
    }

    private int alertStatusStripColor() {
        return ApplicationSettings.isAlertActive(this) ?
                getResources().getColor(R.color.active_color) : getResources().getColor(R.color.standby_color);
    }

    private ViewGroup getRootLayout() {
        return (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAlertStatusStrip();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(activityFinishReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void updateAlertStatusStrip() {
        findViewById(R.id.alert_status_strip).setBackgroundColor(alertStatusStripColor());
    }

    EmergencyAlert getEmergencyAlert() {
        return new EmergencyAlert(this);
    }


    public void registerRestartInstallReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.jiefanproj.android.embutton_master2.RESTART_INSTALL");
        registerReceiver(activityFinishReceiver, intentFilter);
    }

    BroadcastReceiver activityFinishReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.jiefanproj.android.embutton_master2.RESTART_INSTALL")) {
                finish();
            }
        }
    };
}
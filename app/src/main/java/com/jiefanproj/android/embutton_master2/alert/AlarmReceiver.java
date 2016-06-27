package com.jiefanproj.android.embutton_master2.alert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.jiefanproj.android.embutton_master2.ApplicationSettings;
import com.jiefanproj.android.embutton_master2.Intents;


public class AlarmReceiver extends BroadcastReceiver {

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(">>>>>>>>", "onReceive - AlarmReceiver");
        mContext = context.getApplicationContext();
        if(intent.getAction().equals(Intents.SEND_ALERT_ACTION)) {
            Log.e(">>>>>>>>", "alert update received in AlarmReceiver at current time in millis = " + System.currentTimeMillis() % 100000);
            getEmergencyMessage(mContext).sendAlertMessage(getCurrentBestLocation());

            if (!ApplicationSettings.isFirstMsgWithLocationTriggered(context)) {
                ApplicationSettings.setFirstMsgWithLocationTriggered(context, true);
            }
        } else if(intent.getAction().equals(Intents.SEND_ALERT_ACTION_SINGLE)) {
            Log.e(">>>>>>>>", "alert update(single) received in AlarmReceiver at current time in millis = " + System.currentTimeMillis() % 100000);

            if (!ApplicationSettings.isFirstMsgWithLocationTriggered(context)) {
                getEmergencyMessage(mContext).sendAlertMessage(getCurrentBestLocation());
                ApplicationSettings.setFirstMsgWithLocationTriggered(context, true);
            }
        }
    }

    EmergencyMessage getEmergencyMessage(Context context) {
        return new EmergencyMessage(mContext);
    }

    Location getCurrentBestLocation() {
        return ApplicationSettings.getCurrentBestLocation(mContext);
    }
}

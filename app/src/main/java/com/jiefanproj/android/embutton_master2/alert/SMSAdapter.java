package com.jiefanproj.android.embutton_master2.alert;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import com.jiefanproj.android.embutton_master2.ApplicationSettings;

public class SMSAdapter {
    private static final String LOG_TAG = SMSAdapter.class.getName();

    public void sendSMS(Context context, String phoneNumber, String message) {
//        Log.e("20140411", "sending fake SMS -> " + message);
        if(!ApplicationSettings.isFirstMsgSent(context)){
            ApplicationSettings.setFirstMsgSent(context, true);
        }
        SmsManager smsManager = getSmsManager();
        try {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.i(LOG_TAG, "Sms sent: " + message);
        } catch (Exception exception) {
            Log.e(LOG_TAG, "Sending SMS failed " + exception.getMessage());
        }
    }

    SmsManager getSmsManager() {
        return SmsManager.getDefault();
    }
}
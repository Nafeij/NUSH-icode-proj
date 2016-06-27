package com.jiefanproj.android.embutton_master2.trigger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import com.jiefanproj.android.embutton_master2.alert.EmergencyAlert;

import static android.content.Intent.ACTION_SCREEN_OFF;
import static android.content.Intent.ACTION_SCREEN_ON;

public class HardwareTriggerReceiver extends BroadcastReceiver {
    private static final String TAG = HardwareTriggerReceiver.class.getName();
    //    private MultiClickEvent multiClickEvent;
    protected MultiClickEvent multiClickEvent;

    public HardwareTriggerReceiver() {
        resetEvent();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(">>>>>>>", "in onReceive of HWReceiver");
        String action = intent.getAction();
        if (!isCallActive(context) && (action.equals(ACTION_SCREEN_OFF) || action.equals(ACTION_SCREEN_ON))) {
            multiClickEvent.registerClick(System.currentTimeMillis());

            if(multiClickEvent.skipCurrentClick()){
                Log.e("*****", "skipped click");
                multiClickEvent.resetSkipCurrentClickFlag();
            }

            else if(multiClickEvent.canStartVibration()){
                Log.e("*****", "vibration started");
                EmergencyAlert emergencyAlert = getEmergencyAlert(context);
                emergencyAlert.vibrate();
            }

            else if (multiClickEvent.isActivated()) {
                Log.e("*****", "alerts activated");
                onActivation(context);
                resetEvent();
            }
        }
    }

    protected void onActivation(Context context) {
        Log.e(">>>>>>>", "in onActivation of HWReceiver");
        activateAlert(getEmergencyAlert(context));
    }

    void activateAlert(EmergencyAlert emergencyAlert) {
//        emergencyAlert.start();
        emergencyAlert.activate();
    }

    protected void resetEvent() {
        multiClickEvent = new MultiClickEvent();
    }

    protected EmergencyAlert getEmergencyAlert(Context context) {
        return new EmergencyAlert(context);
    }

    private boolean isCallActive(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getMode() == AudioManager.MODE_IN_CALL;
    }
}

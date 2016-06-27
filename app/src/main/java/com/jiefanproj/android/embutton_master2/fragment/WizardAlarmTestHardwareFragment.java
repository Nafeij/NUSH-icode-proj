package com.jiefanproj.android.embutton_master2.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiefanproj.android.embutton_master2.R;
import com.jiefanproj.android.embutton_master2.WizardActivity;
import com.jiefanproj.android.embutton_master2.AppConstants;
import com.jiefanproj.android.embutton_master2.AppUtil;
import com.jiefanproj.android.embutton_master2.ApplicationSettings;
import com.jiefanproj.android.embutton_master2.MyTagHandler;
import com.jiefanproj.android.embutton_master2.data.PBDatabase;
import com.jiefanproj.android.embutton_master2.model.Page;
import com.jiefanproj.android.embutton_master2.trigger.HardwareTriggerReceiver;

import java.util.HashMap;

/**
 * v 2.0.1.1
 */
public class WizardAlarmTestHardwareFragment extends Fragment {

    private static final String PAGE_ID = "page_id";
    private HashMap<String, Drawable> mImageCache = new HashMap<String, Drawable>();
    private Activity activity;

    DisplayMetrics metrics;
    PowerManager.WakeLock wakeLock;

    TextView tvContent;

    Page currentPage;

    public static WizardAlarmTestHardwareFragment newInstance(String pageId) {
        WizardAlarmTestHardwareFragment f = new WizardAlarmTestHardwareFragment();
        Bundle args = new Bundle();
        args.putString(PAGE_ID, pageId);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_type_interactive_test_hardware, container, false);

        tvContent = (TextView) view.findViewById(R.id.fragment_contents);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        if (activity != null) {
            metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "TEST");

            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            activity.registerReceiver(wizardHardwareReceiver, filter);

            String pageId = getArguments().getString(PAGE_ID);
            String selectedLang = ApplicationSettings.getSelectedLanguage(activity);

            PBDatabase dbInstance = new PBDatabase(activity);
            dbInstance.open();
            currentPage = dbInstance.retrievePage(pageId, selectedLang);
            dbInstance.close();

            if (currentPage.getContent() == null)
                tvContent.setVisibility(View.GONE);
            else {
                Log.e(">>>>>", "content = " + currentPage.getContent());
                tvContent.setText(Html.fromHtml(currentPage.getContent(), null, new MyTagHandler()));
                AppUtil.updateImages(true, currentPage.getContent(), activity, metrics, tvContent, AppConstants.IMAGE_FULL_WIDTH);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(">>>>>", "onPause WizardAlarmTestHardwareFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(">>>>>", "onResume WizardAlarmTestHardwareFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("????", "onDestroy");
        activity.unregisterReceiver(wizardHardwareReceiver);
    }


    private BroadcastReceiver wizardHardwareReceiver = new HardwareTriggerReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            Log.e("????????", "in onReceive in WizardAlarmTest");
            ((WizardActivity) getActivity()).hideToastMessageInInteractiveFragment();

            getActivity().onUserInteraction();
        }

        @Override
        public void onActivation(Context context) {
            Log.e(">>>>>>>", "in onActivation of wizardHWReceiver");

            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(AppConstants.HAPTIC_FEEDBACK_DURATION);

            String pageId = currentPage.getSuccessId();

            Intent i = new Intent(activity, WizardActivity.class);
            i.putExtra("page_id", pageId);
            activity.startActivity(i);
            activity.finish();
        }

    };
}
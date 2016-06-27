package com.jiefanproj.android.embutton_master2;


import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jiefanproj.android.embutton_master2.data.PBDatabase;
import com.jiefanproj.android.embutton_master2.fragment.*;
import com.jiefanproj.android.embutton_master2.model.Page;
import com.jiefanproj.android.embutton_master2.trigger.HardwareTriggerService;

public class WizardActivity extends BaseFragmentActivity {

    Page currentPage;
    String pageId;
    String selectedLang;

    TextView tvToastMessage;
    Boolean flagRiseFromPause = false;

    private static final int PICK_CONTACT_REQUEST_ID = 65636;
    private static final int RESULT_OK = -1;

    private Handler inactiveHandler = new Handler();

    ContactPickerFragment contactPickerFragment = new ContactPickerFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_layout);

        tvToastMessage = (TextView) findViewById(R.id.tv_toast);

        try {
            pageId = getIntent().getExtras().getString("page_id");
        } catch (Exception e) {
            pageId = "home-not-configured";
            e.printStackTrace();
        }
        selectedLang = ApplicationSettings.getSelectedLanguage(this);

        Log.e("WizardActivity.onCreate", "pageId = " + pageId);

        PBDatabase dbInstance = new PBDatabase(this);
        dbInstance.open();
        currentPage = dbInstance.retrievePage(pageId, selectedLang);
        dbInstance.close();

        if (currentPage == null) {
            Log.e(">>>>>>", "page = null");
            Toast.makeText(this, "Still to be implemented.", Toast.LENGTH_SHORT).show();
            AppConstants.PAGE_FROM_NOT_IMPLEMENTED = true;
            finish();
            return;
        } else if (currentPage.getId().equals("home-ready")) {
//            ApplicationSettings.setFirstRun(WizardActivity.this, false);
            ApplicationSettings.setWizardState(WizardActivity.this, AppConstants.WIZARD_FLAG_HOME_READY);
            changeAppIcontoCalculator();

            startService(new Intent(this, HardwareTriggerService.class));

            Intent i = new Intent(WizardActivity.this, MainActivity.class);
            i.putExtra("page_id", pageId);
            startActivity(i);

            callFinishActivityReceiver();

            finish();
            return;
        } else {

            if (currentPage.getId().equals("home-not-configured")) {
                ApplicationSettings.setWizardState(WizardActivity.this, AppConstants.WIZARD_FLAG_HOME_NOT_CONFIGURED);
            } else if (currentPage.getId().equals("home-not-configured-alarm")) {
                ApplicationSettings.setWizardState(WizardActivity.this, AppConstants.WIZARD_FLAG_HOME_NOT_CONFIGURED_ALARM);
            } else if (currentPage.getId().equals("home-not-configured-disguise")) {
                ApplicationSettings.setWizardState(WizardActivity.this, AppConstants.WIZARD_FLAG_HOME_NOT_CONFIGURED_DISGUISE);
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment fragment = null;

            if (currentPage.getType().equals("simple")) {
                tvToastMessage.setVisibility(View.INVISIBLE);
                fragment = new SimpleFragment().newInstance(pageId, AppConstants.FROM_WIZARD_ACTIVITY);
            } else if (currentPage.getType().equals("warning")) {
                tvToastMessage.setVisibility(View.INVISIBLE);
                fragment = new WarningFragment().newInstance(pageId, AppConstants.FROM_WIZARD_ACTIVITY);
            } else {          // type = interactive
                if (currentPage.getComponent().equals("contacts"))
                    fragment = new SetupContactsFragment().newInstance(pageId, AppConstants.FROM_WIZARD_ACTIVITY);
                else if (currentPage.getComponent().equals("message"))
                    fragment = new SetupMessageFragment().newInstance(pageId, AppConstants.FROM_WIZARD_ACTIVITY);
                else if (currentPage.getComponent().equals("code"))
                    fragment = new SetupCodeFragment().newInstance(pageId, AppConstants.FROM_WIZARD_ACTIVITY);
                else if (currentPage.getComponent().equals("language"))
                    fragment = new LanguageSettingsFragment().newInstance(pageId, AppConstants.FROM_WIZARD_ACTIVITY);
                else if (currentPage.getComponent().equals("alarm-test-hardware")) {
                    tvToastMessage.setVisibility(View.VISIBLE);
                    if (currentPage.getIntroduction() != null) {
                        tvToastMessage.setText(Html.fromHtml(currentPage.getIntroduction(), null, new MyTagHandler()));
                    }
                    fragment = new WizardAlarmTestHardwareFragment().newInstance(pageId);
                } else if (currentPage.getComponent().equals("alarm-test-disguise")) {
                    tvToastMessage.setVisibility(View.VISIBLE);
                    if (currentPage.getIntroduction() != null) {
                        tvToastMessage.setText(Html.fromHtml(currentPage.getIntroduction(), null, new MyTagHandler()));
                    }
                    fragment = new WizardAlarmTestDisguiseFragment().newInstance(pageId);
                } else if (currentPage.getComponent().equals("disguise-test-open")) {
                    findViewById(R.id.wizard_layout_root).setBackgroundColor(Color.BLACK);
                    tvToastMessage.setVisibility(View.VISIBLE);
                    if (currentPage.getIntroduction() != null) {
                        tvToastMessage.setText(Html.fromHtml(currentPage.getIntroduction(), null, new MyTagHandler()));
                    }
                    fragment = new WizardTestDisguiseOpenFragment().newInstance(pageId);
                } else if (currentPage.getComponent().equals("disguise-test-unlock")) {
                    tvToastMessage.setVisibility(View.VISIBLE);
                    if (currentPage.getIntroduction() != null) {
                        tvToastMessage.setText(Html.fromHtml(currentPage.getIntroduction(), null, new MyTagHandler()));
                    }

                    fragment = new WizardTestDisguiseUnlockFragment().newInstance(pageId);
                } else if (currentPage.getComponent().equals("disguise-test-code")) {
                    tvToastMessage.setVisibility(View.VISIBLE);
                    if (currentPage.getIntroduction() != null) {
                        tvToastMessage.setText(Html.fromHtml(currentPage.getIntroduction(), null, new MyTagHandler()));
                    }
                    fragment = new WizardTestDisguiseCodeFragment().newInstance(pageId);
                } else
                    fragment = new SimpleFragment().newInstance(pageId, AppConstants.FROM_WIZARD_ACTIVITY);
            }
            fragmentTransaction.add(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
    }

    private void changeAppIcontoCalculator() {
        Log.e("WizardActivity.changeAppIcontoCalculator", "");

        getPackageManager().setComponentEnabledSetting(
                new ComponentName("com.jiefanproj.android.embutton_master2", "com.jiefanproj.android.embutton_master2.HomeActivity-calculator"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        getPackageManager().setComponentEnabledSetting(
                new ComponentName("com.jiefanproj.android.embutton_master2", "com.jiefanproj.android.embutton_master2.HomeActivity-setup"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("WizardActivity.onPause", "page = " + pageId);

//        if (currentPage.getId().equals("home-ready") && ApplicationSettings.isRestartedSetup(WizardActivity.this)) {
//            Log.e("WizardActivity.onPause", "false->RestartedSetup");
//            ApplicationSettings.setRestartedSetup(WizardActivity.this, false);
//        }


        if (!pageId.equals("setup-alarm-test-hardware")) {
            Log.e(">>>>>>", "assert flagRiseFromPause = " + true);
            flagRiseFromPause = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("WizardActivity.onStop", "page = " + pageId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("WizardActivity", "onDestroy");
        inactiveHandler.removeCallbacks(runnableInteractive);
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d("WizardActivity.onStart", "page = " + pageId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("WizardActivity.onResume", "pageId = " + pageId + " and flagRiseFromPause = " + flagRiseFromPause);

        int wizardState = ApplicationSettings.getWizardState(WizardActivity.this);


        if (AppConstants.PAGE_FROM_NOT_IMPLEMENTED) {
            Log.e("WizardActivity.onResume", "returning from not-implemented page.");
            AppConstants.PAGE_FROM_NOT_IMPLEMENTED = false;
            return;
        }


        if (AppConstants.IS_BACK_BUTTON_PRESSED) {
            Log.e("WizardActivity.onResume", "back button pressed");
            AppConstants.IS_BACK_BUTTON_PRESSED = false;
            return;
        }

        if (flagRiseFromPause && !pageId.equals("setup-alarm-test-hardware-success")) {
            flagRiseFromPause = false;

            if (wizardState == AppConstants.WIZARD_FLAG_HOME_NOT_CONFIGURED) {
                pageId = "home-not-configured";
            } else if (wizardState == AppConstants.WIZARD_FLAG_HOME_NOT_CONFIGURED_ALARM) {
                pageId = "home-not-configured-alarm";
            } else if (wizardState == AppConstants.WIZARD_FLAG_HOME_NOT_CONFIGURED_DISGUISE) {
                pageId = "home-not-configured-disguise";
            } else if (wizardState == AppConstants.WIZARD_FLAG_HOME_READY) {
                pageId = "home-ready";
            }

            Intent i = new Intent(WizardActivity.this, WizardActivity.class);
            i.putExtra("page_id", pageId);
            startActivity(i);

            callFinishActivityReceiver();
            finish();
        }
    }

    @Override
    public void onUserInteraction() {
        Log.e("WizardActivity", "onUserInteraction");
        super.onUserInteraction();
        hideToastMessageInInteractiveFragment();
        if (currentPage != null && currentPage.getComponent() != null &&
                (
                        currentPage.getComponent().equals("alarm-test-hardware")
                                || currentPage.getComponent().equals("alarm-test-disguise")
                                || currentPage.getComponent().equals("disguise-test-open")
                                || currentPage.getComponent().equals("disguise-test-unlock")
                                || currentPage.getComponent().equals("disguise-test-code")
                )
                ) {
            inactiveHandler.postDelayed(runnableInteractive, Integer.parseInt(currentPage.getTimers().getFail()) * 1000);
        }
    }

    public void hideToastMessageInInteractiveFragment() {
        tvToastMessage.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppConstants.IS_BACK_BUTTON_PRESSED = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//if ((requestCode == PICK_CONTACT_REQUEST_ID) && (resultCode == RESULT_OK)) {
//Context context = getApplicationContext();
//contactPickerFragment.onActivityResult(requestCode, resultCode, data, context);
//}

        Log.i("", "Called from WizardActivity");

        super.onActivityResult(requestCode, resultCode, data);
    }

    private Runnable runnableInteractive = new Runnable() {
        public void run() {

            String pageId = currentPage.getFailedId();

            Intent i = new Intent(WizardActivity.this, WizardActivity.class);
            i.putExtra("page_id", pageId);
            startActivity(i);
            finish();
        }
    };
}
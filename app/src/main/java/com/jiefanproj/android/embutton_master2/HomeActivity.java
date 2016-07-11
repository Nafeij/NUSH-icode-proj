package com.jiefanproj.android.embutton_master2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.jiefanproj.android.embutton_master2.data.PBDatabase;
import com.jiefanproj.android.embutton_master2.model.Page;
import com.jiefanproj.android.embutton_master2.trigger.HardwareTriggerService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class HomeActivity extends Activity {

    ProgressDialog pDialog;

    String pageId;
    String selectedLang;
//    String mobileDataUrl;
//    String helpDataUrl;

    int currentLocalContentVersion;
    int lastLocalContentVersion;
//    int latestVersion;
//    long lastRunTimeInMillis;
    int lastLocalDBVersion;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_screen);

        //deleteShortCut();

        //latestVersion = -1;

        int wizardState = ApplicationSettings.getWizardState(this);
        if (AppConstants.SKIP_WIZARD) {
            pageId = "home-ready";
        } else
        if (wizardState == AppConstants.WIZARD_FLAG_HOME_NOT_CONFIGURED) {
            pageId = "home-not-configured";
        } else if (wizardState == AppConstants.WIZARD_FLAG_HOME_NOT_CONFIGURED_ALARM) {
            pageId = "home-not-configured-alarm";
        } else if (wizardState == AppConstants.WIZARD_FLAG_HOME_NOT_CONFIGURED_DISGUISE) {
            pageId = "home-not-configured-disguise";
        } else if (wizardState == AppConstants.WIZARD_FLAG_HOME_READY) {
            pageId = "home-ready";
        }

        selectedLang = ApplicationSettings.getSelectedLanguage(this);
//        helpDataUrl = AppConstants.BASE_URL + AppConstants.HELP_DATA_URL;

//        lastRunTimeInMillis = ApplicationSettings.getLastRunTimeInMillis(this);


        lastLocalDBVersion = ApplicationSettings.getLastUpdatedDBVersion(this);
        if(lastLocalDBVersion < AppConstants.DATABASE_VERSION){
            Log.e("<<<<<", "local db version changed. needs a force update");
            ApplicationSettings.setLocalDataInsertion(this, false);
//            lastRunTimeInMillis = -1;
        }

        currentLocalContentVersion = ApplicationSettings.getLastUpdatedVersion(HomeActivity.this);

        try {
            JSONObject jsonObj = new JSONObject(loadJSONFromAsset("mobile_en.json"));
            JSONObject mobileObj = jsonObj.getJSONObject("mobile");

            lastLocalContentVersion = mobileObj.getInt("version");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        
		if (lastLocalContentVersion > currentLocalContentVersion) {
            Log.e("???????", "Update local data");
            new InitializeLocalData().execute();
        }
//        else if (!AppUtil.isToday(lastRunTimeInMillis) && AppUtil.hasInternet(HomeActivity.this)) {
//            Log.e(">>>>", "local data initialized but last run not today");
//            new GetLatestVersion().execute();
//        }
        else{
            Log.e(">>>>>", "no update needed");
            startNextActivity();
        }
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
//    	AppUtil.unbindDrawables(getWindow().getDecorView().findViewById(android.R.id.content));
//        System.gc();
    }


    private void deleteShortCut() {

        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.setClassName("com.jiefanproj.android.embutton_master2", "HomeActivity");
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent removeIntent = new Intent();
        removeIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        removeIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "ShortcutName");
        removeIntent.putExtra("duplicate", false);

        removeIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        sendBroadcast(removeIntent);
    }


    private void startNextActivity(){
        Log.e(">>>>>>>>>>>>", "starting next activity");

        int wizardState = ApplicationSettings.getWizardState(this);
        if (wizardState != AppConstants.WIZARD_FLAG_HOME_READY) {
            Log.e(">>>>>>", "first run TRUE, running WizardActivity with pageId = " + pageId);
            Intent i = new Intent(HomeActivity.this, WizardActivity.class);
            // Removing default homescreen shortcut.
            /*i.setAction(Intent.ACTION_MAIN);
            
            i.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            i.putExtra(Intent.EXTRA_SHORTCUT_NAME, "HelloWorldShortcut");
         
            i.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(i);*/
            i.putExtra("page_id", pageId);
            startActivity(i);
        } else {
            Log.e(">>>>>>", "first run FALSE, running CalculatorActivity");
            Intent i = new Intent(HomeActivity.this, CalculatorActivity.class);
            // Make sure the HardwareTriggerService is started
    		startService(new Intent(this, HardwareTriggerService.class));
            startActivity(i);
        }
    }

	private class InitializeLocalData extends AsyncTask<Void, Void, Boolean> {
		int lastUpdatedVersion;
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = ProgressDialog.show(HomeActivity.this, "Application", "Installing...", true, false);
        }
        
        @Override
        protected Boolean doInBackground(Void... params) {
	        try {
	            JSONObject jsonObj = new JSONObject(loadJSONFromAsset("mobile_en.json"));
	            JSONObject mobileObj = jsonObj.getJSONObject("mobile");

	            lastUpdatedVersion = mobileObj.getInt("version");
	            ApplicationSettings.setLastUpdatedVersion(HomeActivity.this, lastUpdatedVersion);

	            JSONArray dataArray = mobileObj.getJSONArray("data");
	            insertMobileDataToLocalDB(dataArray);
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }

	        try {
	            JSONObject jsonObj = new JSONObject(loadJSONFromAsset("help_en.json"));
	            JSONObject mobileObj = jsonObj.getJSONObject("help");

	            JSONArray dataArray = mobileObj.getJSONArray("data");
	            insertMobileDataToLocalDB(dataArray);
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }

	        return true;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if (pDialog.isShowing())
				try {
					pDialog.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}

            ApplicationSettings.setLocalDataInsertion(HomeActivity.this, true);
            ApplicationSettings.setLastUpdatedDBVersion(HomeActivity.this, AppConstants.DATABASE_VERSION);

            startNextActivity();
        }
    }

    private void insertMobileDataToLocalDB(JSONArray dataArray) {
        List<Page> pageList = Page.parsePages(dataArray);

        PBDatabase dbInstance = new PBDatabase(HomeActivity.this);
        dbInstance.open();

        for (int i = 0; i < pageList.size(); i++) {
            dbInstance.insertOrUpdatePage(pageList.get(i));
        }
        dbInstance.close();
    }

    public String loadJSONFromAsset(String jsonFileName) {
        String json = null;
        try {
            InputStream is = getAssets().open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
package com.jiefanproj.android.embutton_master2.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.jiefanproj.android.embutton_master2.MainActivity;
import com.jiefanproj.android.embutton_master2.R;
import com.jiefanproj.android.embutton_master2.WizardActivity;
import com.jiefanproj.android.embutton_master2.AppConstants;
import com.jiefanproj.android.embutton_master2.AppUtil;
import com.jiefanproj.android.embutton_master2.ApplicationSettings;
import com.jiefanproj.android.embutton_master2.model.PageAction;

import java.util.List;
import java.util.Locale;

/**
 * v 2.0.1.1
 */
public class PageLanguageSettingsAdapter extends ArrayAdapter<PageAction> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ProgressDialog pDialog;
    private String currentLang;
    private String selectedLang;
    private int lastUpdatedVersion;
    private int latestVersion;
    private int parentActivity;

    public PageLanguageSettingsAdapter(Context context, int parentActivity) {
        super(context, R.layout.row_page_language_settings);
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.currentLang = ApplicationSettings.getSelectedLanguage(mContext);
        latestVersion = -1;
        lastUpdatedVersion = ApplicationSettings.getLastUpdatedVersion(mContext);
        this.parentActivity = parentActivity;
    }


    private static class ViewHolder {
        Button bAction;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_page_language_settings, null);

            holder = new ViewHolder();
            holder.bAction = (Button) convertView.findViewById(R.id.b_action);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PageAction item = getItem(position);

        holder.bAction.setText(item.getTitle());
        holder.bAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = null;
                selectedLang = item.getLanguage();

                if (currentLang.equals(selectedLang)) {
                    AppUtil.showToast("Language already applied.", Toast.LENGTH_SHORT, mContext);

                    restartApp();

                    ((Activity) mContext).finish();
                    return;
                }
                changeStaticLanguageSettings(((item.getConfirmation() == null) ? AppConstants.DEFAULT_CONFIRMATION_MESSAGE : item.getConfirmation()));
//                new GetLatestVersion(((item.getConfirmation() == null) ? AppConstants.DEFAULT_CONFIRMATION_MESSAGE : item.getConfirmation())).execute();

            }
        });

        return convertView;
    }

    public void restartApp(){

        int wizardState = ApplicationSettings.getWizardState(mContext);
        String pageId = null;
        if (wizardState == AppConstants.WIZARD_FLAG_HOME_NOT_CONFIGURED) {
            pageId = "home-not-configured";
        } else if (wizardState == AppConstants.WIZARD_FLAG_HOME_NOT_CONFIGURED_ALARM) {
            pageId = "home-not-configured-alarm";
        } else if (wizardState == AppConstants.WIZARD_FLAG_HOME_NOT_CONFIGURED_DISGUISE) {
            pageId = "home-not-configured-disguise";
        } else if (wizardState == AppConstants.WIZARD_FLAG_HOME_READY) {
            pageId = "home-ready";
        }

        Log.e(">>>>>>>", "restarting app with pageId = " + pageId);

        if(parentActivity == AppConstants.FROM_WIZARD_ACTIVITY){
            Intent i = new Intent(mContext, WizardActivity.class);
            i.putExtra("page_id", pageId);
            mContext.startActivity(i);

            ((WizardActivity) mContext).callFinishActivityReceiver();
        } else{
            Intent i = new Intent(mContext, MainActivity.class);
            i.putExtra("page_id", pageId);
            mContext.startActivity(i);

            ((MainActivity) mContext).callFinishActivityReceiver();
        }

       	((Activity) mContext).finish();
    }
    
    public void setData(List<PageAction> actionList) {
        clear();
        if (actionList != null) {
            for (int i = 0; i < actionList.size(); i++) {
                add(actionList.get(i));
            }
        }
    }


    private void changeStaticLanguageSettings(String confirmation) {
        Toast.makeText(mContext, confirmation, Toast.LENGTH_SHORT).show();
        ApplicationSettings.setSelectedLanguage(mContext, selectedLang);

        Resources res = mContext.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();

        conf.locale = new Locale(selectedLang);
        res.updateConfiguration(conf, dm);

        restartApp();
    }
}

package com.jiefanproj.android.embutton_master2.fragment;

import android.graphics.Color;

import com.jiefanproj.android.embutton_master2.MainActivity;
import com.jiefanproj.android.embutton_master2.R;
import com.jiefanproj.android.embutton_master2.WizardActivity;
import com.jiefanproj.android.embutton_master2.adapter.PageItemAdapter;
import com.jiefanproj.android.embutton_master2.AppConstants;
import com.jiefanproj.android.embutton_master2.AppUtil;
import com.jiefanproj.android.embutton_master2.ApplicationSettings;
import com.jiefanproj.android.embutton_master2.MyTagHandler;
import com.jiefanproj.android.embutton_master2.data.PBDatabase;
import com.jiefanproj.android.embutton_master2.model.Page;
import com.jiefanproj.android.embutton_master2.model.PageItem;
import com.jiefanproj.android.embutton_master2.model.SMSSettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * v 2.0.1.1
 */
public class SetupMessageFragment extends Fragment {
    private EditText smsEditText;

    private static final String PAGE_ID = "page_id";
    private static final String PARENT_ACTIVITY = "parent_activity";
    private Activity activity;

    DisplayMetrics metrics;

    TextView tvTitle, tvContent, tvIntro, tvWarning;
    Button bAction;
    ListView lvItems;
    LinearLayout llWarning;

    Page currentPage;
    PageItemAdapter pageItemAdapter;

    public static SetupMessageFragment newInstance(String pageId, int parentActivity) {
        SetupMessageFragment f = new SetupMessageFragment();
        Bundle args = new Bundle();
        args.putString(PAGE_ID, pageId);
        args.putInt(PARENT_ACTIVITY, parentActivity);
        f.setArguments(args);
        return(f);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_type_interactive_message, container, false);

        tvTitle = (TextView) view.findViewById(R.id.fragment_title);
        tvIntro = (TextView) view.findViewById(R.id.fragment_intro);
        tvContent = (TextView) view.findViewById(R.id.fragment_contents);

        bAction = (Button) view.findViewById(R.id.fragment_action);
        bAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(">>>>", "action button pressed");
                String msg =  getSMSSettingsFromView();
                Log.e(">>>>", "SetupMessageFragment msg "+msg);

                SMSSettings.saveMessage(activity, msg);
                displaySettings(msg);

                String pageId = currentPage.getAction().get(0).getLink();
                int parentActivity = getArguments().getInt(PARENT_ACTIVITY);
                Intent i;

                if(parentActivity == AppConstants.FROM_WIZARD_ACTIVITY){
                    i = new Intent(activity, WizardActivity.class);
                } else{
//                	AppUtil.showToast("Message saved.", 1000, activity);
                    String confirmation = (currentPage.getAction().get(0).getConfirmation() == null)
                            ? AppConstants.DEFAULT_CONFIRMATION_MESSAGE
                            : currentPage.getAction().get(0).getConfirmation();
                    Toast.makeText(activity, confirmation, Toast.LENGTH_SHORT).show();

                    i = new Intent(activity, MainActivity.class);
                }
                i.putExtra("page_id", pageId);
                startActivity(i);

                if(parentActivity == AppConstants.FROM_MAIN_ACTIVITY){
                    activity.finish();
                }
            }
        });


        lvItems = (ListView) view.findViewById(R.id.fragment_item_list);

        llWarning = (LinearLayout) view.findViewById(R.id.ll_fragment_warning);
        tvWarning  = (TextView) view.findViewById(R.id.fragment_warning);

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PageItem selectedItem = (PageItem) parent.getItemAtPosition(position);

                String pageId = selectedItem.getLink();
                int parentActivity = getArguments().getInt(PARENT_ACTIVITY);
                Intent i;

                if(parentActivity == AppConstants.FROM_WIZARD_ACTIVITY){
                    i = new Intent(activity, WizardActivity.class);
                } else{
                    i = new Intent(activity, MainActivity.class);
                }
                i.putExtra("page_id", pageId);
                startActivity(i);

            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        if (activity != null) {
            metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
/*
            Fragment fragment = getFragmentManager().findFragmentById(R.id.sms_message);
            ((MessageTextFragment)fragment).setActionButtonStateListener(bAction);
            smsEditText = (EditText) fragment.getView().findViewById(R.id.message_edit_text);
            Fragment fragment = new MessageTextFragment();
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.sms_message, fragment).commit();
*/
            Fragment fragment2 = getChildFragmentManager().findFragmentById(R.id.sms_message);
            ((MessageTextFragment) fragment2).setActionButtonStateListener(bAction);
            smsEditText = (EditText) fragment2.getView().findViewById(R.id.message_edit_text);
            Log.e(">>>>", "onActivityCreated smsEditText "+smsEditText.getText().toString());

            String currentMsg = SMSSettings.retrieveMessage(activity);
            Log.e(">>>>", "onActivityCreated currentMsg " + currentMsg);
            if(currentMsg != null) {
                displaySettings(currentMsg);
                ((EditText) fragment2.getView().findViewById(R.id.message_edit_text)).setTextColor(Color.parseColor("#000000"));

            }
            bAction.setEnabled(!smsEditText.getText().toString().trim().equals(""));
            bAction.setEnabled(true);

            String pageId = getArguments().getString(PAGE_ID);
            String selectedLang = ApplicationSettings.getSelectedLanguage(activity);

            PBDatabase dbInstance = new PBDatabase(activity);
            dbInstance.open();
            currentPage = dbInstance.retrievePage(pageId, selectedLang);
            dbInstance.close();

            tvTitle.setText(currentPage.getTitle());

            if(currentPage.getContent() == null)
                tvContent.setVisibility(View.GONE);
            else
                tvContent.setText(Html.fromHtml(currentPage.getContent(), null, new MyTagHandler()));

            if(currentPage.getIntroduction() == null)
                tvIntro.setVisibility(View.GONE);
            else
                tvIntro.setText(currentPage.getIntroduction());

            if(currentPage.getWarning() == null)
                llWarning.setVisibility(View.GONE);
            else
                tvWarning.setText(currentPage.getWarning());

            bAction.setText(currentPage.getAction().get(0).getTitle());

            pageItemAdapter = new PageItemAdapter(activity, null);
            lvItems.setAdapter(pageItemAdapter);
            pageItemAdapter.setData(currentPage.getItems());

            AppUtil.updateImages(true, currentPage.getContent(), activity, metrics, tvContent, AppConstants.IMAGE_INLINE);

        }
    }


    private void displaySettings(String msg) {

        smsEditText.setText(msg);

    }


    private String getSMSSettingsFromView() {
        Fragment fragment = new MessageTextFragment();
        //FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.sms_message, fragment).commit();

        Fragment fragment2 = getChildFragmentManager().findFragmentById(R.id.sms_message);
        ((MessageTextFragment) fragment2).setActionButtonStateListener(bAction);
        smsEditText = (EditText) fragment2.getView().findViewById(R.id.message_edit_text);
        return smsEditText.getText().toString().trim();
    }
}
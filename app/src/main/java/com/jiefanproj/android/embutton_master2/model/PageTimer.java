package com.jiefanproj.android.embutton_master2.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

/**
 * v 2.0.1.1
 */
public class PageTimer {
    private String info;
    private String inactive;
    private String fail;

    public PageTimer() {
    }

    public PageTimer(String info, String inactive, String fail) {
        this.info = info;
        this.inactive = inactive;
        this.fail = fail;
    }

    public static PageTimer parsePageTimer(JSONObject timerObj){
        PageTimer timer = new PageTimer();

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        if(timerObj != null){
            String jsonString = timerObj.toString();
            timer = gson.fromJson(jsonString, PageTimer.class);
        }
        return timer;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInactive() {
        return inactive;
    }

    public void setInactive(String inactive) {
        this.inactive = inactive;
    }

    public String getFail() {
        return fail;
    }

    public void setFail(String fail) {
        this.fail = fail;
    }
}

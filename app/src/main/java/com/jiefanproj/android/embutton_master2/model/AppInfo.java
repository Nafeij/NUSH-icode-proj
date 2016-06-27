package com.jiefanproj.android.embutton_master2.model;

/**
 * v 2.0.1.1
 */
public class AppInfo {

    private String appName;
    private String packageName;

    public AppInfo(String appName, String packageName) {
        this.appName = appName;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}

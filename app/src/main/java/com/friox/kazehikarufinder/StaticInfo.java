package com.friox.kazehikarufinder;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

public class StaticInfo {

    Context context;
    String versionName;

    String baseUrl = "";
    String mainUrl = "";
    String currentUrl = mainUrl + "/";
    String parentUrl;
    String rootName = "";
    String registerUrl = "";

    String animeTag = "anime";
    String manageTag = "manage";

    String notiAnimeId = "khnoti_anime";
    String notiManageId = "khnoti_manage";
    String notiUnknownId = "khnoti_unknown";

    String agentCode = "";

    public StaticInfo(Context context) {
        this.context = context;
        versionName = getAppVersionName();
    }

    public StaticInfo() {}

    public void updateURL(String url) {
        currentUrl = url;
    }

    public void updateParent(String url) {
        parentUrl = url;
    }

    public String getUserAgent() {
        return "KazehikaruFinder-" + agentCode + "/" + versionName + " (" + Build.MANUFACTURER + " " + Build.MODEL + ")";
    }

    public String getAppVersionName(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "UNKNOWN";
        }
        return packageInfo.versionName;
    }
}
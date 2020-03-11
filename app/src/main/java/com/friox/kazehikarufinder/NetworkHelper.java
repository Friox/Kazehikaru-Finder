package com.friox.kazehikarufinder;

import android.content.Context;

import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkHelper extends Thread{

    private boolean success;
    private String host;
    StaticInfo staticInfo;

    public NetworkHelper(String host, Context context){
        this.host = host;
        staticInfo = new StaticInfo(context);
    }

    @Override
    public void run() {

        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection)new URL(host).openConnection();
            conn.setRequestProperty("User-Agent", staticInfo.getUserAgent());
            conn.setConnectTimeout(5000);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if(responseCode >= 200 && responseCode < 300) success = true;
            else success = false;
        }
        catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        if(conn != null){
            conn.disconnect();
        }
    }

    public boolean isSuccess(){
        return success;
    }
}
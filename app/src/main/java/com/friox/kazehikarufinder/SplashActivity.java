package com.friox.kazehikarufinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    StaticInfo staticInfo;

    boolean backLock = true;
    boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View view = getWindow().getDecorView();
        Configuration configuration = getResources().getConfiguration();
        if ((configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        staticInfo = new StaticInfo(getApplicationContext());

        if (isOnline()) status = true;
        Handler hd = new Handler();
        hd.postDelayed(new updateAnimation(), 1000); // SPLASH 1sec
    }

    private class updateAnimation implements Runnable {
        public void run() {
            TextView textView = findViewById(R.id.textView);
            if (status == true) textView.setText(R.string.good_string);
            else textView.setText(R.string.no_network_string);
            ConstraintLayout constraintLayout = findViewById(R.id.test);
            TransitionManager.beginDelayedTransition(constraintLayout);
            textView.setVisibility(View.VISIBLE);
            if (status == true) {
                Handler hd = new Handler();
                hd.postDelayed(new goMain(), 1000); // delay 1sec
            } else {
                backLock = false;
            }
        }
    }

    private class goMain implements Runnable {
        public void run() {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            SplashActivity.this.finish();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }

    public boolean isOnline() {
        NetworkHelper cc = new NetworkHelper(staticInfo.mainUrl, SplashActivity.this);
        cc.start();
        try {
            cc.join();
            return cc.isSuccess();
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        // Back Control
        if (!backLock) super.onBackPressed();
    }
}

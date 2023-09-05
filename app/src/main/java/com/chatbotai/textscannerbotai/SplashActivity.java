package com.chatbotai.textscannerbotai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.chatbotai.textscannerbotai.ads.RemoteConfig;
import com.onesignal.OneSignal;

public class SplashActivity extends AppCompatActivity {
    private static final String ONESIGNAL_APP_ID = "ebff7ced-bb32-4ef4-9284-89c67d9c45cd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.promptForPushNotifications();
        try {
            //TODO : Connect to server Firebase
            new RemoteConfig().Remote();

        } catch (Exception ignored) {
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 2500);
    }
}
package com.chatbotai.textscannerbotai.ads;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONObject;

public class RemoteConfig {

    FirebaseRemoteConfig mFirebaseRemoteConfig;


    public void Remote() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                try {
                    String Ads = mFirebaseRemoteConfig.getString("Ads_Manager");
                    JSONArray array = new JSONArray(Ads);
                    JSONObject obj = array.getJSONObject(0);

                    String Show_Ads = obj.getString("Show_Ads");
                    String Ad_Banner = obj.getString("Ad_Banner");
                    String Ad_Inter = obj.getString("Ad_Inter");
                    String Banner_ADMOB = obj.getString("Banner_ADMOB");
                    String Inter_ADMOB = obj.getString("Inter_ADMOB");
                    String Banner_MAX = obj.getString("Banner_MAX");
                    String Inter_MAX = obj.getString("Inter_MAX");
                    String chatApi = obj.getString("Chat_API");


                    Constants.setShow_Ads(Show_Ads);
                    Constants.setAd_Banner(Ad_Banner);
                    Constants.setAd_Inter(Ad_Inter);
                    Constants.setBanner_ADMOB(Banner_ADMOB);
                    Constants.setInter_ADMOB(Inter_ADMOB);
                    Constants.setBanner_MAX(Banner_MAX);
                    Constants.setInter_MAX(Inter_MAX);
                    Constants.setChatApi(chatApi);

                } catch (Exception ignored) {
                }
            }
        });
    }


}

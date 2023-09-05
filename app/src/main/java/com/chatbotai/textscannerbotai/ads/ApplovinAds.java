package com.chatbotai.textscannerbotai.ads;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.chatbotai.textscannerbotai.R;

import java.util.concurrent.TimeUnit;

public class ApplovinAds {
    private MaxAdView adView;
    private Context context;
    private ViewGroup parentViewGroup;



    public ApplovinAds(Context context, ViewGroup parentViewGroup) {
        this.context = context;
        this.parentViewGroup = parentViewGroup;
    }

    public void LoadBannerAds() {
        adView = new MaxAdView(Constants.getBanner_MAX(), context);
        adView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd maxAd) {

            }

            @Override
            public void onAdCollapsed(MaxAd maxAd) {

            }

            @Override
            public void onAdLoaded(MaxAd maxAd) {

            }

            @Override
            public void onAdDisplayed(MaxAd maxAd) {

            }

            @Override
            public void onAdHidden(MaxAd maxAd) {

            }

            @Override
            public void onAdClicked(MaxAd maxAd) {

            }

            @Override
            public void onAdLoadFailed(String s, MaxError maxError) {

            }

            @Override
            public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {

            }
        });
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = context.getResources().getDimensionPixelSize(R.dimen.banner_hight);

        adView.setLayoutParams(new FrameLayout.LayoutParams(width, height, Gravity.BOTTOM));
        adView.setBackgroundColor(Color.argb(0, 0, 0, 0));
        parentViewGroup.addView(adView);
        adView.loadAd();
    }

}

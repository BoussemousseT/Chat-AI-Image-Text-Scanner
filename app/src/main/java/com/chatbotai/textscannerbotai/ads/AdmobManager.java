package com.chatbotai.textscannerbotai.ads;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowMetrics;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.chatbotai.textscannerbotai.MainActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Objects;

public class AdmobManager {
    public Activity activity;
    public InterstitialAd mInterstitialAd;
    public AdView adView;


    public AdmobManager(Activity activity) {
        this.activity = activity;
        adView = new AdView(activity);
    }

    public void initialize() {
        MobileAds.initialize(activity, initializationStatus -> {
        });
    }

    public void showBannerAdmob(RelativeLayout layoutAds) {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(Constants.getBanner_ADMOB());
        adView.loadAd(adRequest);
        layoutAds.removeAllViews();
        layoutAds.addView(adView);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);


            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();

            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });
    }


    public interface AdsFinished {
        void onAdsFinished();
    }


    public void showBannerAdmob(LinearLayout adBanner) {
        AdRequest adRequest = new AdRequest.Builder().build();
		adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(Constants.getBanner_ADMOB());
        adView.loadAd(adRequest);
        adBanner.removeAllViews();
        adBanner.addView(adView);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                

            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });


    }


    public void loadInterAdmob() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, Constants.getInter_ADMOB(), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitialAd = null;
                loadInterAdmob();
            }
        });
    }

    public void showInterAdmob(MainActivity.AdFinished adsFinished) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    mInterstitialAd = null;
                    loadInterAdmob();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();

                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    adsFinished.onAdFinished();

                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }
            });
        } else {

            adsFinished.onAdFinished();
        }

    }

}

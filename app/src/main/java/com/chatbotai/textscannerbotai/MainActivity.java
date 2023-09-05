package com.chatbotai.textscannerbotai;

import static com.chatbotai.textscannerbotai.ads.Constants.getAd_Banner;
import static com.chatbotai.textscannerbotai.ads.Constants.getAd_Inter;
import static com.chatbotai.textscannerbotai.ads.Constants.getShow_Ads;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.chatbotai.textscannerbotai.ads.AdmobManager;
import com.chatbotai.textscannerbotai.ads.ApplovinAds;
import com.chatbotai.textscannerbotai.ads.Constants;
import com.chatbotai.textscannerbotai.ads.RemoteConfig;
import com.chatbotai.textscannerbotai.databinding.ActivityMainBinding;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    //    private MaxAdView adView;
    LinearLayout layoutBanner;
    ApplovinAds applovinAds;
    AdmobManager admobManager;

    private MaxInterstitialAd interstitialAd;
    //    private int retryAttempt;
    Context context;
    private static final int REQUEST_CAMERA_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setTitle("");
//        Binding SetUp
        context = this;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        admobManager = new AdmobManager(this);
        layoutBanner = findViewById(R.id.ads_ll);
        applovinAds = new ApplovinAds(context, layoutBanner);

        //TODO : Initialization Admob & applovin Ads
        AppLovinSdk.getInstance(this).setMediationProvider("max");
        AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                showAdBanner();
            }
        });

        // we are checking permission for CAMERA Access.
        checkPermissions();

        onClickListeners();


    }


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }
    }

    private void onClickListeners() {
        binding.captureBtn.setOnClickListener(view -> {
            loadAdInter(new AdFinished() {
                @Override
                public void onAdFinished() {
                    cropImage();
                }
            });
        });
        binding.copyBtn.setOnClickListener(view -> {
            loadAdInter(new AdFinished() {
                @Override
                public void onAdFinished() {
                    String scanned_text = binding.dataEt.getText().toString();
                    copyToClipBoard(scanned_text);
                }
            });
        });

        binding.chatGptBtn.setOnClickListener(view -> {
            loadAdInter(new AdFinished() {
                @Override
                public void onAdFinished() {
                    toChatActivity();
                }
            });

        });
    }

    public void cropImage() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);

    }


    public void toChatActivity() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    public void toChatGptBtn() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("message", binding.dataEt.getText().toString());
        startActivity(intent);
    }

    /**
     * for get the capture button Bitmap result.
     *
     * @param requestCode -
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = (result != null) ? result.getUri() : null;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    getTextFromImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void getTextFromImage(Bitmap bitmap) {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> sparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < sparseArray.size(); i++) {
                TextBlock textBlock = sparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
            binding.dataEt.setVisibility(View.VISIBLE);
            binding.dataTv.setVisibility(View.GONE);

            binding.dataEt.setText(stringBuilder.toString());

            binding.captureBtn.setText("Retake");
            binding.copyBtn.setVisibility(View.VISIBLE);
            binding.chatGptBtn.setVisibility(View.VISIBLE);

            binding.chatGptBtn.setOnClickListener(view -> {
                loadAdInter(new AdFinished() {
                    @Override
                    public void onAdFinished() {
                        toChatGptBtn();
                    }
                });
            });
        }
    }

    private void copyToClipBoard(String text) {

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied text", text);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "Copied to clipBoard", Toast.LENGTH_SHORT).show();

    }

    //TODO : method show Banner
    private void showAdBanner() {

        if (Objects.equals(getShow_Ads(), "YES")) {

            if (Objects.equals(getAd_Banner(), "admob")) {
                admobManager.showBannerAdmob(layoutBanner);
            } else if (Objects.equals(getAd_Banner(), "max")) {
                applovinAds.LoadBannerAds();
            }
        }
    }

    //TODO : method load Interstitial
    private void loadAdInter(AdFinished adFinished) {

        if (Objects.equals(getShow_Ads(), "YES")) {

            if (Objects.equals(getAd_Inter(), "admob")) {
                admobManager.loadInterAdmob();
                admobManager.showInterAdmob(adFinished);
            } else if (Objects.equals(getAd_Inter(), "max")) {
                createInterstitialAd(adFinished);
            }
        } else {
            NextActivity(adFinished);
        }

    }


    private void createInterstitialAd(AdFinished adFinished) {
        interstitialAd = new MaxInterstitialAd(Constants.getInter_MAX(), this);
        interstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd maxAd) {
                interstitialAd.showAd();

            }

            @Override
            public void onAdDisplayed(MaxAd maxAd) {

            }

            @Override
            public void onAdHidden(MaxAd maxAd) {
                adFinished.onAdFinished();
            }

            @Override
            public void onAdClicked(MaxAd maxAd) {

            }

            @Override
            public void onAdLoadFailed(String s, MaxError maxError) {
                adFinished.onAdFinished();
            }

            @Override
            public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
                adFinished.onAdFinished();
            }
        });

        // Load the first ad
        interstitialAd.loadAd();

    }


    public interface AdFinished {
        void onAdFinished();
    }

    private void NextActivity(AdFinished adFinished) {
        adFinished.onAdFinished();
    }
}
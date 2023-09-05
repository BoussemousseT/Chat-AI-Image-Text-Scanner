package com.chatbotai.textscannerbotai;

import static com.chatbotai.textscannerbotai.ads.Constants.getAd_Banner;
import static com.chatbotai.textscannerbotai.ads.Constants.getAd_Inter;
import static com.chatbotai.textscannerbotai.ads.Constants.getShow_Ads;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.chatbotai.textscannerbotai.adapter.MessageAdapter;
import com.chatbotai.textscannerbotai.ads.AdmobManager;
import com.chatbotai.textscannerbotai.ads.ApplovinAds;
import com.chatbotai.textscannerbotai.ads.Constants;
import com.chatbotai.textscannerbotai.databinding.ActivityChatBinding;
import com.chatbotai.textscannerbotai.databinding.ActivityMainBinding;
import com.chatbotai.textscannerbotai.entite.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    ArrayList<Message> messagesList;
    MessageAdapter messageAdapter;
    RelativeLayout layoutAds;

    ApplovinAds applovinAds;
    Context context;
    AdmobManager admobManager;

    private MaxInterstitialAd interstitialAd;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        messagesList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messagesList);
        binding.recycleView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        binding.recycleView.setLayoutManager(llm);
        context = this;
        Intent intent = getIntent();
        String msg = intent.getStringExtra("message");

        //banner ads

        layoutAds = findViewById(R.id.ads_ll_chat);
        applovinAds = new ApplovinAds(context, layoutAds);
        admobManager = new AdmobManager(this);
        showAdBanner();

        if (msg != null) {
            addToChat(msg, Message.SENT_BY_ME);
            callAPI(msg);
            binding.welcomeText.setVisibility(View.GONE);
        }

        binding.sendBtn.setOnClickListener(view1 -> {
            String question = binding.etMessage.getText().toString().trim();
            addToChat(question, Message.SENT_BY_ME);
            binding.etMessage.setText("");
            callAPI(question);
            binding.welcomeText.setVisibility(View.GONE);
        });
    }

    public void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messagesList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                binding.recycleView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });

    }

    public void addResponse(String response) {
        messagesList.remove(messagesList.size() - 1);
        addToChat(response, Message.SENT_BY_BOT);
    }

    public void callAPI(String question) {
        messagesList.add(new Message("Typing...", Message.SENT_BY_BOT));
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            JSONArray messageArr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("role", "user");
            obj.put("content", question);
            messageArr.put(obj);
            jsonBody.put("messages", messageArr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", Constants.getChatApi())
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load reponse due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    addResponse("Failed to load reponse due to " + response.body().toString());
                }
            }
        });


    }


    //TODO : method show Banner
    private void showAdBanner() {

        if (Objects.equals(getShow_Ads(), "YES")) {

            if (Objects.equals(getAd_Banner(), "admob")) {
                admobManager.showBannerAdmob(layoutAds);
            } else if (Objects.equals(getAd_Banner(), "max")) {
                applovinAds.LoadBannerAds();
            }
        }
    }

    //TODO : method load Interstitial
    private void loadAdInter(MainActivity.AdFinished adFinished) {

        if (Objects.equals(getShow_Ads(), "YES")) {

            if (Objects.equals(getAd_Inter(), "admob")) {
                admobManager.loadInterAdmob();
                admobManager.showInterAdmob(adFinished);
            } else if (Objects.equals(getAd_Inter(), "max")) {
                createInterstitialAd(adFinished);
            }
        }
    }


    private void createInterstitialAd(MainActivity.AdFinished adFinished) {
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
}
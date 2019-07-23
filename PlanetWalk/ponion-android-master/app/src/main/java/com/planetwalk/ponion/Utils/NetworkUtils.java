package com.planetwalk.ponion.Utils;

import android.content.Context;
import android.text.TextUtils;

import com.planetwalk.ponion.PonionApplication;
import com.planetwalk.ponion.Service.MessageService;
import com.planetwalk.ponion.db.Entity.BuddyEntity;
import com.planetwalk.ponion.db.Entity.SlideEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {
    public static List<SlideEntity> convertContentToSlides(String content) {
        List<SlideEntity> slideEntityList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject slideJSONObject = jsonArray.getJSONObject(i);
                SlideEntity slideEntity = new SlideEntity();
                if (slideJSONObject.has("isLock")) {
                    slideEntity.isLock = true;
                }

                if (slideJSONObject.has("img")) {
                    slideEntity.mediaUrl = slideJSONObject.getString("img");
                }

                if (slideJSONObject.has("txt")) {
                    slideEntity.text = slideJSONObject.getString("txt");
                }

                if (slideJSONObject.has("bkgColor")) {
                    slideEntity.backgroundColor = slideJSONObject.getInt("bkgColor");
                }

                slideEntityList.add(slideEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return slideEntityList;
    }

    public static String convertSlidesToJSONString(List<SlideEntity> slideEntityList) {
        JSONArray jsonArray = new JSONArray();
        for (SlideEntity slide : slideEntityList) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("isLock", slide.isLock);
                jsonObject.put("text", slide.text);
                jsonObject.put("mediaUrl", slide.mediaUrl);
                jsonObject.put("bkgColor", slide.backgroundColor);

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonArray.toString();
    }

    public static BuddyEntity convertJSONToBuddy(JSONObject userJSON) throws JSONException {
        //{"displayName":null,"icon":null,"id":44,"sex":null,"username":"lxsiop"}
        BuddyEntity buddyEntity = new BuddyEntity();
        buddyEntity.displayName = userJSON.optString("displayName");
        if (TextUtils.isEmpty(buddyEntity.displayName) || buddyEntity.displayName == "null") {
            buddyEntity.displayName = userJSON.optString("username");
        }
        buddyEntity.account = userJSON.getLong("id");
        buddyEntity.sex = userJSON.optInt("sex");
        return buddyEntity;
    }


    public static void giveGiftToServerAsync(Context context, int toPetId, List<Integer> productIds, Callback callback) {
        String url = "http://39.105.222.203:8085/storage/give_gift";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("token", CommonUtils.getToken(context.getApplicationContext()));
        formBuilder.add("toPetId", String.valueOf(toPetId));
        if (productIds != null && productIds.size() > 0) {
            formBuilder.add("productIds", TextUtils.join(",", productIds));
        }
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void requestUserAsync(Context context, long userId) {
        PonionApplication application = (PonionApplication) context.getApplicationContext();
        application.getAppExecutors().networkIO().execute(() -> {
            try {
                Response response = NetworkUtils.getUser(context, userId);
                final String res = response.body().string();
                JSONObject jsonObject = new JSONObject(res);
                int code = jsonObject.getInt("code");
                if (code == ResultCode.SUCCESS.code()) {
                    BuddyEntity buddyEntity = NetworkUtils.convertJSONToBuddy(jsonObject.getJSONObject("data"));
                    application.getRepository().insertBuddy(buddyEntity);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public static Response getUser(Context context, long userId) throws IOException {
        String url = "http://39.105.222.203:8085/user/user";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("token", CommonUtils.getToken(context.getApplicationContext()));
        formBuilder.add("userId", String.valueOf(userId));
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return response;
    }


    public static Response eventAction(Context context, long petId, long eventId, int actionChoice) throws IOException {
        String url = "http://39.105.222.203:8085/pet/action";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("token", CommonUtils.getToken(context.getApplicationContext()));
        formBuilder.add("petId", String.valueOf(petId));
        formBuilder.add("eventId", String.valueOf(eventId));
        formBuilder.add("action", String.valueOf(actionChoice));
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return response;
    }

    public static Response giveGiftToServer(Context context, int toPetId, List<Integer> productIds) throws IOException {
        String url = "http://39.105.222.203:8085/storage/give_gift";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("token", CommonUtils.getToken(context.getApplicationContext()));
        formBuilder.add("toPetId", String.valueOf(toPetId));
        if (productIds != null && productIds.size() > 0) {
            formBuilder.add("productIds", TextUtils.join(",", productIds));
        }
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return response;
    }
}

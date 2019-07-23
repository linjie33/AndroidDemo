package com.planetwalk.ponion.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.igexin.sdk.PushManager;
import com.planetwalk.ponion.GlobalData;
import com.planetwalk.ponion.Service.GetuiPushIntentService;
import com.planetwalk.ponion.Service.GetuiPushService;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getSharedPreferences(GlobalData.PET_PREFERENCE, Context.MODE_PRIVATE);
        String name = sharedPref.getString("name", null);
        String token = sharedPref.getString("token", null);
        Intent intent;

        PushManager.getInstance().initialize(getApplicationContext(), GetuiPushService.class);
        // GetuiPushIntentService 为第三⽅方⾃自定义的推送服务事件接收类
        PushManager.getInstance().registerPushIntentService(getApplicationContext(),
                GetuiPushIntentService.class);


        if (TextUtils.isEmpty(token)) {
            intent = new Intent(LauncherActivity.this, LoginActivity.class);
        } else if (TextUtils.isEmpty(name)) {
            intent = new Intent(LauncherActivity.this, PonionMainActivity.class);
        } else {
            intent = new Intent(LauncherActivity.this, PonionMainActivity.class);
        }

        startActivity(intent);
        finish();
    }
}

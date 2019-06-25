package com.linjie.activitylifedemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * 正常显示的Activity
 */
public class NormalActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_normal);
    }
}
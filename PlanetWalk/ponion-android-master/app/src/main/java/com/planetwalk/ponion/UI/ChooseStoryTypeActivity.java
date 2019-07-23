package com.planetwalk.ponion.UI;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.planetwalk.ponion.R;

public class ChooseStoryTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_story_type);

        findViewById(R.id.picture_type_btn).setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("type", 1);
            setResult(RESULT_OK, intent);
            finish();
        });

        findViewById(R.id.text_type_btn).setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("type", 0);
            setResult(RESULT_OK, intent);
            finish();
        });

        findViewById(R.id.lock_type_btn).setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("type", 2);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}

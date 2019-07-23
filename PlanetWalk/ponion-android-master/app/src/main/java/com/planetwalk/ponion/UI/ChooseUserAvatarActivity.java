package com.planetwalk.ponion.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.planetwalk.ponion.R;

public class ChooseUserAvatarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user_avatar);

        findViewById(R.id.confirm_button).setOnClickListener(view -> {
            Intent intent = new Intent(ChooseUserAvatarActivity.this, PonionMainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

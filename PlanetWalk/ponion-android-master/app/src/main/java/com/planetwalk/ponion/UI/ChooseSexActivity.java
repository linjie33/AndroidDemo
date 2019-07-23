package com.planetwalk.ponion.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.planetwalk.ponion.PonionApplication;
import com.planetwalk.ponion.R;
import com.planetwalk.ponion.db.Entity.BuddyEntity;

public class ChooseSexActivity extends AppCompatActivity {

    private BuddyEntity mBuddyEntity;
    private boolean isMale = true;
    private Button mMaleButton;
    private Button mFemaleButton;
    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sex);
        mFemaleButton = findViewById(R.id.female_button);
        mMaleButton = findViewById(R.id.male_button);
        mNextButton = findViewById(R.id.confirm_button);

        mMaleButton.setOnClickListener(view -> {
            isMale = true;
            refreshViews();
        });

        mFemaleButton.setOnClickListener(view -> {
            isMale = false;
            refreshViews();
        });

        mNextButton.setOnClickListener(view -> {
            mBuddyEntity.sex = isMale ? 0 : 1;
            PonionApplication application = (PonionApplication) getApplication();
            application.getAppExecutors().diskIO().execute(() -> {
                application.getRepository().updateBuddy(mBuddyEntity);
            });

            startActivity(new Intent(ChooseSexActivity.this, ChooseUserAvatarActivity.class));
            finish();
        });

        refreshViews();
        PonionApplication application = (PonionApplication) getApplication();
        application.getRepository().getMeBuddy().observe(this, buddyEntity -> {
            mBuddyEntity = buddyEntity;
        });
    }

    private void refreshViews() {
        if (isMale) {
            mMaleButton.setTextColor(getResources().getColor(R.color.Crimson));
            mFemaleButton.setTextColor(getResources().getColor(R.color.white));
        } else {
            mMaleButton.setTextColor(getResources().getColor(R.color.white));
            mFemaleButton.setTextColor(getResources().getColor(R.color.Crimson));
        }
    }
}

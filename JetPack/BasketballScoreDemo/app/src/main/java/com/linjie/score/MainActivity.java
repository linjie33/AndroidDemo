package com.linjie.score;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.linjie.score.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    MyViewmodel myViewmodel;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        myViewmodel = ViewModelProviders.of(this).get(MyViewmodel.class);
        binding.setData(myViewmodel);
        binding.setLifecycleOwner(this);


    }
}

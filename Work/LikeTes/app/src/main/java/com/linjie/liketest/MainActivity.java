package com.linjie.liketest;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;



public class MainActivity extends AppCompatActivity {
    final Animation animation = AnimationUtils.loadAnimation(this,R.anim.shake);
    final EditText editText = (EditText)findViewById(R.id.textView);
    final ViewGroup view = (ViewGroup)findViewById(R.id.view);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText.startAnimation(animation);
        Button but = (Button)findViewById(R.id.but);
        but.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                view.startAnimation(animation);
            }
        });

    }
}

package com.linjie.swaggeruidemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button getBtn  = (Button)findViewById(R.id.getMethod);
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }

    private void getData() {

        URL url = null;
        try {
            url = new URL("");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

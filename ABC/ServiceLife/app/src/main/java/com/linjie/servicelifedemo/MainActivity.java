package com.linjie.servicelifedemo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btnStartService;
    private Button btnStopService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intiView();
    }

    private void intiView() {
    btnStartService = (Button)findViewById(R.id.btnStartService);
    btnStopService = (Button)findViewById(R.id.btnStopService);

    btnStartService.setOnClickListener(this);
    btnStopService.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnStartService){
            Intent intent = new Intent(this,ServiceTest.class);
            startService(intent);
            return;
        }
        if (v.getId() == R.id.btnStopService){
            Intent intent = new Intent(this,ServiceTest.class);
            stopService(intent);
            return;

        }
    }
}

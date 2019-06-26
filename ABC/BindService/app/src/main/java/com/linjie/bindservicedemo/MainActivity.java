package com.linjie.bindservicedemo;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener{

    private Button btnLockService;
    private Button btnUnLockService;
    private Button btnGetService;

    final Intent intent = new Intent();

    TestService.MyBinder binder;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("连接服务");
            binder = (TestService.MyBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        System.out.println("不连接服务");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnLockService = (Button) findViewById(R.id.btnLockService);
        btnUnLockService = (Button) findViewById(R.id.btnUnLockService);
        btnGetService = (Button) findViewById(R.id.btnGetService);

        intent.setAction("com.linjie.bindservicedemo.TestService");
        intent.setPackage(getPackageName());

        btnLockService.setOnClickListener(this);
        btnUnLockService.setOnClickListener(this);
        btnGetService.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnLockService){
            //绑定Service
            bindService(intent,conn, Service.BIND_AUTO_CREATE);
        }
        if (v.getId() == R.id.btnUnLockService){
            if (conn != null){
                //解除service的绑定
                unbindService(conn);
            }
        }
        if (v.getId() == R.id.btnGetService){
            Toast.makeText(getApplicationContext(),"Service的count的值" +
                    "为："+binder.getCount(),Toast.LENGTH_SHORT).show();
        }

    }
}

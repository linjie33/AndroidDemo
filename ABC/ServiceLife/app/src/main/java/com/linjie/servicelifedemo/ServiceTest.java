package com.linjie.servicelifedemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServiceTest extends Service {
    private static final String TAG = ServiceTest.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"onBind方法被调用!");

        return null;
    }

    @Override
    public void onCreate() {
        Log.e(TAG,"onCreate方法被调用！");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"onStartCommand方法被调用！");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        Log.e(TAG,"方法onDestory被调用");
        super.onDestroy();
    }


}

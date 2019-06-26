package com.linjie.bindservicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class TestService extends Service {

    private static String TAG = TestService.class.getSimpleName();
    private int count;
    private boolean quit;

    //定义onBinder方法所返回的对象
    private MyBinder binder = new MyBinder();
    public class MyBinder extends Binder {
    public int getCount(){
        return count;
    }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"onBind方法被调用");
        return binder;
    }
    //Service被创建时回调


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate被回调");
        //创建一个线程动态的修改count的值
        new Thread(new Runnable() {
            @Override
            public void run() {
            while (!quit){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;;
            }
            }
        }).start();
    }
    //Service断开时回调
    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG,"onUnbind方法被调用");
        return true;
    }

    //Service被关闭前回调

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.quit= true;
        Log.e(TAG,"onDestory方法被调用");
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e(TAG,"onRebind方法被调用");
        super.onRebind(intent);
    }
}

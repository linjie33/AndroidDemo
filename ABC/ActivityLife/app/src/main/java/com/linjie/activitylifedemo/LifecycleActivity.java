package com.linjie.activitylifedemo;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.content.Intent;


//生命周期演示
public class LifecycleActivity extends Activity implements View.OnClickListener {
    private static final String TAG = LifecycleActivity.class.getSimpleName();

    private Button btnNormal;
    private Button btnDialog;

//    Activity第一次被创建


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCraete() -- android系统调用onCreate方法第一次创建LifecycleActivity，" +
                "LifecycleActivity此时处于【运行状态】");
        //去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //加载布局
        setContentView(R.layout.activity_lifecycle);
        //初始化控件
        initView();
    }
    //初始化视图
    private void initView(){
        btnNormal = (Button)findViewById(R.id.btnNormal);
        btnDialog = (Button)findViewById(R.id.btnDialog);

        btnNormal.setOnClickListener(this);
        btnDialog.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnNormal:
                Intent intent = new Intent(LifecycleActivity.this,NormalActivity.class);
                startActivity(intent);
                break;
            case R.id.btnDialog:
                Intent intent1 = new Intent(LifecycleActivity.this, DialogActivity.class);
                startActivity(intent1);
                break;

        }


        }

//activity 从后台重新回到前台（不可见变为可见）时调用
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart()--LifecycleAcitivity从后台重新回到前台（由不可见变为可见），" +
                "android系统调用了onStart方法，LifecycleActivity此时处于【运行状态】");

    }
    //activity准备好和用户进行交互时调用
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()--LifecycleAcitivity准备好和用户做好交互，" +
                "android系统调用了onResume方法，LifecycleActivity此时处于【运行状态】");

    }
    //activity准备去启动或者恢复另一个activity时调用
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause()--LifecycleAcitivity准备去启动另一个Activity" +
                "android系统调用了onPause方法，LifecycleActivity此时处于【暂停状态】");

    }
    //退出当前activity或者跳转到新activity时调用
    //activity完全不可见时调用
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop()--LifecycleAcitivity已经完全不可见了" +
                "android系统调用了onStop方法，LifecycleActivity此时处于【停止状态】");

    }

    //退出activity时调用，调用后Activity就结束了
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestory()--LifecycleAcitivity已经准备被销毁了" +
                "android系统调用了onDestory方法，LifecycleActivity此时处于【销毁状态】");

    }
    //activity从后台重新回到前台时调用
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart()--LifecycleAcitivity由停止状态变为运行状态" +
                "android系统调用了onRestart方法，LifecycleActivity此时处于【运行状态】");

    }



}


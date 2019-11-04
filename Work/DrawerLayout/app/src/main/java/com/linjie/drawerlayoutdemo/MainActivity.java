package com.linjie.drawerlayoutdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.app_toolbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_bg));//设置Toolbar的背景颜色

        //toolbar.setNavigationIcon(R.mipmap.ic_menu);//设置导航的图标
        toolbar.setLogo(R.mipmap.ic_launcher);//设置logo

        toolbar.setTitle("title");//设置标题
        toolbar.setSubtitle("subtitle");//设置子标题

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));//设置标题的字体颜色
        toolbar.setSubtitleTextColor(getResources().getColor(android.R.color.white));//设置子标题的字体颜色

        setSupportActionBar(toolbar);
    }
    }


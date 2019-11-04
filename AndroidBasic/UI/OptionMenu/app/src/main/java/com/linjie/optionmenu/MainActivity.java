package com.linjie.optionmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;
public class MainActivity extends AppCompatActivity {
    //定义菜单项的标识
    final private int OPEN = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //加载xml菜单目录
        getMenuInflater().inflate(R.menu.main_optionmenu,menu);
        menu.add(1,OPEN,0,"打开");
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //菜单选中时触发
        int id = item.getItemId();
        String label = "";
        Log.d("OptionMenu",String.valueOf(id));

        switch (id){
            case R.id.create:
                label = "新建";
                break;
            case OPEN:
                label="打开";
                break;
            case R.id.help:
                label ="帮助";
                break;
        }

        Toast.makeText(getApplicationContext(),"你点击了："+label,Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}

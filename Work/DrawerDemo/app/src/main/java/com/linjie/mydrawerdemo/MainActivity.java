package com.linjie.mydrawerdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
private RecyclerView publicRecyclerView;
private RecyclerView payRecyclerView;
private List<String> publicDatas;
private List<String> payDatas;
private ItemAdapter publicAdapter;
private ItemAdapter payAdapter;

private RecyclerView mainRecyclerView;
private MyRVAdapter mainAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        initData();
        publicAdapter = new ItemAdapter(MainActivity.this);
        payAdapter    = new ItemAdapter(MainActivity.this);
        publicAdapter.setDatas(publicDatas);
        payAdapter.setDatas(payDatas);
        publicRecyclerView = (RecyclerView) findViewById(R.id.public_channel);
        payRecyclerView = (RecyclerView) findViewById(R.id.pay_channe2);
        publicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        payRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        publicRecyclerView.setAdapter(publicAdapter);
        payRecyclerView.setAdapter(payAdapter);

        mainAdapter = new MyRVAdapter(MainActivity.this);
        mainRecyclerView = (RecyclerView)findViewById(R.id.mainlist);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainRecyclerView.setAdapter(mainAdapter);
    }

    private void initData() {
    publicDatas = new ArrayList<String>(){};
    publicDatas.add("公告");
    publicDatas.add("问题求助");
    publicDatas.add("闲聊");
    payDatas    = new ArrayList<String>(){};
    payDatas.add("交易系统");
    payDatas.add("高级咨询");
    payDatas.add("定制服务");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setting) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//
//        if (id == R.id.nav_home) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_tools) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

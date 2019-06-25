package com.linjie.viewpagerdemo;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private View view1,view2,view3;
    private ViewPager viewPager;
    private List<View> viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.layout1,null);
        view2 = inflater.inflate(R.layout.layout2,null);
        view3 = inflater.inflate(R.layout.layout3,null);
        viewList = new ArrayList<View>();
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);

        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
               //TODO
                container.removeView(viewList.get(position));
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                //TODO

            container.addView(viewList.get(position));
            return viewList.get(position);
            }

            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o ;
            }
        };
        viewPager.setAdapter(pagerAdapter);
    }
}

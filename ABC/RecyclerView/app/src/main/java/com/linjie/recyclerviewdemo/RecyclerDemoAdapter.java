package com.linjie.recyclerviewdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerDemoAdapter  extends RecyclerView.Adapter<RecyclerDemoAdapter.MyHolder> {
    Context context;
    List<String> list;


    public RecyclerDemoAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    public void update(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    class MyHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MyHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.text);
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_demo_item,viewGroup,false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        String s = list.get(i);
        myHolder.textView.setText(s);

        myHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
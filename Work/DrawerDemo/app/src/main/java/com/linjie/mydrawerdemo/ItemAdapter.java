package com.linjie.mydrawerdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv;
        public ViewHolder(View view){
            super(view);
            tv = view.findViewById(R.id.id_num);
        }
    }
    private Context context;
    private List<String> datas;
    public ItemAdapter(Context context){
        this.context = context;
    }

    public void setDatas(List<String> datas){
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        ViewHolder holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        holder.tv.setText(datas.get(position));
    }

    @Override
    public int getItemCount(){
        return datas.size();
    }
}

package com.linjie.mydrawerdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyRVAdapter extends RecyclerView.Adapter<MyRVAdapter.MyTVHolder> {

    private final String[] mArray;
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;

    public MyRVAdapter(Context context) {
        mArray = context.getResources().getStringArray(R.array.mainlist);
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public MyRVAdapter.MyTVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyTVHolder(mLayoutInflater.inflate(R.layout.mainlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyRVAdapter.MyTVHolder holder, int position) {
        holder.mTextView.setText(mArray[position]);
    }

    @Override
    public int getItemCount() {
        return mArray == null ? 0 : mArray.length;
    }

    public class MyTVHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public MyTVHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_test);
        }
    }
}

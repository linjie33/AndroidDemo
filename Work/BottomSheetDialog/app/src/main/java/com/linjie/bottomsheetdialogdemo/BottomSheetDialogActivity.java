package com.linjie.bottomsheetdialogdemo;


import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Layout;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class BottomSheetDialogActivity extends AppCompatActivity {

    private Button mBotton ;
//    private Button  mShareDialog ;
//    private BottomSheetDialog bottomSheetDialog;
//    private Button mBottomDialog ;
//    private Button mBottomDialogFragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet_dialog);

        //bottomSheetDialg动态布局
        mBotton = (Button) findViewById(R.id.button);
        mBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });


    }


    private void showBottomDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null);

        handleList(view);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();
    }


    private void handleList(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        MyRecyclerAdapter adapter = new MyRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setData(getDatas());
        adapter.notifyDataSetChanged();
    }

    private List<String> getDatas() {
        List<String> list = new ArrayList<>();
        for (int i = 0 ;i<30 ;i++) {
            list.add("android："+i);
        }
        return list;
    }


    public static class MyRecyclerAdapter extends RecyclerView.Adapter{

        private List<String> mData ;

        public void setData(List<String> list) {
            mData = list ;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_item,null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            myViewHolder.index.setText(mData.get(position)+"a");
            myViewHolder.name.setText(mData.get(position)+"b");
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 :mData.size();
        }



        public static class MyViewHolder extends RecyclerView.ViewHolder{

            private TextView name ;
            private TextView index ;
            public MyViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.bottom_sheet_dialog_item_name);
                index = (TextView) itemView.findViewById(R.id.bottom_sheet_dialog_item_index);
            }
        }
    }

}

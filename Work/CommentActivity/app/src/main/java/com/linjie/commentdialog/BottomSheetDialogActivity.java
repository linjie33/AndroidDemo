package com.linjie.commentdialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetDialogActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bt_start;
    private ImageView iv_dialog_close;
    private RecyclerView rv_dialog_lists;
    private BottomSheetAdapter bottomSheetAdapter;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetBehavior mDialogBehavior;
    private List<String> list_strs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.BottomSheetDialog);

        list_strs = new ArrayList<>();
        initView();
        showSheetDialog();
    }

    private void initView() {
        bt_start = (Button) findViewById(R.id.main_bt_start);

        for (int i=0; i<20; i++) {
            list_strs.add("评论" + i);
        }

        bt_start.setOnClickListener(this);
    }

    private void showSheetDialog() {
        View view = View.inflate(BottomSheetDialogActivity.this, R.layout.dialog_bottomsheet, null);
        iv_dialog_close = (ImageView) view.findViewById(R.id.dialog_bottomsheet_iv_close);
        rv_dialog_lists = (RecyclerView) view.findViewById(R.id.dialog_bottomsheet_rv_lists);

        iv_dialog_close.setOnClickListener(this);

        bottomSheetAdapter = new BottomSheetAdapter(BottomSheetDialogActivity.this, list_strs);
        rv_dialog_lists.setHasFixedSize(true);
        rv_dialog_lists.setLayoutManager(new LinearLayoutManager(BottomSheetDialogActivity.this));
        rv_dialog_lists.setItemAnimator(new DefaultItemAnimator());
        rv_dialog_lists.setAdapter(bottomSheetAdapter);

        bottomSheetDialog = new BottomSheetDialog(BottomSheetDialogActivity.this, R.style.dialog);
        bottomSheetDialog.setContentView(view);
        mDialogBehavior = BottomSheetBehavior.from((View) view.getParent());
        mDialogBehavior.setPeekHeight(getWindowHeight());
        mDialogBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetDialog.dismiss();
                    mDialogBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    private int getWindowHeight() {
        Resources res = BottomSheetDialogActivity.this.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_bt_start:
                if (bottomSheetDialog != null) {
                    bottomSheetDialog.show();
                }
                break;
            case R.id.dialog_bottomsheet_iv_close:
                if (bottomSheetDialog != null) {
                    bottomSheetDialog.dismiss();
                }
                break;
        }
    }
}

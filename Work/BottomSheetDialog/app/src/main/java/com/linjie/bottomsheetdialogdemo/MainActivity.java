package com.linjie.bottomsheetdialogdemo;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mBottomSheetDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBottomSheetDialog = (Button)findViewById(R.id.bottomsheetdialog);
        mBottomSheetDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOtherActivity(BottomSheetDialogActivity.class);
            }
        });
    }

    private void goToOtherActivity(Class clazz) {
    Intent intent = new Intent(this,clazz);
    startActivity(intent);
     }
}

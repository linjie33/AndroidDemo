package com.linjie.helloworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for(int i = 0;i<10 ;i++){
            int selector = i;
          Log.e("MainActivity","for - i value:"+i);

            Button button = (Button)findViewById(R.id.button1);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* todo */
                }
            });
        }
    }
    public void showToast(View view){
        Toast.makeText(this, "客观，您要的吐司来啦", Toast.LENGTH_LONG).show();
    }
}

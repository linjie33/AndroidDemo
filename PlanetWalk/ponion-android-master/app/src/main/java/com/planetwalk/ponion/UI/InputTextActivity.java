package com.planetwalk.ponion.UI;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.planetwalk.ponion.R;

public class InputTextActivity extends AppCompatActivity {
    public static final int CODE_REQUEST_CONTENT = 10001;
    public static final String EXTRA_CONTENT = "extra_content";

    private EditText mEditText;
    private TextView mPostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_text);

        mEditText = findViewById(R.id.content_editText);
        mPostButton = findViewById(R.id.post_button);
        mPostButton.setEnabled(false);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPostButton.setEnabled(!TextUtils.isEmpty(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        findViewById(R.id.post_button).setOnClickListener(view -> {
            String msg = mEditText.getText().toString();
            Intent data = new Intent();
            data.putExtra(EXTRA_CONTENT, msg);
            setResult(CODE_REQUEST_CONTENT, data);
            finish();
        });

        findViewById(R.id.cancel_button).setOnClickListener(view -> {
            String msg = mEditText.getText().toString();
            if (!TextUtils.isEmpty(msg)) {
                final AlertDialog dialog = new AlertDialog.Builder(InputTextActivity.this).setMessage(R.string.warn_draft_deleted)
                        .setPositiveButton(R.string.confirm, ((dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            finish();
                        }))
                        .setNegativeButton(R.string.cancel, ((dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })).create();
                dialog.show();
            } else {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        setResult(CODE_REQUEST_CONTENT, data);

        super.onBackPressed();
    }
}

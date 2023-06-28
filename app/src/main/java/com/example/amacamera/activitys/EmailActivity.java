package com.example.amacamera.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amacamera.R;

public class EmailActivity extends AppCompatActivity {

    private Button btnSubmit;
    private EditText etDescribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        btnSubmit = findViewById(R.id.btnSubmit);
        etDescribe = findViewById(R.id.etDescribe);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etDescribe.getText().toString())) {
                    etDescribe.setError("請輸入您遇到的問題");
                    etDescribe.requestFocus();
                    Toast.makeText(EmailActivity.this, "請輸入您遇到的問題", Toast.LENGTH_LONG).show();
                }else{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "AMACameraCustomerService@gmail.com"))
                            .putExtra(Intent.EXTRA_SUBJECT, "AMA Camera 問題求助")
                            .putExtra(Intent.EXTRA_TEXT, etDescribe.getText().toString()));
                }
            }
        });
    }
}
package com.example.amacamera.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.amacamera.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSend;

    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respassword);

        etEmail = findViewById(R.id.etEmail);

        btnSend = findViewById(R.id.btnSend);

        mAuth = FirebaseAuth.getInstance();

        btnSend.setOnClickListener(view -> {
            sendPasswordReset();
        });

    }

    public void sendPasswordReset() {
        mAuth = FirebaseAuth.getInstance();
        String email = etEmail.getText().toString();

        if (TextUtils.isEmpty(email)){
            etEmail.setError("請輸入電子信箱");
            etEmail.requestFocus();
            Toast.makeText(ResPasswordActivity.this, "請輸入電子信箱", Toast.LENGTH_LONG).show();
        }else{
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME,0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("hasLoggedIn",false);
                                editor.commit();
                                Toast.makeText(ResPasswordActivity.this, "已發送重置密碼郵件", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResPasswordActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(ResPasswordActivity.this, "未查詢到此電子信箱", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}

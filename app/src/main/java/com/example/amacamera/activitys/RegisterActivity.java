package com.example.amacamera.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.amacamera.R;
import com.example.amacamera.entity.RegisterModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail,etName,etPassword,etCheckPassword;

    private CheckBox cbPassword;
    private Button btnRegister;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etCheckPassword = findViewById(R.id.etCheckPassword);
        cbPassword = findViewById(R.id.cbPassword);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        cbPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etCheckPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etCheckPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        btnRegister.setOnClickListener(view -> {
            createUser();
        });
    }

    private void createUser(){
        String email = etEmail.getText().toString();
        String name = etName.getText().toString();
        String password = etPassword.getText().toString();
        String checkPassword = etCheckPassword.getText().toString();

        RegisterModel registerModel = new RegisterModel();
        registerModel.setUserPassword(password);
        registerModel.setUserCheckPassword(checkPassword);

        if (TextUtils.isEmpty(email)){
            etEmail.setError("未輸入電子信箱");
            etEmail.requestFocus();
            Toast.makeText(RegisterActivity.this, "未輸入電子信箱", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(name)) {
            etName.setError("未輸入姓名");
            etName.requestFocus();
            Toast.makeText(RegisterActivity.this, "未輸入姓名", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)){
            etPassword.setError("未輸入密碼");
            etPassword.requestFocus();
            Toast.makeText(RegisterActivity.this, "未輸入密碼", Toast.LENGTH_LONG).show();
        }else if (TextUtils.isEmpty(checkPassword)){
            etCheckPassword.setError("未輸入確認密碼");
            etCheckPassword.requestFocus();
            Toast.makeText(RegisterActivity.this, "未輸入確認密碼", Toast.LENGTH_LONG).show();
        }else if(!registerModel.getUserCheckPassword().equals(registerModel.getUserPassword())){
            etCheckPassword.setError("確認密碼與密碼不相符");
            etCheckPassword.requestFocus();
            etPassword.setText("");
            etCheckPassword.setText("");
            Toast.makeText(RegisterActivity.this, "確認密碼與密碼不相符", Toast.LENGTH_LONG).show();
        }else{
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME,0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("hasLoggedIn",false);
                        editor.commit();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                        Toast.makeText(RegisterActivity.this, "註冊成功,請先驗證電子信箱", Toast.LENGTH_SHORT).show();
                        mAuth.getCurrentUser().sendEmailVerification();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        mAuth.getCurrentUser().updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDatabase.getReference("Users")
                                                .child(mAuth.getCurrentUser().getUid())
                                                .child("image").setValue("");
                                        mDatabase.getReference("Users")
                                                .child(mAuth.getCurrentUser().getUid())
                                                .child("email").setValue(mAuth.getCurrentUser().getEmail());
                                        mDatabase.getReference("Users")
                                                .child(mAuth.getCurrentUser().getUid())
                                                .child("name").setValue(mAuth.getCurrentUser().getDisplayName());
                                        mDatabase.getReference("Users")
                                                .child(mAuth.getCurrentUser().getUid())
                                                .child("gender").setValue("未選擇");
                                        mDatabase.getReference("Users")
                                                .child(mAuth.getCurrentUser().getUid())
                                                .child("address").setValue("");
                                        mDatabase.getReference("Users")
                                                .child(mAuth.getCurrentUser().getUid())
                                                .child("uid").setValue(mAuth.getCurrentUser().getUid());
                                        mDatabase.getReference("Users")
                                                .child(mAuth.getCurrentUser().getUid())
                                                .child("isAdmin")
                                                .setValue("0");
                                        mDatabase.getReference("Devices")
                                                .child(mAuth.getCurrentUser().getUid())
                                                .child("token");
                                    }
                                });
                    }else{
                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                        switch (errorCode) {
                            case "ERROR_INVALID_CUSTOM_TOKEN":
                                Toast.makeText(RegisterActivity.this, "自定義token不正確。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                Toast.makeText(RegisterActivity.this, "自定義token屬於不同的用戶。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_INVALID_CREDENTIAL":
                                Toast.makeText(RegisterActivity.this, "提供的身份驗證憑據格式不正確或已過期。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_INVALID_EMAIL":
                                Toast.makeText(RegisterActivity.this, "電子郵件地址格式錯誤。", Toast.LENGTH_LONG).show();
                                etEmail.setError("電子郵件地址格式錯誤");
                                etEmail.requestFocus();
                                break;
                            case "ERROR_WRONG_PASSWORD":
                                Toast.makeText(RegisterActivity.this, "密碼不正確。", Toast.LENGTH_LONG).show();
                                etPassword.setError("密碼不正確");
                                etPassword.requestFocus();
                                etPassword.setText("");
                                break;
                            case "ERROR_USER_MISMATCH":
                                Toast.makeText(RegisterActivity.this, "提供的憑證與先前登錄的用戶不相符。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_REQUIRES_RECENT_LOGIN":
                                Toast.makeText(RegisterActivity.this, "需要最近的身份驗證。 在重試此請求之前再次登錄。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                Toast.makeText(RegisterActivity.this, "已存在具有相同電子郵件地址但登錄憑證不同的帳戶。 使用與此電子郵件地址關聯的提供商登錄。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_EMAIL_ALREADY_IN_USE":
                                Toast.makeText(RegisterActivity.this, "該電子郵件地址已被另一個帳戶使用。", Toast.LENGTH_LONG).show();
                                etEmail.setError("該電子郵件地址已被另一個帳戶使用");
                                etEmail.requestFocus();
                                break;
                            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                Toast.makeText(RegisterActivity.this, "此憑證已與其他用戶帳戶相關聯。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_USER_DISABLED":
                                Toast.makeText(RegisterActivity.this, "該用戶帳戶已被管理員禁用。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_USER_TOKEN_EXPIRED":
                                Toast.makeText(RegisterActivity.this, "用戶的憑證不再有效。 用戶必須重新登錄。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_USER_NOT_FOUND":
                                Toast.makeText(RegisterActivity.this, "為查詢到與此對應的用戶記錄， 該用戶可能已被刪除。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_OPERATION_NOT_ALLOWED":
                                Toast.makeText(RegisterActivity.this, "不允許此操作。 您必須在控制台中啟用此服務。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_WEAK_PASSWORD":
                                Toast.makeText(RegisterActivity.this, "輸入的密碼無效。", Toast.LENGTH_LONG).show();
                                etPassword.setError("密碼無效，必須至少6個字符");
                                etPassword.requestFocus();
                                break;
                        }
                    }
                 }
            });
        }
    }
}
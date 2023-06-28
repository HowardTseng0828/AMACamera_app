package com.example.amacamera.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.amacamera.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;


public class LoginActivity extends AppCompatActivity {

    public static String PREFS_NAME = "AMA CAMERA";
    private static int SPLASH_TIME_OUT = 10;
    private static EditText etEmail, etPassword;
    private TextView txtRegister, txtResPassword;
    private CheckBox cbPassword;
    private Button btnLogin;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    /*private SignInButton btnSignInWithGoogle;
    public static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbPassword = findViewById(R.id.cbPassword);
        txtRegister = findViewById(R.id.txtRegister);
        txtResPassword = findViewById(R.id.txtResPassword);
        btnLogin = findViewById(R.id.btnLogin);

        mDatabase = FirebaseDatabase.getInstance();

        //btnSignInWithGoogle = findViewById(R.id.buttonGmail);

        mAuth = FirebaseAuth.getInstance();


        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        boolean hasLoggedIn = sharedPreferences.getBoolean("hasLoggedIn", false);

        /*
        requestGoogleSignIn();

        btnSignInWithGoogle.setOnClickListener(view -> {
            signIn();
        });
        */

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null && hasLoggedIn == true) {
                    startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                    finish();
                } else {

                }
            }
        }, SPLASH_TIME_OUT);


        cbPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        txtResPassword.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ResPasswordActivity.class));
        });

        btnLogin.setOnClickListener(view -> {
            loginUser();
        });

        txtRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    /*
    private void requestGoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                        } else{
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    */

    private void loginUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("請輸入電子信箱");
            etEmail.requestFocus();
            Toast.makeText(LoginActivity.this, "請輸入電子信箱", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            etPassword.setError("請輸入密碼");
            etPassword.requestFocus();
            Toast.makeText(LoginActivity.this, "請輸入密碼", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("hasLoggedIn", true);
                            editor.commit();
                            Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                            finish();
                            String userId = mAuth.getCurrentUser().getUid();
                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            if (!task.isSuccessful()) {
                                                return;
                                            }else{
                                            String token = task.getResult();
                                            mDatabase.getReference("Devices").child(userId).child("token").setValue(token);
                                            }
                                        }
                                    });
                        } else {
                            mAuth.getCurrentUser().sendEmailVerification();
                            Toast.makeText(LoginActivity.this, "尚未驗證電子信箱", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                        switch (errorCode) {
                            case "ERROR_INVALID_CUSTOM_TOKEN":
                                Toast.makeText(LoginActivity.this, "自定義token不正確。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                Toast.makeText(LoginActivity.this, "自定義token屬於不同的用戶。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_INVALID_CREDENTIAL":
                                Toast.makeText(LoginActivity.this, "提供的身份驗證憑據格式不正確或已過期。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_INVALID_EMAIL":
                                Toast.makeText(LoginActivity.this, "電子郵件地址格式錯誤。", Toast.LENGTH_LONG).show();
                                etEmail.setError("電子郵件地址格式錯誤");
                                etEmail.requestFocus();
                                break;
                            case "ERROR_WRONG_PASSWORD":
                                Toast.makeText(LoginActivity.this, "密碼不正確。", Toast.LENGTH_LONG).show();
                                etPassword.setError("密碼不正確");
                                etPassword.requestFocus();
                                etPassword.setText("");
                                break;
                            case "ERROR_USER_MISMATCH":
                                Toast.makeText(LoginActivity.this, "提供的憑證與先前登錄的用戶不相符。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_REQUIRES_RECENT_LOGIN":
                                Toast.makeText(LoginActivity.this, "需要最近的身份驗證。 在重試此請求之前再次登錄。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                Toast.makeText(LoginActivity.this, "已存在具有相同電子郵件地址但登錄憑證不同的帳戶。 使用與此電子郵件地址關聯的提供商登錄。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_EMAIL_ALREADY_IN_USE":
                                Toast.makeText(LoginActivity.this, "該電子郵件地址已被另一個帳戶使用。", Toast.LENGTH_LONG).show();
                                etEmail.setError("該電子郵件地址已被另一個帳戶使用");
                                etEmail.requestFocus();
                                break;
                            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                Toast.makeText(LoginActivity.this, "此憑證已與其他用戶帳戶相關聯。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_USER_DISABLED":
                                Toast.makeText(LoginActivity.this, "該用戶帳戶已被管理員禁用。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_USER_TOKEN_EXPIRED":
                                Toast.makeText(LoginActivity.this, "用戶的憑證不再有效。 用戶必須重新登錄。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_USER_NOT_FOUND":
                                Toast.makeText(LoginActivity.this, "為查詢到與此對應的用戶記錄， 該用戶可能已被刪除。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_OPERATION_NOT_ALLOWED":
                                Toast.makeText(LoginActivity.this, "不允許此操作。 您必須在控制台中啟用此服務。", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_WEAK_PASSWORD":
                                Toast.makeText(LoginActivity.this, "輸入的密碼無效。", Toast.LENGTH_LONG).show();
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

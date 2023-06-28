package com.example.amacamera.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.amacamera.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

public class AccountActivity extends AppCompatActivity {

    private TextView txtEmail;
    private EditText etName, etAddress;
    private Spinner spSex;
    private Button btnUpdate, btnUpEmail, btnUpPassword;

    private String myUri = "";
    private ImageView updateProfile;
    private Uri imageUri;
    private StorageReference storageProfilePicsRef;
    private StorageTask uploadTask;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        etName = findViewById(R.id.etName);
        txtEmail = findViewById(R.id.txtEmail);
        etAddress = findViewById(R.id.etAddress);
        spSex = findViewById(R.id.spSex);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpEmail = findViewById(R.id.btnUpEmail);
        btnUpPassword = findViewById(R.id.btnUpPassword);
        updateProfile = findViewById(R.id.profile_image);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child(mAuth.getCurrentUser().getUid());

        getUserInfo();

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(1, 1).start(AccountActivity.this);
            }
        });

        btnUpEmail.setOnClickListener(view -> {
            String userId = mAuth.getCurrentUser().getUid();
            final EditText checkPassword = new EditText(view.getContext());
            final EditText resetEmail = new EditText(view.getContext());
            final AlertDialog.Builder ResetDialog = new AlertDialog.Builder(view.getContext());
            final AlertDialog.Builder CheckDialog = new AlertDialog.Builder(view.getContext());
            final AlertDialog.Builder ConfirmDialog = new AlertDialog.Builder(view.getContext());
            CheckDialog.setTitle("請輸入目前密碼");
            CheckDialog.setView(checkPassword);
            CheckDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(mAuth.getCurrentUser().getEmail(), checkPassword.getText().toString());
                    mAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ResetDialog.setTitle("請輸入新的電子郵件地址?");
                                ResetDialog.setView(resetEmail);
                                ResetDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ConfirmDialog.setTitle("確定重置電子郵件地址");
                                        ConfirmDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                String email = resetEmail.getText().toString();
                                                mAuth.getCurrentUser().updateEmail(email)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    mAuth.getCurrentUser().sendEmailVerification();
                                                                    mAuth.signOut();
                                                                    mDatabase.getReference("Users").child(userId).child("email").setValue(email);
                                                                    startActivity(new Intent(AccountActivity.this, LoginActivity.class));
                                                                    finish();
                                                                    Toast.makeText(AccountActivity.this, "更新電子郵件地址成功，請重新登錄", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                                                switch (errorCode) {
                                                                    case "ERROR_INVALID_CUSTOM_TOKEN":
                                                                        Toast.makeText(AccountActivity.this, "自定義token不正確。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                                                        Toast.makeText(AccountActivity.this, "自定義token屬於不同的用戶。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_INVALID_CREDENTIAL":
                                                                        Toast.makeText(AccountActivity.this, "提供的身份驗證憑據格式不正確或已過期。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_INVALID_EMAIL":
                                                                        Toast.makeText(AccountActivity.this, "電子郵件地址格式錯誤。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_WRONG_PASSWORD":
                                                                        Toast.makeText(AccountActivity.this, "密碼不正確。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_USER_MISMATCH":
                                                                        Toast.makeText(AccountActivity.this, "提供的憑證與先前登錄的用戶不相符。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_REQUIRES_RECENT_LOGIN":
                                                                        Toast.makeText(AccountActivity.this, "需要最近的身份驗證。 在重試此請求之前再次登錄。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                                                        Toast.makeText(AccountActivity.this, "已存在具有相同電子郵件地址但登錄憑證不同的帳戶。 使用與此電子郵件地址關聯的提供商登錄。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                                                        Toast.makeText(AccountActivity.this, "該電子郵件地址已被另一個帳戶使用。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                                                        Toast.makeText(AccountActivity.this, "此憑證已與其他用戶帳戶相關聯。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_USER_DISABLED":
                                                                        Toast.makeText(AccountActivity.this, "該用戶帳戶已被管理員禁用。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_USER_TOKEN_EXPIRED":
                                                                        Toast.makeText(AccountActivity.this, "用戶的憑證不再有效。 用戶必須重新登錄。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_USER_NOT_FOUND":
                                                                        Toast.makeText(AccountActivity.this, "為查詢到與此對應的用戶記錄， 該用戶可能已被刪除。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_OPERATION_NOT_ALLOWED":
                                                                        Toast.makeText(AccountActivity.this, "不允許此操作。 您必須在控制台中啟用此服務。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_WEAK_PASSWORD":
                                                                        Toast.makeText(AccountActivity.this, "輸入的密碼無效。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                                        ConfirmDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                        ConfirmDialog.create().show();
                                    }
                                });
                                ResetDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                ResetDialog.create().show();
                            } else {
                                Toast.makeText(AccountActivity.this, "密碼輸入錯誤", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            CheckDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            CheckDialog.create().show();
        });

        btnUpPassword.setOnClickListener(view -> {
            final EditText checkPassword = new EditText(view.getContext());
            final EditText resetPassword = new EditText(view.getContext());
            final AlertDialog.Builder ResetDialog = new AlertDialog.Builder(view.getContext());
            final AlertDialog.Builder CheckDialog = new AlertDialog.Builder(view.getContext());
            final AlertDialog.Builder ConfirmDialog = new AlertDialog.Builder(view.getContext());
            CheckDialog.setTitle("請輸入目前密碼");
            CheckDialog.setView(checkPassword);
            CheckDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(mAuth.getCurrentUser().getEmail(), checkPassword.getText().toString());
                    mAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ResetDialog.setTitle("請輸入新密碼");
                                ResetDialog.setView(resetPassword);
                                ResetDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ConfirmDialog.setTitle("確定重置密碼?");
                                        ConfirmDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                String password = resetPassword.getText().toString();
                                                mAuth.getCurrentUser().updatePassword(password)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    mAuth.signOut();
                                                                    startActivity(new Intent(AccountActivity.this, LoginActivity.class));
                                                                    finish();
                                                                    Toast.makeText(AccountActivity.this, "更新密碼成功，請重新登錄", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                                                switch (errorCode) {
                                                                    case "ERROR_INVALID_CUSTOM_TOKEN":
                                                                        Toast.makeText(AccountActivity.this, "自定義token不正確。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                                                        Toast.makeText(AccountActivity.this, "自定義token屬於不同的用戶。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_INVALID_CREDENTIAL":
                                                                        Toast.makeText(AccountActivity.this, "提供的身份驗證憑據格式不正確或已過期。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_INVALID_EMAIL":
                                                                        Toast.makeText(AccountActivity.this, "電子郵件地址格式錯誤。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_WRONG_PASSWORD":
                                                                        Toast.makeText(AccountActivity.this, "密碼不正確。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_USER_MISMATCH":
                                                                        Toast.makeText(AccountActivity.this, "提供的憑證與先前登錄的用戶不相符。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_REQUIRES_RECENT_LOGIN":
                                                                        Toast.makeText(AccountActivity.this, "需要最近的身份驗證。 在重試此請求之前再次登錄。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                                                        Toast.makeText(AccountActivity.this, "已存在具有相同電子郵件地址但登錄憑證不同的帳戶。 使用與此電子郵件地址關聯的提供商登錄。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                                                        Toast.makeText(AccountActivity.this, "該電子郵件地址已被另一個帳戶使用。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                                                        Toast.makeText(AccountActivity.this, "此憑證已與其他用戶帳戶相關聯。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_USER_DISABLED":
                                                                        Toast.makeText(AccountActivity.this, "該用戶帳戶已被管理員禁用。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_USER_TOKEN_EXPIRED":
                                                                        Toast.makeText(AccountActivity.this, "用戶的憑證不再有效。 用戶必須重新登錄。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_USER_NOT_FOUND":
                                                                        Toast.makeText(AccountActivity.this, "為查詢到與此對應的用戶記錄， 該用戶可能已被刪除。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_OPERATION_NOT_ALLOWED":
                                                                        Toast.makeText(AccountActivity.this, "不允許此操作。 您必須在控制台中啟用此服務。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                    case "ERROR_WEAK_PASSWORD":
                                                                        Toast.makeText(AccountActivity.this, "輸入的密碼無效。", Toast.LENGTH_LONG).show();
                                                                        break;
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                                        ConfirmDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                        ConfirmDialog.create().show();
                                    }
                                });
                                ResetDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                ResetDialog.create().show();
                            } else {
                                Toast.makeText(AccountActivity.this, "密碼輸入錯誤", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            CheckDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            CheckDialog.create().show();
        });


        btnUpdate.setOnClickListener(view -> {
            new Thread() {
                @Override
                public void run() {
                    String userId = mAuth.getCurrentUser().getUid();
                    String email = mAuth.getCurrentUser().getEmail();
                    String name = etName.getText().toString();
                    String sex = spSex.getSelectedItem().toString();
                    String address = etAddress.getText().toString();
                    if (name.equals("")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                etName.setError("未輸入姓名");
                                etName.requestFocus();
                                Toast.makeText(AccountActivity.this, "未輸入姓名", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        mAuth.getCurrentUser().updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });
                        if (imageUri != null) {
                            final StorageReference fileRef = storageProfilePicsRef
                                    .child("image");
                            uploadTask = fileRef.putFile(imageUri);
                            uploadTask.continueWithTask(new Continuation() {
                                @Override
                                public Object then(@NonNull Task task) throws Exception {
                                    return fileRef.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = (Uri) task.getResult();
                                        myUri = downloadUri.toString();
                                        mDatabase.getReference("Users").child(mAuth.getCurrentUser().getUid()).child("image").setValue(myUri);
                                    }
                                }
                            });
                        }
                        mDatabase.getReference("Users").child(userId).child("email").setValue(email);
                        mDatabase.getReference("Users").child(userId).child("name").setValue(name);
                        mDatabase.getReference("Users").child(userId).child("gender").setValue(sex);
                        mDatabase.getReference("Users").child(userId).child("address").setValue(address);
                        Looper.prepare();
                        Toast.makeText(AccountActivity.this, "更新資料成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AccountActivity.this, MenuActivity.class));
                        finish();
                        Looper.loop();
                    }
                }
            }.start();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            updateProfile.setImageURI(imageUri);
        } else {
            Toast.makeText(AccountActivity.this, "發生錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
        }
    }

    public void getUserInfo() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.getReference("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    String image = String.valueOf(dataSnapshot.child("image").getValue());
                    Glide.with(AccountActivity.this)
                            .load(image)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .circleCrop()
                            .fitCenter()
                            .error(R.mipmap.ic_launcher_round)
                            .into(updateProfile);
                    String sex = String.valueOf(dataSnapshot.child("gender").getValue());
                    String address = String.valueOf(dataSnapshot.child("address").getValue());
                    txtEmail.setText(mAuth.getCurrentUser().getEmail());
                    etName.setText(mAuth.getCurrentUser().getDisplayName());
                    etAddress.setText(address);
                    if (sex.equals("未選擇")) {
                        spSex.setSelection(0);
                    } else if (sex.equals("生理男")) {
                        spSex.setSelection(1);
                    } else if (sex.equals("生理女")) {
                        spSex.setSelection(2);
                    }
                }
            }
        });
    }
}


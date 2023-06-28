package com.example.amacamera.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.amacamera.R;
import com.example.amacamera.activitys.AccountActivity;
import com.example.amacamera.activitys.HelpActivity;
import com.example.amacamera.activitys.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class ProfileFragment extends Fragment {

    private Button btnLogout, btnReminders, btnHelp, btnAccount;

    private TextView txtName;
    private ImageView profile_image;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //AccessModel access_model = new AccessModel();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnReminders = view.findViewById(R.id.btnReminders);
        btnHelp = view.findViewById(R.id.btnHelp);
        btnAccount = view.findViewById(R.id.btnAccount);

        profile_image = view.findViewById(R.id.profile_image);
        txtName = view.findViewById(R.id.txtName);

        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.getReference("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (getContext() == null) {
                    return;
                } else {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        String image = String.valueOf(dataSnapshot.child("image").getValue());
                        Glide.with(getContext())
                                .load(image)
                                .placeholder(R.mipmap.ic_launcher_round)
                                .circleCrop()
                                .fitCenter()
                                .error(R.mipmap.ic_launcher_round)
                                .into(profile_image);
                        txtName.setText(mAuth.getCurrentUser().getDisplayName());
                        //access_model.setAccess("Users");
                    }
                }
            }
        });

        /*mDatabase.getReference("Admins").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (getContext() == null) {
                    return;
                } else {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        String image = String.valueOf(dataSnapshot.child("image").getValue());
                        Glide.with(getContext())
                                .load(image)
                                .placeholder(R.mipmap.ic_launcher_round)
                                .circleCrop()
                                .fitCenter()
                                .error(R.mipmap.ic_launcher_round)
                                .into(profile_image);
                        txtName.setText(mAuth.getCurrentUser().getDisplayName());
                        access_model.setAccess("Admins");
                    }
                }
            }
        });*/

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplication(), AccountActivity.class));
            }
        });

        btnReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendarEvent = Calendar.getInstance();
                startActivity(new Intent(Intent.ACTION_EDIT)
                        .setType("vnd.android.cursor.item/event")
                        .putExtra("beginTime", calendarEvent.getTimeInMillis())
                        .putExtra("allDay", true)
                        .putExtra("rule", "FREQ=YEARLY")
                        .putExtra("endTime", calendarEvent.getTimeInMillis() + 60 * 60 * 1000)
                        .putExtra("title", "藥物提醒"));
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplication(), HelpActivity.class));
            }
        });

        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplication(), AccountActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity().getApplication(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
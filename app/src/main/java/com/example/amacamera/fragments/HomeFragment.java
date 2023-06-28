package com.example.amacamera.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amacamera.R;
import com.example.amacamera.activitys.HelpActivity;
import com.example.amacamera.activitys.RecordActivity;
import com.example.amacamera.adapter.NewsAdapter;
import com.example.amacamera.entity.NewsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeFragment extends Fragment {

    private RelativeLayout btnRecord,btnHelp;
    private NewsAdapter newsAdapter;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        btnRecord = view.findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplication(), RecordActivity.class));
            }
        });

        btnHelp = view.findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplication(), HelpActivity.class));
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }else{
                            String userId = mAuth.getCurrentUser().getUid();
                            String token = task.getResult();
                            FirebaseDatabase.getInstance().getReference("Devices").child(userId).child("token").setValue(token);
                        }
                    }
                });

        recyclerView = (view).findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<NewsModel> options =
                new FirebaseRecyclerOptions.Builder<NewsModel>()
                        .setQuery(mDatabase.getReference("News"), NewsModel.class)
                        .build();
        newsAdapter = new NewsAdapter(options);
        recyclerView.setAdapter(newsAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        newsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        newsAdapter.stopListening();
    }
}
package com.example.amacamera.activitys;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amacamera.R;
import com.example.amacamera.adapter.HelpAdapter;
import com.example.amacamera.entity.HelpModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class HelpActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HelpAdapter helpAdapter;
    private RelativeLayout btnEmail;
    private androidx.appcompat.widget.SearchView searchView;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        mDatabase = FirebaseDatabase.getInstance();

        searchView = findViewById(R.id.search);
        searchView.setIconified(true);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String str) {
                Search(str);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String str) {
                Search(str);
                return false;
            }
        });

        btnEmail = findViewById(R.id.btnEmail);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HelpActivity.this,EmailActivity.class));
            }
        });

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<HelpModel> options =
                new FirebaseRecyclerOptions.Builder<HelpModel>()
                        .setQuery(mDatabase.getReference("Q&A"), HelpModel.class)
                        .build();
        helpAdapter = new HelpAdapter(options);
        recyclerView.setAdapter(helpAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        helpAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        helpAdapter.stopListening();
    }

    private void Search(String str) {
        FirebaseRecyclerOptions<HelpModel> options =
                new FirebaseRecyclerOptions.Builder<HelpModel>()
                        .setQuery(mDatabase.getReference("Q&A").orderByChild("title").startAt(str).endAt(str + "\uf8ff"), HelpModel.class)
                        .build();
        helpAdapter = new HelpAdapter(options);
        helpAdapter.startListening();
        recyclerView.setAdapter(helpAdapter);
    }
}
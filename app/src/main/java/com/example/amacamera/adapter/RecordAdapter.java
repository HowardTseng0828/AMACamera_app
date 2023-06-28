package com.example.amacamera.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amacamera.R;
import com.example.amacamera.entity.RecordModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RecordAdapter extends FirebaseRecyclerAdapter<RecordModel, RecordAdapter.MyViewHolder> {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private int hour,min;

    public RecordAdapter(FirebaseRecyclerOptions<RecordModel> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull RecordModel recordModel) {
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        holder.txtRecord.setText(recordModel.getTime());

    }

    @NonNull
    @Override
    public RecordAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_record, parent, false)
        );
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtRecord;


        public MyViewHolder(@NonNull View view) {
            super(view);
            txtRecord = (TextView) view.findViewById(R.id.txtRecord);

        }
    }
}

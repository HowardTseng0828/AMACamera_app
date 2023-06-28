package com.example.amacamera.activitys;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amacamera.R;
import com.example.amacamera.adapter.RecordAdapter;
import com.example.amacamera.entity.RecordModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RecordActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecordAdapter recordAdapter;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private ImageButton btnDelayer;
    private int hour,min,year,month,day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        btnDelayer = findViewById(R.id.btnDelayer);

        String userId = mAuth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<RecordModel> options =
                new FirebaseRecyclerOptions.Builder<RecordModel>()
                        .setQuery(mDatabase.getReference("Records").child(userId).orderByChild("time"), RecordModel.class)
                        .build();
        recordAdapter = new RecordAdapter(options);
        recyclerView.setAdapter(recordAdapter);

        btnDelayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int y, int mn, int d) {
                        final TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int h, int m) {
                                String userId = mAuth.getCurrentUser().getUid();
                                int mon = mn+1;
                                String delayer = y + "-" + mon + "-" + d + "\t" + h + ":" + m + ":" + "00";
                                mDatabase.getReference("Devices").child(userId).child("delayer").setValue(delayer);
                            }
                        };
                        TimePickerDialog timePickerDialog = new TimePickerDialog(RecordActivity.this,onTimeSetListener,calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE),true);
                        timePickerDialog.setTitle("暫停通知至所選時間");
                        timePickerDialog.show();
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(RecordActivity.this,onDateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.setTitle("暫停通知至所選時間");
                datePickerDialog.show();
            }
        });

        /*mDatabase.getReference("Records").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userId = mAuth.getCurrentUser().getUid();
                if(dataSnapshot.exists()){
                    recordsList = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        if(ds.hasChild(userId)) {
                            recordsList.add(ds.child(userId).getValue(RecordModel.class));
                        }
                    }
                    recordAdapter = new RecordAdapter(recordsList);
                    recyclerView.setAdapter(recordAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();
        recordAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        recordAdapter.stopListening();
    }
}
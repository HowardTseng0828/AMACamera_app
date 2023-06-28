package com.example.amacamera.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amacamera.R;
import com.example.amacamera.activitys.NewsActivity;
import com.example.amacamera.entity.NewsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class NewsAdapter extends FirebaseRecyclerAdapter<NewsModel, NewsAdapter.MyViewHolder> {


    public NewsAdapter(FirebaseRecyclerOptions<NewsModel> options) {
        super(options);
    }

    @NonNull
    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsAdapter.MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_news, parent, false)
        );
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull NewsModel newsModel) {
        holder.txtTitle.setText(newsModel.getTitle());
        holder.btnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(view.getContext(), NewsActivity.class)
                        .putExtra("newsModel",newsModel)
                );
            }
        });
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private RelativeLayout btnNews;

        public MyViewHolder(@NonNull View view) {
            super(view);

            txtTitle = view.findViewById(R.id.txtTitle);
            btnNews = view.findViewById(R.id.btnNews);
        }
    }
}

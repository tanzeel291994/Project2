package com.app.android.project2;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Movie_adapter extends RecyclerView.Adapter<Movie_adapter.MyViewHolder> {
    Context mContext;
     interface OnItemClickListener
    {
        void onItemClick(Movie_model movie_item);
    }
    private final OnItemClickListener listener;
    private final List<Movie_model> movie_items;

public class MyViewHolder extends RecyclerView.ViewHolder {
public ImageView poster;

    public MyViewHolder(View view) {
        super(view);
        poster=(ImageView)view.findViewById(R.id.poster);

    }
     void bind(final Movie_model movie_item, final OnItemClickListener listener) {

        Picasso.with(mContext).load(movie_item.poster_path).into(poster);

        itemView.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                listener.onItemClick(movie_item);

            }

        });

    }
}


    public Movie_adapter(Context context,OnItemClickListener listener,List<Movie_model> movie_items)
    {
        mContext=context;
        this.listener=listener;
        this.movie_items=movie_items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(movie_items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return movie_items.size();
    }
}

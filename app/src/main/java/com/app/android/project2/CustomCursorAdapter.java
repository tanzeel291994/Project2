package com.app.android.project2;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.MovieViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    @Override
    public CustomCursorAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item, parent, false);

        return null;
    }
    public CustomCursorAdapter(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    public void onBindViewHolder(CustomCursorAdapter.MovieViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
    class MovieViewHolder extends RecyclerView.ViewHolder {



        public MovieViewHolder(View itemView) {
            super(itemView);

           // taskDescriptionView = (TextView) itemView.findViewById(R.id.taskDescription);
           // priorityView = (TextView) itemView.findViewById(R.id.priorityTextView);
        }
    }
}

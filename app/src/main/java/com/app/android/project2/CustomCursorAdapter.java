package com.app.android.project2;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.app.android.project2.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.MovieViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    private ArrayList<Movie_model> movie_list;

    interface OnItemClickListener {
        void onItemClick(Movie_model movie_item);
    }

    private final OnItemClickListener listener;

    @Override
    public CustomCursorAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    public CustomCursorAdapter(Context mContext,OnItemClickListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        movie_list=new ArrayList<Movie_model>();

    }

    @Override
    public void onBindViewHolder(CustomCursorAdapter.MovieViewHolder holder, int position) {
        int poster_Path_Index = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        int background_Path_Index = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKGROUND_PATH);
        int movie_ID_Index = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int title_Index = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        int date_Index = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        int votes_Index = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int overview_Index = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
/*
        movie_list.add(new Movie_model(mCursor.getString(movie_ID_Index)
                , mCursor.getString(background_Path_Index),
                mCursor.getString(title_Index),
                mCursor.getString(date_Index),
                mCursor.getString(votes_Index), mCursor.getString(overview_Index)
                , mCursor.getString(poster_Path_Index)));
*/
        mCursor.moveToPosition(position);
        String poster_path = mCursor.getString(poster_Path_Index);
        movie_list.add(new Movie_model(mCursor.getString(movie_ID_Index)
                , mCursor.getString(background_Path_Index),
                mCursor.getString(title_Index),
                mCursor.getString(date_Index),
                mCursor.getString(overview_Index), mCursor.getString(votes_Index)
                , mCursor.getString(poster_Path_Index)));
      /* Picasso.with(mContext)
                .load(poster_path)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.poster);*/
        holder.bind(movie_list.get(position), listener);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;

        public MovieViewHolder(View itemView) {
            super(itemView);

            poster = (ImageView) itemView.findViewById(R.id.poster);

        }

        void bind(final Movie_model movie_item, final OnItemClickListener listener) {

            Picasso.with(mContext).load(movie_item.poster_path).into(poster);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.onItemClick(movie_item);

                }

            });
        }
    }
}

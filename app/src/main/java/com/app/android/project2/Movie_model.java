package com.app.android.project2;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Movie_model implements Parcelable
{
    public Movie_model(String id,String thumbnail_path,String title,String date, String synopsis,String ratings,String poster_path)
    {
        this.id=id;
        this.thumbnail_path=thumbnail_path;
        this.title=title;
        this.date=date;
        this.ratings=ratings;
        this.synopsis=synopsis;
        this.poster_path=poster_path;
    }

    public String id;
    public String thumbnail_path;
    public String title;
    public String date;
    public String synopsis;
    public String ratings;
    public String poster_path;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(id);
        out.writeString(thumbnail_path);
        out.writeString(title);
        out.writeString(date);
        out.writeString(synopsis);
        out.writeString(ratings);
        out.writeString(poster_path);
    }
    private Movie_model(Parcel in) {
        id = in.readString();
        thumbnail_path = in.readString();
        title = in.readString();
        date = in.readString();
        synopsis = in.readString();
        ratings = in.readString();
        poster_path = in.readString();
    }

    public static final Parcelable.Creator<Movie_model> CREATOR = new Parcelable.Creator<Movie_model>() {
        public Movie_model createFromParcel(Parcel in) {
            return new Movie_model(in);
        }

        public Movie_model[] newArray(int size) {
            return new Movie_model[size];
        }
    };
}
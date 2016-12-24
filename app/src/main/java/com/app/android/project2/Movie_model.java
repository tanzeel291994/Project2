package com.app.android.project2;

import java.io.Serializable;

public class Movie_model implements Serializable
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


}
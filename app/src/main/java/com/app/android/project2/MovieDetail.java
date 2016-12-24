package com.app.android.project2;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.android.project2.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieDetail extends AppCompatActivity {
    TextView title_text;
    TextView rating_text ;
    TextView date_text ;
    TextView synopsis_text;
    ImageView thumbnail ;
    Movie_model movie_item;
    Button watchTrailer;
    HttpURLConnection urlConnection=null;
    BufferedReader reader=null;
    String jsonstring=null;
    String mKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
         movie_item=(Movie_model) getIntent().getSerializableExtra("Movie_item");
        title_text = (TextView) findViewById(R.id.original_title);
         rating_text = (TextView) findViewById(R.id.user_rating);
        date_text = (TextView) findViewById(R.id.release_date);
        synopsis_text = (TextView) findViewById(R.id.synopsis);
        thumbnail = (ImageView) findViewById(R.id.thumbnail_image_view);
        watchTrailer=(Button)findViewById(R.id.trailer_btn);
        watchTrailer.setEnabled(false);
        try
        {
            title_text.setText(movie_item.title);
            rating_text.setText(movie_item.ratings+"/10");
            synopsis_text.setText(movie_item.synopsis);
            date_text.setText(movie_item.date);
            Picasso.with(this)
                    .load(movie_item.thumbnail_path)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(thumbnail);

        }
        catch (Exception e)
        {

        }
        new NetworkingForVideoResponse().execute(movie_item.id);
    }
    String loadTrailer(String id) throws IOException
    {
        URL url = new URL("http://api.themoviedb.org/3/movie/"+id+"?api_key=60e7c427c564cf915fd06a078398855a&append_to_response=videos");
        String key=null;
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream inputStream=urlConnection.getInputStream();
        StringBuffer  stringBuffer=new StringBuffer ();
        reader=new BufferedReader( new InputStreamReader(inputStream));
        String line;
        while ((line=reader.readLine())!=null)
        {
            stringBuffer.append(line + "\n");
        }
        if(stringBuffer.length()==0)
        {
            jsonstring=null; //buffer was empty
        }
        else
        {
            jsonstring = stringBuffer.toString();
        }
        if(jsonstring!=null)
        {
            try
            {
                JSONObject jsonObj = new JSONObject(jsonstring);
                key=jsonObj.getJSONObject("videos").getJSONArray("results").getJSONObject(0).getString("key");
                mKey=key;


            }
            catch(JSONException e)
            {

            }
        }
        if(urlConnection!=null)
            urlConnection.disconnect();
        if(reader!=null)
        {
            try{reader.close();}
            catch (final IOException e){}
        }
        return key;
    }
    public class NetworkingForVideoResponse extends AsyncTask<String, Void,String>
    {


        @Override
        protected String doInBackground(String... params)
        {
            try {
                return  loadTrailer(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return  null;
            }
        }



        protected void onPostExecute(String result) {
            super.onPostExecute(result);
           if(result!=null) {
               watchTrailer.setEnabled(true);
           }
        }
    }
    public void watchTrailer(View view)
    {
        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.youtube.com/watch?v="+mKey)));
    }
    public void addToFav(View view)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, synopsis_text.getText().toString());
        contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, title_text.getText().toString());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, rating_text.getText().toString());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, date_text.getText().toString());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie_item.poster_path);
        contentValues.put(MovieContract.MovieEntry.COLUMN_BACKGROUND_PATH, movie_item.thumbnail_path);
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie_item.id);

        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

}

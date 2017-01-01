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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.app.android.project2.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MovieDetail extends AppCompatActivity {
    TextView title_text;
    TextView rating_text ;
    TextView date_text ;
    TextView synopsis_text;
    ImageView thumbnail ;
    Movie_model movie_item;
    Button watchTrailer;
    Button favMovie;
    HttpURLConnection urlConnection=null;
    BufferedReader reader=null;
    String jsonstring=null;
    String mKey;
    List<HashMap<String, String>> fillMaps;
    ListView lv;
    SimpleAdapter adapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
         movie_item=(Movie_model) getIntent().getParcelableExtra("Movie_item");
        String type=getIntent().getStringExtra("type");
        title_text = (TextView) findViewById(R.id.original_title);
         rating_text = (TextView) findViewById(R.id.user_rating);
        date_text = (TextView) findViewById(R.id.release_date);
        synopsis_text = (TextView) findViewById(R.id.synopsis);
        thumbnail = (ImageView) findViewById(R.id.thumbnail_image_view);
        watchTrailer=(Button)findViewById(R.id.trailer_btn);
        favMovie=(Button)findViewById(R.id.fav_btn);
        String[] from = new String[] {"author", "comment"};
        int[] to = new int[] { R.id.author, R.id.comment };
        fillMaps = new ArrayList<HashMap<String, String>>();
        lv= (ListView)findViewById(R.id.reviews);
        adapter = new SimpleAdapter(this, fillMaps, R.layout.review_item, from, to);
        lv.setAdapter(adapter);
        if(Objects.equals(type, "fav"))
            favMovie.setVisibility(View.INVISIBLE);
        watchTrailer.setEnabled(false);
        try
        {
           Log.i("Tag",movie_item.ratings);
           Log.i("Tag",movie_item.synopsis);
            title_text.setText(movie_item.title);
            rating_text.setText(movie_item.ratings);
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
        new NetworkingForReview().execute(movie_item.id);
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
    void loadReview(String id) throws IOException
    {
        URL url = new URL("http://api.themoviedb.org/3/movie/"+id+"/reviews?api_key=60e7c427c564cf915fd06a078398855a");
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
                JSONArray result_list=jsonObj.getJSONArray("results");
                for(int i=0;i<result_list.length();i++)
                {
                    JSONObject jobj=result_list.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("author",jobj.getString("author"));
                    map.put("comment",jobj.getString("content"));
                    fillMaps.add(map);
                }
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
    public class NetworkingForReview extends AsyncTask<String, Void,Void>
    {
        @Override
        protected Void doInBackground(String... params) {

            try {
                loadReview(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
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

package com.app.android.project2;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.android.project2.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    HttpURLConnection urlConnection=null;
    BufferedReader reader=null;
    String jsonstring=null;
    String TAG_RESULTS="results";
    String TAG_POSTER_PATH="poster_path";
    JSONArray result_list;
    private Cursor mData;
    int RESULT_STATUS;
    Movie_adapter madapter;
    public static ArrayList<Movie_model> movies;
    String discover_movies="https://api.themoviedb.org/3/movie/popular?api_key=60e7c427c564cf915fd06a078398855a&page=1";
    String sort_movies="https://api.themoviedb.org/3/movie/top_rated?api_key=60e7c427c564cf915fd06a078398855a&page=1";
    Menu menuTemp;
    RecyclerView recyclerview;
    Networking mnetworking;
    private static final String STATE_COUNTER = "counter";
    boolean firstChecked=false;
    boolean secondChecked=false;
    boolean thirdChecked=false;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 0;

    private CustomCursorAdapter mAdapterFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        movies=new ArrayList<Movie_model>();

        madapter=new Movie_adapter(this,new Movie_adapter.OnItemClickListener(){
            @Override
            public void onItemClick(Movie_model movie_item) {
                Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(getApplicationContext(),MovieDetail.class);
                intent.putExtra("Movie_item", movie_item);
                intent.putExtra("type", "normal");
                startActivity(intent);
            };
        },movies);
        mAdapterFav = new CustomCursorAdapter(this,new CustomCursorAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(Movie_model movie_item) {
                Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(getApplicationContext(),MovieDetail.class);
                intent.putExtra("Movie_item", movie_item);
                intent.putExtra("type", "fav");
                startActivity(intent);
            };});
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(madapter);
      if(savedInstanceState!=null )
        {
            Log.i("tag", savedInstanceState.getString("type"));
            firstChecked=savedInstanceState.getBoolean("0");
            secondChecked=savedInstanceState.getBoolean("1");
            thirdChecked=savedInstanceState.getBoolean("2");
            if(Objects.equals(savedInstanceState.getString("type"), "normal")) {
                Log.i("tag", String.valueOf(movies.size()));
                movies = savedInstanceState.getParcelableArrayList("list");
                Log.i("tag", String.valueOf(movies.size()));
                // madapter.notifyDataSetChanged(); tried using this ,but the screen comes ,empty so reinitalized the adapter here
                madapter = new Movie_adapter(this, new Movie_adapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Movie_model movie_item) {
                        Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MovieDetail.class);
                        intent.putExtra("Movie_item", movie_item);
                        intent.putExtra("type", "normal");
                        startActivity(intent);
                    }

                    ;
                }, movies);
                recyclerview.setAdapter(madapter);
                //  madapter.notifyDataSetChanged();
            }
            else
            {
                recyclerview.setAdapter(mAdapterFav);
                getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);}
        }
        else
       {
            mnetworking= new Networking();
            mnetworking.execute(discover_movies);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("Tag", String.valueOf(menuTemp.getItem(0).isEnabled()));
        Log.i("Tag", String.valueOf(menuTemp.getItem(2).isEnabled()));
        if(!menuTemp.getItem(0).isEnabled()||!menuTemp.getItem(1).isEnabled())
        {
            outState.putParcelableArrayList("list",movies);
            outState.putString("type","normal");

        }
        else
        {
            outState.putString("type","fav");
        }
        outState.putBoolean("0",menuTemp.getItem(0).isEnabled());
        outState.putBoolean("1",menuTemp.getItem(1).isEnabled());
        outState.putBoolean("2",menuTemp.getItem(2).isEnabled());

    }


    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
       if(!firstChecked&&!secondChecked&&!thirdChecked)
       {
           menu.getItem(1).setEnabled(false);
       }
        else
       {
           menu.getItem(0).setEnabled(firstChecked);
           menu.getItem(1).setEnabled(secondChecked);
           menu.getItem(2).setEnabled(thirdChecked);
       }
        menuTemp=menu;
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())
        {
            case R.id.sort:
                Toast.makeText(getBaseContext(),"Sorting......", Toast.LENGTH_SHORT).show();

                movies.clear();
                menuTemp.getItem(1).setEnabled(true);
                menuTemp.getItem(0).setEnabled(false);
                menuTemp.getItem(2).setEnabled(true);
                recyclerview.setAdapter(madapter);
                new Networking().execute(sort_movies);
                break;
            case R.id.popular:
                Toast.makeText(getBaseContext(),"Sorting.by popularity", Toast.LENGTH_SHORT).show();
                //mnetworking.cancel(true);
                movies.clear();

                menuTemp.getItem(0).setEnabled(true);
                menuTemp.getItem(2).setEnabled(true);
                menuTemp.getItem(1).setEnabled(false);
                recyclerview.setAdapter(madapter);
                new Networking().execute(discover_movies);
                break;
            case R.id.favourites:
                Toast.makeText(getBaseContext(), "Fetching your favourites", Toast.LENGTH_SHORT).show();
               // mnetworking.cancel(true);
                movies.clear();

                menuTemp.getItem(2).setEnabled(false);
                menuTemp.getItem(1).setEnabled(true);
                menuTemp.getItem(0).setEnabled(true);

                recyclerview.setAdapter(mAdapterFav);
                 /*
                Ensure a loader is initialized and active. If the loader doesn't already exist, one is
                created, otherwise the last created loader is re-used.
                */
                getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
                break;
        }
        return true;
    }
    public void loadingPoster(String s) throws IOException
    {
        URL url = new URL(s);
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
                result_list=jsonObj.getJSONArray(TAG_RESULTS);
                RESULT_STATUS=1;
                for(int i=0;i<result_list.length();i++)
                {
                    JSONObject jobj=result_list.getJSONObject(i);
                    movies.add(new Movie_model(jobj.getString("id"),
                            "http://image.tmdb.org/t/p/w500" + jobj.getString("backdrop_path"),
                            jobj.getString("original_title"), jobj.getString("release_date"),
                            jobj.getString("overview"),
                            jobj.getString("vote_average"),
                            "http://image.tmdb.org/t/p/w500"+jobj.getString(TAG_POSTER_PATH)
                    ));

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    Log.i("tag","ini");
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null, null, null, null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }




    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapterFav.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapterFav.swapCursor(null);
    }


    public class Networking extends AsyncTask<String, Void,Void>
    {
        @Override
        protected Void doInBackground(String... params)
        {
            try
            {
                loadingPoster(params[0]);
            }
            catch (IOException e){}
            return  null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(RESULT_STATUS==1)
            {
                madapter.notifyDataSetChanged();

            }

        }
    }
}

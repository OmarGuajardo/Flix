package com.example.flix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flix.adapters.MovieAdapter;
import com.example.flix.databinding.ActivityMainBinding;
import com.example.flix.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    final static String NOW_PLAYING_ENDPOINT = "https://api.themoviedb.org/3/movie/top_rated?api_key=fb1cb576c71896da8c7c626bae047420";
    final static String TOP_RATED_ENDPOINT = "https://api.themoviedb.org/3/movie/now_playing?api_key=fb1cb576c71896da8c7c626bae047420";

    final static String TAG = "MainActivity";
    List<Movie> movies;
    MovieAdapter movieAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        movies = new ArrayList<>();



        //Create and Adapter
        movieAdapter = new MovieAdapter(this,movies);

        //Set the Adapter on the RecyclerView
        binding.rvMovies.setAdapter(movieAdapter);
        //Set Layout Manager on the RecyclerView (REQUIRED)
        binding.rvMovies.setLayoutManager(new LinearLayoutManager(this));



        AsyncHttpClient client = new AsyncHttpClient();
        fetchData(client,NOW_PLAYING_ENDPOINT);



    }

    //Method to fetch different types of data
    public void fetchData(AsyncHttpClient client, String endpoint){
        client.get(endpoint, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG,"Results " + results.toString());
                    movies.addAll(Movie.fromJsonArray(results));
                    movieAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG,"Hit JSON Exception",e);
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                Log.i(TAG,"onFailure");
            }
        });

    }
}

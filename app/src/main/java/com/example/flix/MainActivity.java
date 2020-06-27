package com.example.flix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Person;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    final static String TOP_RATED_ENDPOINT = "https://api.themoviedb.org/3/movie/top_rated?api_key=fb1cb576c71896da8c7c626bae047420";
    final static String NOW_PLAYING_ENDPOINT = "https://api.themoviedb.org/3/movie/now_playing?api_key=fb1cb576c71896da8c7c626bae047420";
    final static String UPCOMING_ENDPOINT = "https://api.themoviedb.org/3/movie/upcoming?api_key=fb1cb576c71896da8c7c626bae047420";

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




        fetchData(NOW_PLAYING_ENDPOINT);



    }

    //Method to fetch different types of data
    public void fetchData(String endpoint){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(endpoint, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG,"Results " + results.toString());
                    movies.clear();
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

    public List<Movie> merge(List<Movie> l1, List<Movie> l2){
        List<Movie> aux = new ArrayList<>();
        int left = 0;
        int right = 0;
        while (left < l1.size() && right < l2.size()){

            Movie movieLeft = l1.get(left);
            Movie movieRight = l2.get(right);

            int result = movieLeft.getTitle().compareTo(movieRight.getTitle());
            if(result >= 0){
                aux.add(movieLeft);
                left++;
            }
            else{
                aux.add(movieRight);
                right++;
            }
        }
        if(left < l1.size()){
            aux.addAll(l1);
        }
        else{
            aux.addAll(l2);
        }
        return aux;
    }

    public void merge_sort(List<Movie> list){
//        if(list.size() < 2){
//            return list;
//        }
//        else{
//            int mid = list.size()/2;
//            List<Movie> left = list.subList(0,mid-1);
//            List<Movie> right = list.subList(mid,list.size()-1);
//            return merge(merge_sort(left),merge_sort(right));
//        }
        Collections.sort(movies, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Movie m1 = (Movie) o1;
                Movie m2 = (Movie) o2;
                return m1.getTitle().compareToIgnoreCase(m2.getTitle());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options,menu);
        return true;
    }

    public void seeAll(){
        for(int i = 0;i < movies.size();i++){
            Log.d(TAG, "seeAll: movie " + movies.get(i).getTitle());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.Alphabetize:
                merge_sort(movies);
                movieAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Alphabetize", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Top_Rated:
                fetchData(TOP_RATED_ENDPOINT);
                Toast.makeText(this, "Top Rated", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Upcoming:
                fetchData(UPCOMING_ENDPOINT);
                Toast.makeText(this, "Up Coming", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Now_Playing:
                fetchData(NOW_PLAYING_ENDPOINT);
                Toast.makeText(this, "Now Playing", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

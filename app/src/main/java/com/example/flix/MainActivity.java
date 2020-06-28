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
    List<Movie> topRated;
    List<Movie> nowPlaying;
    List<Movie> upComing;
    MovieAdapter movieAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        movies = new ArrayList<>();
        topRated = new ArrayList<>();
        nowPlaying = new ArrayList<>();
        upComing = new ArrayList<>();

        //Create and Adapter
        movieAdapter = new MovieAdapter(this,movies);

        //Set the Adapter on the RecyclerView
        binding.rvMovies.setAdapter(movieAdapter);
        //Set Layout Manager on the RecyclerView (REQUIRED)
        binding.rvMovies.setLayoutManager(new LinearLayoutManager(this));

        fetchData(NOW_PLAYING_ENDPOINT,nowPlaying);
    }

    //Method to fetch different types of data
    public void fetchData(String endpoint, final List<Movie> auxList){

        if(auxList.isEmpty()) {
            Log.d(TAG, "fetching data");
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(endpoint, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i(TAG, "onSuccess");
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        JSONArray results = jsonObject.getJSONArray("results");
                        Log.i(TAG, "Results " + results.toString());
                        movies.clear();
                        movies.addAll(Movie.fromJsonArray(results));
                        auxList.addAll(Movie.fromJsonArray(results));
                        movieAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, "Hit JSON Exception", e);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                    Log.i(TAG, "onFailure");
                }
            });
        }
        else{
            movies.clear();
            movies.addAll(auxList);
            movieAdapter.notifyDataSetChanged();
        }
    }

    public void merge(List<Movie> list, int beg,int mid,int end){
        List<Movie> aux = new ArrayList<Movie>();
       int left = beg;
       int right = mid+1;
       while(left <= mid && right <= end){
           Movie leftMovie = list.get(left);
           Movie rightMovie = list.get(right);
           if(leftMovie.getTitle().compareTo(rightMovie.getTitle()) < 0){
               aux.add(leftMovie);
               left++;
           }
           else {
               aux.add(rightMovie);
               right++;
           }
       }
       if(left<= mid){
           for(int i = left;i<=mid;i++){
               aux.add(list.get(i));
           }
       }
       else{
           for(int i = right;i<=end;i++){
               aux.add(list.get(i));
           }
       }
       for(int i = 0;i<aux.size();i++){
           list.set(beg+i,aux.get(i));
       }
    }

    public void merge_sort(List<Movie> list,int left,int right){
        if(left == right){
            return;
        }
        else{
          int mid = (left+right)/2;
          merge_sort(list,left,mid);
          merge_sort(list,mid+1,right);
          merge(list,left,mid,right);

        }


    }

    public void sort(List<Movie>list){
    //This is using the built in function to sort items
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
                merge_sort(movies,0,movies.size()-1);
                movieAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Alphabetize", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Top_Rated:
                fetchData(TOP_RATED_ENDPOINT,topRated);
                Toast.makeText(this, "Top Rated", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Upcoming:
                fetchData(UPCOMING_ENDPOINT,upComing);
                Toast.makeText(this, "Up Coming", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Now_Playing:
                fetchData(NOW_PLAYING_ENDPOINT,nowPlaying);
                Toast.makeText(this, "Now Playing", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

package com.example.flix;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flix.databinding.ActivityMovieDetailsBinding;
import com.example.flix.databinding.ActivityMovieTrailerBinding;
import com.example.flix.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.MessageFormat;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {


    Movie movie;
    String MOVIE_TRAILER_ENDPOINT = "https://api.themoviedb.org/3/movie/{0}/videos?api_key=fb1cb576c71896da8c7c626bae047420&language=en-US";
    String TAG = "MovieDetailsActivity";
    JSONArray results;
    String trailerURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));

        //getting the MovieID from the object and customizing the endpoint with said ID
        String movieID = String.valueOf(movie.getMovieID());
        String customizedENDPOINT = MessageFormat.format(MOVIE_TRAILER_ENDPOINT, movieID);

        //Fetching the Trailer with the Movie ID
        AsyncHttpClient client = new AsyncHttpClient();


        Log.d(TAG, "onCreate: movie ID "+movie.getMovieID());


        //Making a request to fetch the videos listed under the specific movie
        client.get(customizedENDPOINT, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess: ");
                JSONObject jsonObject = json.jsonObject;
                try {
                    results = jsonObject.getJSONArray("results");
                    setURL(binding);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });


        //Populating info based on the Movie received
        binding.tvTitle.setText(movie.getTitle());
        binding.tvOverview.setText(movie.getOverView());


        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            //Populating the Poster with the image of the Movie
            Glide.with(getApplicationContext())
                    .load(movie.getBackdropPath())
                    .transform(new RoundedCornersTransformation(30, 10))
                    .into(binding.posterImage);
        }else{
            //Populating the Poster with the image of the Movie
            Glide.with(getApplicationContext())
                    .load(movie.getPosterPath())
                    .transform(new RoundedCornersTransformation(30, 10))
                    .into(binding.posterImage);
        }





        //Setting the number of Stars
        float voteAverage = movie.getVoteAverage().floatValue();
        binding.rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        binding.btnTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                intent.putExtra("YouTubeUrl", trailerURL);
                startActivity(intent);

            }
        });

    }

    public void setURL(ActivityMovieDetailsBinding binding){
        //Checking what URL we are going to use
        if(results.length() > 0) {
            for (int i = 0; i < results.length(); i++) {
                try {
                    String site = results.getJSONObject(i).getString("site");
                    if (site.equals("YouTube")) {
                        trailerURL = results.getJSONObject(i).getString("key");
                        Log.d(TAG, "setURL: " + trailerURL);
                        break;
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "No trailer found");
                    binding.btnTrailer.setText("No Trailer Available");
                    binding.btnTrailer.setEnabled(false);
                }
            }
        }
        else{ binding.btnTrailer.setText("No Trailer Available");
            binding.btnTrailer.setClickable(false);}
    }
}

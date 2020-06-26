package com.example.flix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flix.databinding.ActivityMainBinding;
import com.example.flix.databinding.ActivityMovieDetailsBinding;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        //Fetching the Trailer with the Movie ID
        AsyncHttpClient client = new AsyncHttpClient();

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d(TAG, "onCreate: this is the movie ID " + movie.getMovieID());
        //getting the MovieID from the object and customizing the endpoint with said ID
        String movieID = String.valueOf(movie.getMovieID());
        String customizedENDPOINT = MessageFormat.format(MOVIE_TRAILER_ENDPOINT,movieID);

        //Making a request to fetch the videos listed under the specific movie
        client.get(customizedENDPOINT, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess: ");
                JSONObject jsonObject = json.jsonObject;
                try {
                     results = jsonObject.getJSONArray("results");
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
        binding.tvRelease.setText("Release Date: "+movie.getReleaseDate());

        //Populating the Poster with the image of the Movie
        Glide.with(getApplicationContext())
                .load(movie.getPosterPath())
                .transform(new RoundedCornersTransformation(30 , 10))
                .into(binding.posterImage);

        //Setting the number of starts
        float voteAverage = movie.getVoteAverage().floatValue();
        binding.rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        binding.posterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieDetailsActivity.this,MovieTrailerActivity.class);

                try {
                    intent.putExtra("YouTubeUrl",results.getJSONObject(0).getString("key"));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}

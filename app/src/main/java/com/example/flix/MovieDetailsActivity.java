package com.example.flix;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.flix.models.Movie;

import org.parceler.Parcels;

public class MovieDetailsActivity extends AppCompatActivity {


    Movie movie;
    TextView tvTitle;
        TextView tvOverview;
    RatingBar rbVoteAverage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        //Finding the objects
        tvOverview = findViewById(R.id.tvOverview);
        tvTitle = findViewById(R.id.tvTitle);
        rbVoteAverage = (RatingBar)findViewById(R.id.rbVoteAverage);

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        //Populating info based on the Movie received
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverView());

        //Setting the number of starts
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
    }
}

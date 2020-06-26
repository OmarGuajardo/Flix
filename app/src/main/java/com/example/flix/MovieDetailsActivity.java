package com.example.flix;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flix.databinding.ActivityMainBinding;
import com.example.flix.databinding.ActivityMovieDetailsBinding;
import com.example.flix.models.Movie;

import org.parceler.Parcels;

public class MovieDetailsActivity extends AppCompatActivity {


    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));

        //Populating info based on the Movie received
        binding.tvTitle.setText(movie.getTitle());
        binding.tvOverview.setText(movie.getOverView());
        binding.tvRelease.setText("Release Date: "+movie.getReleaseDate());

        //Populating the Poster with the image of the Movie
        Glide.with(getApplicationContext()).load(movie.getPosterPath()).into(binding.posterImage);

        //Setting the number of starts
        float voteAverage = movie.getVoteAverage().floatValue();
        binding.rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
    }
}

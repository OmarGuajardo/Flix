package com.example.flix.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    String title;
    String posterPath;
    String overView;

    public Movie(JSONObject jsonObject) throws JSONException {
        this.title = jsonObject.getString("title");
        this.posterPath =  jsonObject.getString("poster_path");
        this.overView = jsonObject.getString("overview");
    }

    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws  JSONException{
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return ("https://image.tmdb.org/t/p/w342" + posterPath);
    }

    public String getOverView() {
        return overView;
    }
}

package com.example.steveko.popularmovies;

/**
 * Created by steveko on 12/7/16.
 */

public class MovieData {
    //title, release date, movie poster, vote average, and plot synopsis
    public String title;
    public String releaseDate;
    public String posterPath;
    public double rating;
    public String synopsis;

    MovieData(String movieTitle, String date, String path, double movieRating, String plotSynopsis) {
        title = movieTitle;
        releaseDate = date;
        posterPath = path;
        rating = movieRating;
        synopsis = plotSynopsis;
    }
}

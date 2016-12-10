package com.example.steveko.popularmovies;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by steveko on 12/7/16.
 */

public class MovieAdapter extends ArrayAdapter<MovieData> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, List<MovieData> data) {
        // the second argument will not be used, so can be anything
        // It is only used when populating a simple TextView
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieData data = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        ImageView posterView = (ImageView) convertView.findViewById(R.id.list_item_view);
        final String path = BuildConfig.TMDB_MOVIE_BASE_PATH + data.posterPath;
        Picasso.with(getContext()).load(path).into(posterView);

        return convertView;
    }
}

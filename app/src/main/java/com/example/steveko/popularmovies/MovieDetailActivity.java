package com.example.steveko.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_movie_detail, new DetailFragment())
                    .commit();
        }
    }

    public static class DetailFragment extends Fragment {
        private final String LOG_TAG = DetailFragment.class.getSimpleName();

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

            Intent intent = getActivity().getIntent();
            if (intent != null) {
                String title = intent.getStringExtra(getString(R.string.intent_title_key));
                String path = intent.getStringExtra(getString(R.string.intent_path_key));
                String date = intent.getStringExtra(getString(R.string.intent_date_key));
                double rating = intent.getDoubleExtra(getString(R.string.intent_rating_key), 0.0);
                String synopsis = intent.getStringExtra(getString(R.string.intent_synopsis_key));
                TextView titleView = (TextView) rootView.findViewById(R.id.movie_detail_title);
                TextView dateView = (TextView) rootView.findViewById(R.id.movie_detail_release_date);
                TextView ratingView = (TextView) rootView.findViewById(R.id.movie_detail_rating);
                TextView synopsisView = (TextView) rootView.findViewById(R.id.movie_detail_synopsis);
                ImageView posterView = (ImageView) rootView.findViewById(R.id.movie_detail_poster);
                if (title != null && titleView != null) {
                    titleView.append(title);
                }
                if (path != null && posterView != null) {
                    Picasso.with(getContext()).load(BuildConfig.TMDB_MOVIE_BASE_PATH + path).into(posterView);
                }
                if (date != null && dateView != null) {
                    dateView.append(date);
                }
                if (ratingView != null) {
                    ratingView.append(String.format(getString(R.string.detail_rating_format), rating));
                }
                if (synopsis != null && synopsisView != null) {
                    synopsisView.append(synopsis);
                }
            }
            return rootView;
        }
    }

}

package com.example.steveko.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListingFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private MovieAdapter mMovieAdapter;

    public ListingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListingFragment newInstance(String param1, String param2) {
        ListingFragment fragment = new ListingFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<MovieData>());
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_listing, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_listing);
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieData data = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra(getString(R.string.intent_title_key), data.title)
                        .putExtra(getString(R.string.intent_date_key), data.releaseDate)
                        .putExtra(getString(R.string.intent_path_key), data.posterPath)
                        .putExtra(getString(R.string.intent_rating_key), data.rating)
                        .putExtra(getString(R.string.intent_synopsis_key), data.synopsis);
                startActivity(intent);
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString()
             //       + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieListing();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void updateMovieListing() {

        FetchMoviesTask task = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString("", getActivity().getString(R.string.pref_sorting_popular_value));
        task.execute(sortOrder);

    }

    class FetchMoviesTask extends AsyncTask<String, Void, MovieData[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected MovieData[] doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonData = null;

            final String API_KEY_PARAMETER = "api_key";

            try {
                String sortOrder = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
                        getString(R.string.pref_sorting_key),
                        getString(R.string.pref_sorting_popular_value)
                );
                String basePath = BuildConfig.TMDB_LISTING_BASE_PATH + sortOrder;
                Uri builtUri = Uri.parse(basePath).buildUpon()
                        .appendQueryParameter(API_KEY_PARAMETER, BuildConfig.TMDB_API_KEY)
                        .build();

                String urlString = builtUri.toString();
                URL url = new URL(urlString);

                // Create the request to TMDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                jsonData = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // return nothing if there was an error
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }

                }
            }

            return parseJson(jsonData);
        }

        @Override
        protected void onPostExecute(MovieData[] movieData) {
            if (movieData != null) {
                mMovieAdapter.clear();
                mMovieAdapter.addAll(movieData);
            }
        }

        private MovieData[] parseJson(String rawJson) {
            final String RESULT_ATTRIBUTE = "results";
            final String TITLE_ATTRIBUTE = "title";
            final String RELEASE_DATE_ATTRIBUTE = "release_date";
            final String PATH_ATTRIBUTE = "poster_path";
            final String AVERAGE_ATTRIBUTE = "vote_average";
            final String OVERVIEW_ATTRIBUTE = "overview";
            try {
                JSONObject root = new JSONObject(rawJson);
                JSONArray movies = root.getJSONArray(RESULT_ATTRIBUTE);
                MovieData[] movieData = new MovieData[movies.length()];
                for (int i = 0; i < movies.length(); i++) {
                    movieData[i] = new MovieData(
                            movies.getJSONObject(i).getString(TITLE_ATTRIBUTE),
                            movies.getJSONObject(i).getString(RELEASE_DATE_ATTRIBUTE),
                            movies.getJSONObject(i).getString(PATH_ATTRIBUTE),
                            movies.getJSONObject(i).getDouble(AVERAGE_ATTRIBUTE),
                            movies.getJSONObject(i).getString(OVERVIEW_ATTRIBUTE));
                }
                return movieData;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Received bad JSON string: '" + rawJson + "'", e);
            }
            // if we reach here return an empty array since something went wrong
            return new MovieData[0];
        }
    }
}

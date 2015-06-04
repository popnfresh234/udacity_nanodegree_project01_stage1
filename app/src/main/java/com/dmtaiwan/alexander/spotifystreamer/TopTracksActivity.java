package com.dmtaiwan.alexander.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Alexander on 6/2/2015.
 */
public class TopTracksActivity extends ActionBarActivity {

    public static final String SPOTIFY_ID = "spotify_id";
    private static final String OUTSTATE_ARRAY = "outstate_arra";

    private ListView mListView;
    private TrackAdapter mAdapter;
    private ArrayList<Track> mTrackArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);
        mListView = (ListView) findViewById(R.id.list_view_tracks);

        if (savedInstanceState != null) {
            mTrackArray = (ArrayList<Track>) savedInstanceState.getSerializable(OUTSTATE_ARRAY);
            List<Track> trackList = mTrackArray;
            mAdapter = new TrackAdapter(getApplicationContext(), R.layout.list_item_tracks, trackList);
            mListView.setAdapter(mAdapter);
        } else {

            if (getIntent().getStringExtra(SPOTIFY_ID) != null) {
                String spotifyId = getIntent().getStringExtra(SPOTIFY_ID);

                SpotifyApi api = new SpotifyApi();
                final SpotifyService spotify = api.getService();
                HashMap map = new HashMap<String, String>();
                map.put("country", "us");
                spotify.getArtistTopTrack(spotifyId, map, new Callback<Tracks>() {
                    @Override
                    public void success(Tracks tracks, Response response) {
                        final List<Track> trackList = tracks.tracks;

                        if (trackList.size() > 0) {
                            mTrackArray = new ArrayList<Track>();
                            for (int i = 0; i < trackList.size(); i++) {
                                mTrackArray.add(trackList.get(i));
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter = new TrackAdapter(getApplicationContext(), R.layout.list_item_tracks, trackList);
                                    mListView.setAdapter(mAdapter);
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.toast_track_error), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                    }
                });
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(OUTSTATE_ARRAY, mTrackArray);
        super.onSaveInstanceState(outState);
    }

    public class AsyncSpotifyQuery extends AsyncTask<String, Void, Tracks> {
        @Override
        protected Tracks doInBackground(String... params) {

            SpotifyApi api = new SpotifyApi();
            final SpotifyService spotify = api.getService();
            HashMap map = new HashMap<String, String>();
            map.put("country", "us");
            Tracks results = spotify.getArtistTopTrack(params[0], map);
            return results;
        }

        @Override
        protected void onPostExecute(Tracks tracks) {
            super.onPostExecute(tracks);
            List<Track> trackList = tracks.tracks;

            if (trackList.size() > 0) {
                mTrackArray = new ArrayList<Track>();
                for (int i = 0; i < trackList.size(); i++) {
                    mTrackArray.add(trackList.get(i));
                }
                mAdapter = new TrackAdapter(getApplicationContext(), R.layout.list_item_tracks, trackList);
                mListView.setAdapter(mAdapter);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_track_error), Toast.LENGTH_LONG).show();
            }
        }
    }
}




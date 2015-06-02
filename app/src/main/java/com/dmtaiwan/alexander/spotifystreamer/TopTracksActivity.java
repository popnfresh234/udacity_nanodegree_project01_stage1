package com.dmtaiwan.alexander.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Alexander on 6/2/2015.
 */
public class TopTracksActivity extends ActionBarActivity {

    public static final String SPOTIFY_ID = "spotify_id";

    private ListView mListView;
    private TrackAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);
        mListView = (ListView) findViewById(R.id.list_view_tracks);

        if (getIntent().getStringExtra(SPOTIFY_ID) != null) {
            String spotifyId = getIntent().getStringExtra(SPOTIFY_ID);
            Log.i("ID", spotifyId);
            AsyncSpotifyQuery query = new AsyncSpotifyQuery();
            query.execute(spotifyId);
        }
    }

    public class AsyncSpotifyQuery extends AsyncTask<String, Void, Tracks> {
        @Override
        protected Tracks doInBackground(String... params) {

            Log.i("PARAMS", params[0]);
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
            if (trackList.size()>0) {
                mAdapter = new TrackAdapter(getApplicationContext(), R.layout.list_item_tracks, trackList);
                mListView.setAdapter(mAdapter);
            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.toast_track_error), Toast.LENGTH_LONG).show();
            }
        }
    }
}




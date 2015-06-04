package com.dmtaiwan.alexander.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {

    private EditText mArtistField;
    private ListView mListView;
    private ArtistsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list_view_artist);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = mAdapter.getItem(position);
                String spotifyId = artist.id;

                Intent i = new Intent(getApplicationContext(), TopTracksActivity.class);
                i.putExtra(TopTracksActivity.SPOTIFY_ID, spotifyId);
                startActivity(i);
            }
        });

        mArtistField = (EditText) findViewById(R.id.edit_text_artist);
        mArtistField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Utils.hideKeyboard(getApplicationContext(), getCurrentFocus());
                    String query = mArtistField.getText().toString();

                    SpotifyApi api = new SpotifyApi();
                    final SpotifyService spotify = api.getService();
                    spotify.searchArtists(query, new Callback<ArtistsPager>() {

                        @Override
                        public void success(ArtistsPager artistsPager, Response response) {
                            List<Artist> artists = artistsPager.artists.items;
                            if (artists.size() > 0) {
                                mAdapter = new ArtistsAdapter(getApplicationContext(), R.layout.list_item_artist, artists);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mListView.setAdapter(mAdapter);
                                    }
                                });

                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.toast_no_artist_error), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            error.printStackTrace();
                        }
                    });
                    return true;
                }
                return false;
            }
        });
    }
}

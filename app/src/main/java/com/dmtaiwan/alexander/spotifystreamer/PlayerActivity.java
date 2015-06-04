package com.dmtaiwan.alexander.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.client.Response;

/**
 * Created by Alexander on 6/4/2015.
 */
public class PlayerActivity extends ActionBarActivity{

    public static final String TRACK = "track";
    private TextView mArtist;
    private TextView mTrack;
    private ImageView mImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mArtist = (TextView) findViewById(R.id.text_view_player_artist);
        mTrack = (TextView) findViewById(R.id.text_view_player_track);
        mImage = (ImageView) findViewById(R.id.image_view_player_track);


        if (getIntent() != null) {


            String id = getIntent().getStringExtra(TRACK);
            SpotifyApi api = new SpotifyApi();
            final SpotifyService spotify = api.getService();
            spotify.getTrack(id, new SpotifyCallback<Track>() {
                @Override
                public void failure(SpotifyError spotifyError) {

                }

                @Override
                public void success(Track track, Response response) {

                }
            });

        }

    }
}

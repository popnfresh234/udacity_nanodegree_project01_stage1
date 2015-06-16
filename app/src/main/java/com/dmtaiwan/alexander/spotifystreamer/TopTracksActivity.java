package com.dmtaiwan.alexander.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * Created by Alexander on 6/2/2015.
 */
public class TopTracksActivity extends ActionBarActivity {

    private boolean mTabletLayout;
    private String mSpotifyId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setContentView(R.layout.activity_track_list);

        if (savedInstanceState == null) {

            mSpotifyId = getIntent().getStringExtra(Utils.SPOTIFY_ID);
            mTabletLayout = getIntent().getBooleanExtra(Utils.IS_TABLET_LAYOUT, false);
            Bundle arguments = new Bundle();
            arguments.putString(Utils.SPOTIFY_ID, mSpotifyId);
            arguments.putBoolean(Utils.IS_TABLET_LAYOUT, mTabletLayout);

            TopTracksFragment fragment = new TopTracksFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.container_track_list, fragment, "fragment").commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_player, menu);
        return super.onCreateOptionsMenu(menu);
    }
}



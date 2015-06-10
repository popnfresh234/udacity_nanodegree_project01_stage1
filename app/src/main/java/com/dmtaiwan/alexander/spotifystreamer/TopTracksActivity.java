package com.dmtaiwan.alexander.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
            getSupportFragmentManager().beginTransaction().add(R.id.container_track_list, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_launch_player:
                Intent intent = new Intent(this, PlayerActivity.class);
                intent.putExtra(Utils.TRACK_ID, mSpotifyId);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void launchPlayer(String trackId) {
        Intent playerActivityIntent = new Intent(this, PlayerActivity.class);
        playerActivityIntent.putExtra(Utils.TRACK_ID, trackId);
        startActivity(playerActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_player, menu);
        return super.onCreateOptionsMenu(menu);
    }
}



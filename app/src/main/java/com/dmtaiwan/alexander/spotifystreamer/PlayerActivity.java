package com.dmtaiwan.alexander.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by Alexander on 6/4/2015.
 */
public class PlayerActivity extends ActionBarActivity {

    private String mTrackId;
    private static final String TAG = "PlayerActivity";

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("OnNewIntent", "newIntent");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        if (getIntent() != null) {
            String trackId = getIntent().getStringExtra(Utils.TRACK_ID);
            mTrackId = trackId;
            PlayerDialogFragment fragment = new PlayerDialogFragment();
            Bundle arguments = new Bundle();
            arguments.putString(Utils.TRACK_ID, trackId);
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.container_player, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public String getTrackId() {
        return mTrackId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

}

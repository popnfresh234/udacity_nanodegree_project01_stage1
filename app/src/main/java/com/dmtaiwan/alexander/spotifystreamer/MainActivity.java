package com.dmtaiwan.alexander.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    private String mTrackId;
    private ArrayList<ParcelableTrack> mTrackArray;
    public Boolean mTabletLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_player, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_launch_player) {
            Bundle arguments = new Bundle();
            arguments.putString(Utils.TRACK_ID, mTrackId);
            PlayerDialogFragment fragment = new PlayerDialogFragment();
            fragment.setArguments(arguments);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prevFrag = getSupportFragmentManager().findFragmentByTag("fragment");
            if (prevFrag != null) {
                ft.remove(prevFrag);
            }
            if (!mTabletLayout) {
                ft.replace(R.id.container_main, fragment, "fragment").addToBackStack(null).commit();
            }else{
                fragment.show(ft, "fragment");
            }

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mTrackId = savedInstanceState.getString(Utils.TRACK_ID);
            mTrackArray = savedInstanceState.getParcelableArrayList(Utils.OUTSTATE_ARRAY);
        }

        //load main fragment into container
        if (getSupportFragmentManager().findFragmentByTag("mainfragment") == null) {
            Log.i("NEW MAIN FRAGMENT", "NEW MAIN FRAGMENT");
            MainFragment mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container_main, mainFragment, "mainfragment").commit();
        }


        //check if tablet layout
        if (findViewById(R.id.container_track_list) != null) {
            mTabletLayout = true;
        } else {
            mTabletLayout = false;
        }
    }

    public void onItemSelected(String id) {
        //Create TopTracks fragment and attach spotify ID
        Bundle arguments = new Bundle();
        arguments.putString(Utils.SPOTIFY_ID, id);
        arguments.putBoolean(Utils.IS_TABLET_LAYOUT, mTabletLayout);
        TopTracksFragment fragment = new TopTracksFragment();
        fragment.setArguments(arguments);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prevFrag = getSupportFragmentManager().findFragmentByTag("toptracks");
        if (prevFrag != null) {
            ft.remove(prevFrag);
        }
        if (!mTabletLayout) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_main, fragment, "toptracks").addToBackStack(null).commit();
        }
        if (mTabletLayout) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_track_list, fragment, "toptracks").commit();
        }
    }

    public void launchPlayerDialog(String spotifyId, ArrayList<ParcelableTrack> trackArray) {
        mTrackArray = trackArray;
        Log.i(TAG, mTrackArray.toString());
        PlayerDialogFragment fragment = new PlayerDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString(Utils.TRACK_ID, spotifyId);
        arguments.putBoolean(Utils.LAUNCHED_FROM_TRACK_LIST, true);
        fragment.setArguments(arguments);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prevFrag = getSupportFragmentManager().findFragmentByTag("player");
        if (prevFrag != null) {
            Log.i(TAG, "removing");
            ft.remove(prevFrag);
        }
        ft.addToBackStack(null);
        if (mTabletLayout) {
            fragment.show(ft, "player");
        } else {
            ft.replace(R.id.container_main, fragment, "fragment").addToBackStack(null).commit();
        }
    }

    public void setTrackId(String trackId) {
        mTrackId = trackId;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Utils.TRACK_ID, mTrackId);
        outState.putParcelableArrayList(Utils.OUTSTATE_ARRAY, mTrackArray);
    }

    public String getNextTrack() {
        int position = 0;
        for (int j = 0; j < mTrackArray.size(); j++) {
            ParcelableTrack track = mTrackArray.get(j);
            if (track.getTrackId().equals(mTrackId)) {
                position = j;
            }
        }
        ParcelableTrack nextTrack;
        Log.i(TAG, String.valueOf(position + 1));
        if (position + 1 <= mTrackArray.size()-1) {
            Log.i(TAG, String.valueOf(mTrackArray.size()));
            nextTrack = mTrackArray.get(position + 1);

        }else {
            nextTrack = mTrackArray.get(0);
        }
        mTrackId = nextTrack.getTrackId();
        return mTrackId;
    }

    public String getPreviousTrack() {
        int position = 0;
        for (int j = 0; j < mTrackArray.size(); j++) {
            ParcelableTrack track = mTrackArray.get(j);
            if (track.getTrackId().equals(mTrackId)) {
                position = j;
            }
        }
        ParcelableTrack nextTrack;
        Log.i(TAG, String.valueOf(position - 1));
        if (position - 1 >= 0) {
            Log.i(TAG, String.valueOf(mTrackArray.size()));
            nextTrack = mTrackArray.get(position - 1);

        }else {
            nextTrack = mTrackArray.get(mTrackArray.size()-1);
        }
        mTrackId = nextTrack.getTrackId();
        return mTrackId;
    }
}

package com.dmtaiwan.alexander.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    private String mTrackId;
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
            Fragment prevFrag = getSupportFragmentManager().findFragmentByTag("player");
            if (prevFrag != null) {
                ft.remove(prevFrag).commit();
            }
            if (!mTabletLayout) {
                ft.replace(R.id.container_main, fragment, "player").addToBackStack(null).commit();
            }else{
                fragment.show(ft, "player");
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load main fragment into container
        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container_main, mainFragment).commit();

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
            ft.remove(prevFrag).commit();
        }
        if (!mTabletLayout) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_main, fragment, "toptracks").addToBackStack(null).commit();
        }
        if (mTabletLayout) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_track_list, fragment, "toptracks").addToBackStack(null).commit();
        }
    }

    public void launchPlayerDialog(String spotifyId) {
        PlayerDialogFragment fragment = new PlayerDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString(Utils.TRACK_ID, spotifyId);
        fragment.setArguments(arguments);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prevFrag = getSupportFragmentManager().findFragmentByTag("player");
        if (prevFrag != null) {
            ft.remove(prevFrag);
        }
        ft.addToBackStack(null);
        if (mTabletLayout) {
            fragment.show(ft, "player");
        } else {
            ft.replace(R.id.container_main, fragment, "player").addToBackStack(null).commit();
        }
    }

    public void setTrackId(String trackId) {
        mTrackId = trackId;
    }

}

package com.dmtaiwan.alexander.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    private String mTrackId;
    private ArrayList<ParcelableTrack> mTrackArray;
    private int mTrackPosition;
    public Boolean mTabletLayout;
    private MenuItem mPlayerItem;
    private Boolean mIsShowingMenuItem = false;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_player, menu);
        mPlayerItem = menu.findItem(R.id.action_launch_player);
        if (mIsShowingMenuItem) {
            mPlayerItem.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_launch_player) {
            Bundle arguments = new Bundle();
            arguments.putString(Utils.TRACK_ID, mTrackId);
            arguments.putParcelableArrayList(Utils.OUTSTATE_ARRAY, mTrackArray);
            arguments.putInt(Utils.TRACK_POSITION, mTrackPosition);

            PlayerDialogFragment fragment = new PlayerDialogFragment();
            fragment.setArguments(arguments);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prevFrag = getSupportFragmentManager().findFragmentByTag("fragment");
            if (prevFrag != null) {
                ft.remove(prevFrag);
            }
            if (!mTabletLayout) {
                ft.replace(R.id.container_main, fragment, "fragment").addToBackStack(null).commit();
            } else {
                fragment.show(ft, "fragment");
            }

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mTrackId = savedInstanceState.getString(Utils.TRACK_ID);
            mTrackArray = savedInstanceState.getParcelableArrayList(Utils.OUTSTATE_ARRAY);
            mTrackPosition = savedInstanceState.getInt(Utils.TRACK_POSITION);
            mIsShowingMenuItem = savedInstanceState.getBoolean(Utils.IS_SHOWING_MENU_ITEM);

        }

        //load main fragment into container
        if (getSupportFragmentManager().findFragmentByTag("mainfragment") == null) {
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

    public void launchPlayerDialog(String spotifyId, ArrayList<ParcelableTrack> trackArray, int trackPosition) {
        mTrackArray = trackArray;
        mTrackPosition = trackPosition;
        PlayerDialogFragment fragment = new PlayerDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString(Utils.TRACK_ID, spotifyId);
        arguments.putBoolean(Utils.LAUNCHED_FROM_TRACK_LIST, true);
        arguments.putParcelableArrayList(Utils.OUTSTATE_ARRAY, mTrackArray);
        arguments.putInt(Utils.TRACK_POSITION, mTrackPosition);
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
            ft.replace(R.id.container_main, fragment, "fragment").addToBackStack(null).commit();
        }
    }

    public void setTrackId(String trackId) {
        mTrackId = trackId;
    }

    public void setTrackArray(ArrayList<ParcelableTrack> trackArray) {
        mTrackArray = trackArray;
    }

    public void setTrackPosition(int arrayPosition) {
        mTrackPosition = arrayPosition;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Utils.TRACK_ID, mTrackId);
        outState.putParcelableArrayList(Utils.OUTSTATE_ARRAY, mTrackArray);
        outState.putInt(Utils.TRACK_POSITION, mTrackPosition);
        outState.putBoolean(Utils.IS_SHOWING_MENU_ITEM, mIsShowingMenuItem);
    }

    public void showMenuItem() {
        if (mPlayerItem != null) {
            mPlayerItem.setVisible(true);
            mIsShowingMenuItem = true;
        }
    }

    public void hideMenuItem() {
        if (mPlayerItem != null) {
            mPlayerItem.setVisible(false);
            mIsShowingMenuItem = false;
        }
    }
}

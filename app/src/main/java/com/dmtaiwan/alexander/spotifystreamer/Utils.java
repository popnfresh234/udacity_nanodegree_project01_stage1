package com.dmtaiwan.alexander.spotifystreamer;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Alexander on 6/4/2015.
 */
public class Utils{

    public static final String OUTSTATE_ARRAY = "outstate_array";
    public static final String TRACK_ID = "track_id";
    public static final String SPOTIFY_ID = "spotify_id";
    public static final String IS_TABLET_LAYOUT = "is_tablet_layout";
    public static final String TRACK_URL = "track_url";
    public static final String LAUNCHED_FROM_TRACK_LIST = "launched_from_track_list";

    public static final String ACTION_START_FOREGROUND = "action_start_foreground";
    public static final String ACTION_MAIN = "action_main";
    public static final String ACTION_PREV = "action_prev";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_NEXT = "action_next";
    public static final int NOTIFICATION_ID = 2;


    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}

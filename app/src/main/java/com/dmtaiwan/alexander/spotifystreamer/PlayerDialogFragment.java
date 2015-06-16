package com.dmtaiwan.alexander.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Alexander on 6/15/2015.
 */
public class PlayerDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "PlayerDialog";

    private String mTrackId;
    private ArrayList<ParcelableTrack> mTrackArray;
    private int mArrayPosition;

    private String mArtistName;
    private String mTrackName;
    private String mAlbumName;
    private String mImageUrl;


    private TextView mArtistTextView;
    private TextView mTrackTextView;
    private TextView mAlbumTextView;
    private TextView mCurrentProgressTextView;
    private TextView mDurationTextView;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private ProgressBar mSeekBar;
    private ImageButton mPreviousButton;
    private ImageButton mPlayButton;
    private ImageButton mNextButton;

    //MusicService items
    private MusicService mMusicService;
    private Intent mServiceIntent;
    private boolean mBound = false;
    private Handler mHandler;
    private boolean mLoaded = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        setRetainInstance(true);
        //Hide the launch player icon
        ((MainActivity) getActivity()).hideMenuItem();

        if (savedInstanceState != null) {
            mTrackId = savedInstanceState.getString(Utils.TRACK_ID);
            mTrackArray = savedInstanceState.getParcelableArrayList(Utils.OUTSTATE_ARRAY);
            mArrayPosition = savedInstanceState.getInt(Utils.TRACK_POSITION);

            //Track names amd image url
            mArtistName = savedInstanceState.getString(Utils.ARTIST_NAME);
            mTrackName = savedInstanceState.getString(Utils.TRACK_NAME);
            mAlbumName = savedInstanceState.getString(Utils.ALBUM_NAME);
            mImageUrl = savedInstanceState.getString(Utils.IMAGE_URL);
            //start running handler
            mSeekBarUpdate.run();
        } else {
            mTrackId = getArguments().getString(Utils.TRACK_ID);
            mTrackArray = getArguments().getParcelableArrayList(Utils.OUTSTATE_ARRAY);
            mArrayPosition = getArguments().getInt(Utils.TRACK_POSITION);
        }


        mArtistTextView = (TextView) rootView.findViewById(R.id.text_view_player_artist);
        mTrackTextView = (TextView) rootView.findViewById(R.id.text_view_player_track);
        mAlbumTextView = (TextView) rootView.findViewById(R.id.text_view_player_album);
        mImageView = (ImageView) rootView.findViewById(R.id.image_view_player_track);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_player);
        mPreviousButton = (ImageButton) rootView.findViewById(R.id.button_player_previous);
        mPlayButton = (ImageButton) rootView.findViewById(R.id.button_player_play);
        mNextButton = (ImageButton) rootView.findViewById(R.id.button_player_next);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.seek_bar_player);
        mCurrentProgressTextView = (TextView) rootView.findViewById(R.id.text_view_player_current);
        mDurationTextView = (TextView) rootView.findViewById(R.id.text_view_player_duration);

        if (savedInstanceState != null) {
            setViews();
        }

        //set listeners
        mPlayButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mPreviousButton.setOnClickListener(this);

//        querySpotify(mTrackId);

        if (mHandler == null) {
            mHandler = new Handler();
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mServiceIntent == null) {
            mServiceIntent = new Intent(getActivity(), MusicService.class);

            //set extras
            mServiceIntent.putExtra(Utils.TRACK_ID, mTrackId);
            mServiceIntent.putParcelableArrayListExtra(Utils.OUTSTATE_ARRAY, mTrackArray);
            mServiceIntent.putExtra(Utils.TRACK_POSITION, mArrayPosition);

            //Bind and start service
            getActivity().startService(mServiceIntent);


            //start querying for position, duration, and track info

        }
        if (!mBound) {
            getActivity().bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
            mSeekBarUpdate.run();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //get latest ID and Position from service, communicate to MainActivity for relaunching player
        mTrackId = mMusicService.getCurrentTrackId();
        mArrayPosition = mMusicService.getArrayPosition();
        ((MainActivity) getActivity()).setTrackId(mTrackId);
        ((MainActivity) getActivity()).setTrackArray(mTrackArray);
        ((MainActivity) getActivity()).setTrackPosition(mArrayPosition);

        //Show relaunch player item
        if (mMusicService.isPlaying() || mMusicService.isPaused() || mMusicService.isPreparing()) {
            ((MainActivity) getActivity()).showMenuItem();
        }

        //unbind service
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
        //stop querying
        mHandler.removeCallbacks(mSeekBarUpdate);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //update TrackID
        mTrackId = mMusicService.getCurrentTrackId();
        outState.putString(Utils.TRACK_ID, mTrackId);
        outState.putParcelableArrayList(Utils.OUTSTATE_ARRAY, mTrackArray);
        outState.putInt(Utils.TRACK_POSITION, mArrayPosition);
        outState.putString(Utils.ARTIST_NAME, mArtistName);
        outState.putString(Utils.TRACK_NAME, mTrackName);
        outState.putString(Utils.ALBUM_NAME, mAlbumName);
        outState.putString(Utils.IMAGE_URL, mImageUrl);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_player_previous:
                //If player is paused, reset the play button
                if (mMusicService.isPaused()) {
                    mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                }
                mMusicService.previousTrack();
                //reset loaded boolean
                mLoaded = false;
                break;

            case R.id.button_player_play:
                if (mMusicService.isPlaying()) {
                    mMusicService.pause();
                    mPlayButton.setImageResource(android.R.drawable.ic_media_play);
                } else if (mMusicService.isPaused()) {
                    mMusicService.resume();
                    mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                }else if (mMusicService.isPreparing()) {
                    Toast.makeText(getActivity(), "Loading track, please wait", Toast.LENGTH_LONG).show();
                } else {
                    mMusicService.play();
                    mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                }

                break;

            case R.id.button_player_next:
                //If player is paused, reset the play button
                if (mMusicService.isPaused()) {
                    mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                }
                mMusicService.nextTrack();
                //reset loaded boolean
                mLoaded = false;
                //TODO next
                break;
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder localBinder = (MusicService.LocalBinder) service;
            //Get music service
            mMusicService = localBinder.getService();

            //stop and reset player if new track
            if (!mTrackId.equals(mMusicService.getCurrentTrackId())) {
                mMusicService.setTrackArray(mTrackArray);
                mMusicService.setTrackPosition(mArrayPosition);
                mMusicService.play();
                mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                mBound = true;
            } else {
                //First time starting track Pass trackArray and position
                mMusicService.setTrackArray(mTrackArray);
                mMusicService.setTrackPosition(mArrayPosition);
                if (mMusicService.isPlaying() || mMusicService.isPreparing()) {
                    mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                }
                mBound = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private Runnable mSeekBarUpdate = new Runnable() {
        @Override
        public void run() {

            if (mMusicService != null && mMusicService.isDataLoaded()) {
                //Fetch image, artist name, and track name
                if (!mLoaded) {
                    mArtistName = mMusicService.getArtistName();
                    mTrackName = mMusicService.getTrackName();
                    mAlbumName = mMusicService.getAlbumName();
                    mImageUrl = mMusicService.getImageUrl();
                    setViews();

                    mLoaded = true;
                }
            }

            if (mMusicService != null && mMusicService.isPreparing()) {
                mImageView.setAlpha(0.1F);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            if (mMusicService != null && !mMusicService.isPreparing()) {
                mImageView.setAlpha(1F);
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            if (mMusicService != null && mMusicService.isPreparing()) {
                mSeekBar.setMax(0);
                mSeekBar.setProgress(0);
                mCurrentProgressTextView.setText("0:00");
                mDurationTextView.setText("0:00");
            }

            if (mMusicService != null && mMusicService.isPrepared()) {

                //Get the duration
                int duration = mMusicService.getDuration();
                if (duration > 0) {
                    mSeekBar.setMax(duration);
                    String formatDuration = Utils.convert(duration);
                    mDurationTextView.setText(formatDuration);
                }

                //Get current progress
                if (mMusicService.getCurrentPosition() < 300000000 && duration > 0) {
                    int progress = mMusicService.getCurrentPosition();
                    mSeekBar.setProgress(progress);
                    String formatProgress = Utils.convert(progress);
                    mCurrentProgressTextView.setText(formatProgress);
                    mSeekBar.refreshDrawableState();
                }

            }
            mHandler.postDelayed(mSeekBarUpdate, 50);
        }
    };

    private void setViews() {
        mArtistTextView.setText(mArtistName);
        mTrackTextView.setText(mTrackName);
        mAlbumTextView.setText(mAlbumName);
        Picasso.with(getActivity()).load(mImageUrl).into(mImageView);
    }
}

package com.dmtaiwan.alexander.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Alexander on 6/7/2015.
 */
public class PlayerDialogFragment extends android.support.v4.app.DialogFragment {
    private static final String TAG = "Player";

    private String mTrackId;
    private String mImageUrl;
    private ProgressBar mSeekBar;
    private TextView mArtist;
    private TextView mTrack;
    private TextView mAlbum;
    private ImageView mImageView;
    private Button mPreviousButton;
    private Button mPlayButton;
    private Button mNextButton;

    private String mTrackUrl;

    //Service Connection for binding activity to service
    private MusicService mMusicService;
    private Handler mHandler;
    private boolean mBound = false;
    private boolean mStartedPlayback = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        mTrackId = getArguments().getString(Utils.TRACK_ID);
        ((MainActivity) getActivity()).setTrackId(mTrackId);


        mArtist = (TextView) rootView.findViewById(R.id.text_view_player_artist);
        mTrack = (TextView) rootView.findViewById(R.id.text_view_player_track);
        mAlbum = (TextView) rootView.findViewById(R.id.text_view_player_album);
        mImageView = (ImageView) rootView.findViewById(R.id.image_view_player_track);
        mPreviousButton = (Button) rootView.findViewById(R.id.button_player_previous);
        mPlayButton = (Button) rootView.findViewById(R.id.button_player_play);
        mNextButton = (Button) rootView.findViewById(R.id.button_player_next);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.seek_bar_player);
        mHandler = new Handler();

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mMusicService.isPlaying() && !mMusicService.isPaused()) {
                    if (mMusicService.getMediaPlayer() != null) {
                        mMusicService.resetPlayer();
                    }
                    mMusicService.initializePlayer();
                    mMusicService.prepareService(mTrackUrl);
                    mStartedPlayback = false;

                } else if (mMusicService.isPaused()) {
                    mMusicService.resumePlayback();
                } else if (mMusicService.isPlaying()) {
                    mMusicService.pausePlayer();
                }
            }
        });


        SpotifyApi api = new SpotifyApi();
        final SpotifyService spotify = api.getService();
        spotify.getTrack(mTrackId, new Callback<Track>() {
            @Override
            public void success(Track track, Response response) {
                mTrackUrl = track.preview_url;
                List<ArtistSimple> artists = track.artists;
                ArtistSimple artist = artists.get(0);
                final String name = artist.name;
                final String trackName = track.name;
                final AlbumSimple album = track.album;

                List<Image> albumImages = album.images;
                if (albumImages.size() > 0) {
                    for (int j = 0; j < albumImages.size(); j++) {
                        Image image = albumImages.get(j);
                        int height = image.height;
                        if (height >= 600) {
                            mImageUrl = image.url;
                        }
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mArtist.setText(name);
                        mTrack.setText(trackName);
                        mAlbum.setText(album.name);
                        Picasso.with(getActivity()).load(mImageUrl).into(mImageView);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();


        Intent playIntent = new Intent(getActivity(), MusicService.class);
        playIntent.putExtra(Utils.TRACK_ID, getActivity().getIntent().getStringExtra(Utils.TRACK_ID));
        playIntent.setAction(Utils.ACTION_PLAY);
        getActivity().startService(playIntent);


        //Bind to service
        if (!mBound) {
            getActivity().bindService(playIntent, mConnection, Context.BIND_AUTO_CREATE);
            mSeekBarUpdate.run();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
            mHandler.removeCallbacks(mSeekBarUpdate);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder localBinder = (MusicService.LocalBinder) service;
            mMusicService = localBinder.getService();
            mMusicService.getDuration();
            mBound = true;
            if (mMusicService.isPlaying()) {
                mStartedPlayback = false;
                mSeekBarUpdate.run();
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

            if (mMusicService != null) {
//                Log.i("laying?", String.valueOf(mMusicService.isPlaying()));


                int duration = mMusicService.getDuration();
//                Log.i(TAG, String.valueOf(duration));

                if (duration > 0) {
                    mSeekBar.setMax(duration);
                    mStartedPlayback = true;
                }


                    mSeekBar.setProgress(mMusicService.getPosition());
                    mSeekBar.refreshDrawableState();


            }
            mHandler.postDelayed(mSeekBarUpdate, 1000);
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Utils.TRACK_ID, mTrackId);
    }
}

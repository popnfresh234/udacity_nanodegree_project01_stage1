package com.dmtaiwan.alexander.spotifystreamer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
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
 * Created by Alexander on 6/15/2015.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener{
    private static final String TAG = "MusicService";

    private MediaPlayer mMediaPlayer;
    private ArrayList<ParcelableTrack> mTrackArray;
    private int mArrayPosition;
    private String mCurrentTrackId;
    private int mDuration = 0;
    private WifiManager.WifiLock mWifiLock;
    private Boolean mIsPrepared = false;
    private Boolean mIsPreparing = false;
    private Boolean mDataLoaded = false;
    private Boolean mIsPaused = false;

    private String mTrackUrl;

    //Strings to pass back to activity
    private String mImageUrl;
    private String mArtistName;
    private String mTrackName;
    private String mAlbumName;

    private final IBinder mBinder = new LocalBinder();

    //Return binder
    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnBind");
        return false;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        mMediaPlayer = new MediaPlayer();
        initPlayer();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG, "onPrepared");
        mIsPreparing = false;
        mIsPrepared = true;
        mIsPaused = false;
        mDuration = mp.getDuration();
        mp.start();
    }

    public void initPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "wifiLock");
        mWifiLock.acquire();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    }

    public void setTrackArray(ArrayList<ParcelableTrack> trackArray) {
        mTrackArray = trackArray;
    }

    public void setTrackPosition(int position) {
        mArrayPosition = position;
    }

    public void play() {
        //Reset the player to play next song
        mIsPrepared = false;
        mMediaPlayer.reset();
        initPlayer();
        //Get the selected song from track array
        querySpotifyForUrl(mArrayPosition);
    }

    public void pause() {
        if(mIsPrepared) {
            mMediaPlayer.pause();
            mIsPaused = true;
        }
    }

    public void resume() {
        if(mMediaPlayer!= null) {
            mMediaPlayer.start();
            mIsPaused = false;
        }
    }

    public void nextTrack() {
        mIsPrepared = false;
        mMediaPlayer.reset();
        Log.i(TAG, String.valueOf(mArrayPosition));
        int newPosition = mArrayPosition+1;

        //If track is last track, loop back to first track
        if (newPosition <= mTrackArray.size() - 1) {
            mArrayPosition = newPosition;
        }else {
            mArrayPosition = 0;
        }
        Log.i(TAG, String.valueOf(mArrayPosition));
        querySpotifyForUrl(mArrayPosition);
    }

    public void previousTrack() {
        mIsPrepared = false;
        mMediaPlayer.reset();
        Log.i(TAG, String.valueOf(mArrayPosition));
        int newPosition = mArrayPosition - 1;
        if (newPosition >= 0) {
            mArrayPosition = newPosition;
        }else {
            mArrayPosition = mTrackArray.size() - 1;
        }
        Log.i(TAG, String.valueOf(mArrayPosition));
        querySpotifyForUrl(mArrayPosition);
    }

    public String getArtistName() {
        return mArtistName;
    }

    public Boolean isPaused() {
        return mIsPaused;
    }

    public Boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }else{
            return false;
        }
    }

    public String getCurrentTrackId() {
        return mCurrentTrackId;
    }

    public int getArrayPosition() {
        return mArrayPosition;
    }

    public String getTrackName() {
        return mTrackName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public Boolean isPrepared() {
        return mIsPrepared;
    }

    public Boolean isPreparing() {
        return mIsPreparing;
    }

    public Boolean isDataLoaded() {
        return mDataLoaded;
    }

    public int getDuration() {
        return mDuration;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    private void querySpotifyForUrl(int arrayPosition) {
        mDataLoaded = false;
        ParcelableTrack track = mTrackArray.get(arrayPosition);
        mCurrentTrackId = track.getTrackId();
        SpotifyApi api = new SpotifyApi();
        final SpotifyService spotify = api.getService();
        spotify.getTrack(mCurrentTrackId, new Callback<Track>() {
            @Override
            public void success(Track track, Response response) {
                mTrackUrl = track.preview_url;

                //Load Track URL
                try {
                    mMediaPlayer.setDataSource(mTrackUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Prepare Service
                mIsPreparing = true;
                mMediaPlayer.prepareAsync();

                //Get the proper image size
                List<ArtistSimple> artists = track.artists;
                ArtistSimple artist = artists.get(0);
                final AlbumSimple album = track.album;

                mArtistName = artist.name;
                mTrackName = track.name;
                mAlbumName = album.name;
                mDataLoaded = true;

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
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}

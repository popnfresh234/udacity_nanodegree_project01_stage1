package com.dmtaiwan.alexander.spotifystreamer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Alexander on 6/7/2015.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {


    private static final String TAG = "MusicService";
    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    private Boolean isPrepared = false;
    private String mUrl;
    private boolean mIsPaused = false;
    private String mTrackId;
    private int mDuration = 0;
    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTrackId = intent.getStringExtra(Utils.TRACK_ID);

        if (intent.getAction().equals(Utils.ACTION_START_FOREGROUND)) {
            Log.i(TAG, "Received start foreground intent");

        } else if (intent.getAction().equals(Utils.ACTION_PREV)) {
            Log.i(TAG, "previous");

        } else if (intent.getAction().equals(Utils.ACTION_PLAY)) {

            Log.i(TAG, "play");
            //Intent for start


        } else if (intent.getAction().equals(Utils.ACTION_NEXT)) {
            Log.i(TAG, "next");
        }
        return START_STICKY;
    }



    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG, "prepared");
        mDuration = mp.getDuration();
        mp.start();
        mIsPaused = false;
        isPrepared = true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    public int getDuration() {
        return mDuration;
    }

    public int getPosition() {
        if (mediaPlayer != null) {
//            Log.i(TAG, String.valueOf(mediaPlayer.getCurrentPosition()));
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public boolean isPlaying() {
        if(mediaPlayer!=null) {
            return mediaPlayer.isPlaying();
        }else return false;
    }

    public void pausePlayer() {
        mediaPlayer.pause();
        mIsPaused = true;
    }

    public boolean isPaused() {
        if (mediaPlayer != null) {
            return mIsPaused;
        }else return false;
    }

    public void resumePlayback() {
        mediaPlayer.start();
        mIsPaused = false;
    }

    public String getTrackId() {
        return mTrackId;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public void initializePlayer() {
        //Initialize media player
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void resetPlayer() {
        mediaPlayer.reset();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void prepareService(String url) {

        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        notificationIntent.setAction(Utils.ACTION_MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //Notification
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Player")
                .setTicker("Player")
                .setContentText("Music")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(Utils.NOTIFICATION_ID, notification);

        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }
}
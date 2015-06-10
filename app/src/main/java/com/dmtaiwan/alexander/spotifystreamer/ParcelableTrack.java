package com.dmtaiwan.alexander.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alexander on 6/4/2015.
 */
public class ParcelableTrack implements Parcelable {


    private String trackName;
    private String albumName;
    private String imageUrl;
    private String trackId;

    public ParcelableTrack(String trackName, String albumName, String imageUrl, String trackId) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.imageUrl = imageUrl;
        this.trackId = trackId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public ParcelableTrack(Parcel in) {
        String[] data = new String[4];
        in.readStringArray(data);
        this.trackName = data[0];
        this.albumName = data[1];
        this.imageUrl = data[2];
        this.trackId = data[3];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.trackName, this.albumName, this.imageUrl, this.trackId});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ParcelableTrack createFromParcel(Parcel in) {
            return new ParcelableTrack(in);
        }

        public ParcelableTrack[] newArray(int size) {
            return new ParcelableTrack[size];
        }
    };
}

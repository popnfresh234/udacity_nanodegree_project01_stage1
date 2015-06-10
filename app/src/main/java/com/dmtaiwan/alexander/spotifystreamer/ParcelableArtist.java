package com.dmtaiwan.alexander.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alexander on 6/6/2015.
 */
public class ParcelableArtist implements Parcelable {

    private String artistName;
    private String imageUrl;
    private String artistId;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }



    public ParcelableArtist(String artistName, String imageUrl, String artistId) {
        this.artistName = artistName;
        this.imageUrl = imageUrl;
        this.artistId = artistId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.artistName, this.imageUrl, this.artistId});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ParcelableTrack createFromParcel(Parcel in) {
            return new ParcelableTrack(in);
        }

        public ParcelableArtist[] newArray(int size) {
            return new ParcelableArtist[size];
        }
    };
}

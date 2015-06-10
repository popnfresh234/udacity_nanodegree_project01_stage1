package com.dmtaiwan.alexander.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Alexander on 6/2/2015.
 */
public class TrackAdapter extends ArrayAdapter<ParcelableTrack> {

    private LayoutInflater mInflater;
    private int mLayoutId;
    private Context mContext;
    private ArrayList<ParcelableTrack> mTracks;
    private String mLargeImgUrl;

    public TrackAdapter(Context context, int resource, ArrayList<ParcelableTrack> tracks) {
        super(context, resource, tracks);
        mLayoutId = resource;
        mContext = context;
        mTracks = tracks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(mLayoutId, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view_album);
        TextView trackNameTextView = (TextView) convertView.findViewById(R.id.text_view_track_name);
        TextView albumNameTextView = (TextView) convertView.findViewById(R.id.text_view_album_name);

        ParcelableTrack track = mTracks.get(position);


        String trackName = track.getTrackName();
        String albumName = track.getAlbumName();
        String imageUrl = track.getImageUrl();
        trackNameTextView.setText(trackName);
        albumNameTextView.setText(albumName);


        Picasso.with(mContext).load(imageUrl).into(imageView);


        return convertView;
    }
}

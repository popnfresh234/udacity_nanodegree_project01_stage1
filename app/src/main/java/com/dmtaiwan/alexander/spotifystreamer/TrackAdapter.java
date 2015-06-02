package com.dmtaiwan.alexander.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmtaiwan.alexander.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Alexander on 6/2/2015.
 */
public class TrackAdapter extends ArrayAdapter<Track> {

    private LayoutInflater mInflater;
    private int mLayoutId;
    private Context mContext;
    private List<Track> mTracks;

    public TrackAdapter(Context context, int resource,  List<Track> tracks) {
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

        Track track = mTracks.get(position);
        AlbumSimple album = track.album;

        String trackName = track.name;
        String albumName = album.name;

        trackNameTextView.setText(trackName);
        albumNameTextView.setText(albumName);

        List<Image> albumImages = album.images;
        if (albumImages.size() > 0) {
            Image image = albumImages.get(0);
            String imageUrl = image.url;
            Picasso.with(mContext).load(imageUrl).into(imageView);
        }


        return convertView;
    }
}

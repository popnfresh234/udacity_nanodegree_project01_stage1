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
public class ArtistsAdapter extends ArrayAdapter<ParcelableArtist> {

    private ArrayList<ParcelableArtist> mArtists;
    private LayoutInflater mInflater;
    private Context mContext;
    private int mLayoutId;
    private String mSmallImgUrl;

    public ArtistsAdapter(Context context, int resource, ArrayList<ParcelableArtist> artists) {
        super(context, resource, artists);
        mArtists = artists;
        mContext = context;
        mLayoutId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(mLayoutId, parent, false);
        }

        TextView artistName = (TextView) convertView.findViewById(R.id.text_view_artist_name);
        ImageView artistImage = (ImageView) convertView.findViewById(R.id.image_view_artist);

        ParcelableArtist artist = mArtists.get(position);
        mSmallImgUrl = artist.getImageUrl();

                Picasso.with(mContext).load(mSmallImgUrl).into(artistImage);


        artistName.setText(artist.getArtistName());


        return convertView;
    }
}

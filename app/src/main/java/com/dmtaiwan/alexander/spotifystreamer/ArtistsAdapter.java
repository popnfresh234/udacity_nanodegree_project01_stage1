package com.dmtaiwan.alexander.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Alexander on 6/2/2015.
 */
public class ArtistsAdapter extends ArrayAdapter<Artist> {

    private List<Artist> mArtists;
    private LayoutInflater mInflater;
    private Context mContext;
    private int mLayoutId;

    public ArtistsAdapter(Context context, int resource, List<Artist> artists) {
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

        Artist artist = mArtists.get(position);

        //get image and load into view
        List<Image> images = artist.images;
        if (images.size() > 0) {
            Image image = images.get(0);
            if (image != null) {
                String imageUrl = image.url;
                Picasso.with(mContext).load(imageUrl).into(artistImage);
            }
        }

        artistName.setText(artist.name);


        return convertView;
    }
}

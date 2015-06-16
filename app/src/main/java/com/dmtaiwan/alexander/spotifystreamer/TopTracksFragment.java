package com.dmtaiwan.alexander.spotifystreamer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Alexander on 6/6/2015.
 */
public class TopTracksFragment extends android.support.v4.app.Fragment {


    private ListView mListView;
    private TrackAdapter mAdapter;
    private ArrayList<ParcelableTrack> mTrackArray;
    private Boolean mTabletLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_list, container, false);
        setRetainInstance(true);
        mTabletLayout = getArguments().getBoolean(Utils.IS_TABLET_LAYOUT);
        mTrackArray = new ArrayList<ParcelableTrack>();
        mListView = (ListView) rootView.findViewById(R.id.list_view_tracks);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParcelableTrack track = mAdapter.getItem(position);
                String trackId = track.getTrackId();
                ((MainActivity) getActivity()).launchPlayerDialog(trackId, mTrackArray, position);
            }
        });

        if (savedInstanceState != null) {
            mTrackArray = savedInstanceState.getParcelableArrayList(Utils.OUTSTATE_ARRAY);
            mAdapter = new TrackAdapter(getActivity(), R.layout.list_item_tracks, mTrackArray);
            mListView.setAdapter(mAdapter);

        } else {
            if (getArguments().getString(Utils.SPOTIFY_ID) != null) {
                String spotifyId = getArguments().getString(Utils.SPOTIFY_ID);
                SpotifyApi api = new SpotifyApi();
                final SpotifyService spotify = api.getService();
                HashMap map = new HashMap<String, String>();
                map.put("country", "us");
                spotify.getArtistTopTrack(spotifyId, map, new Callback<Tracks>() {
                    @Override
                    public void success(Tracks tracks, Response response) {
                        final List<Track> trackList = tracks.tracks;

                        if (trackList.size() > 0) {
                            for (int i = 0; i < trackList.size(); i++) {
                                Track track = trackList.get(i);
                                AlbumSimple album = track.album;

                                String trackName = track.name;
                                String albumName = album.name;
                                String imageUrl = null;
                                String trackId = track.id;
                                List<Image> albumImages = album.images;
                                if (albumImages.size() > 0) {
                                    for (int j = 0; j < albumImages.size(); j++) {
                                        Image image = albumImages.get(j);
                                        int height = image.height;
                                        if (height <= 200 && height <= 220) {
                                            imageUrl = image.url;
                                        }
                                    }
                                }
                                ParcelableTrack parcelableTrack = new ParcelableTrack(trackName, albumName, imageUrl, trackId);
                                mTrackArray.add(parcelableTrack);
                            }
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter = new TrackAdapter(getActivity(), R.layout.list_item_tracks, mTrackArray);
                                        mListView.setAdapter(mAdapter);
                                    }
                                });
                            }
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), getString(R.string.toast_track_error), Toast.LENGTH_LONG).show();
                                    }
                                });
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
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Utils.OUTSTATE_ARRAY, mTrackArray);
        super.onSaveInstanceState(outState);
    }
}

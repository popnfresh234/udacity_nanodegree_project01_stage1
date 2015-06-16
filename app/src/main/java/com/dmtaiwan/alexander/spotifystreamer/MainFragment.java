package com.dmtaiwan.alexander.spotifystreamer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Alexander on 6/6/2015.
 */
public class MainFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "MainFragment";
    private static final String OUTSTATE_ARTIST_ARRAY = "outstate_artist_array";
    private EditText mArtistField;
    private ListView mListView;
    private ArrayList<ParcelableArtist> mArtistArray;
    private ArtistsAdapter mAdapter;

    @Override
    public void onResume() {
        super.onResume();
        mListView.setAdapter(mAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setRetainInstance(true);
        mArtistField = (EditText) rootView.findViewById(R.id.edit_text_artist);
        mListView = (ListView) rootView.findViewById(R.id.list_view_artist);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParcelableArtist artist = mAdapter.getItem(position);
                String spotifyId = artist.getArtistId();
                ((MainActivity) getActivity()).onItemSelected(spotifyId);
            }
        });

        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(OUTSTATE_ARTIST_ARRAY).size()>0) {
            mArtistArray = savedInstanceState.getParcelableArrayList(OUTSTATE_ARTIST_ARRAY);
            mAdapter = new ArtistsAdapter(getActivity(), R.layout.list_item_artist, mArtistArray);
            mAdapter.notifyDataSetChanged();
            //hide soft keyboard on rotate
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } else {
            mArtistArray = new ArrayList<ParcelableArtist>();
            mArtistField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        mArtistArray.clear();
                        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());
                        String query = mArtistField.getText().toString();

                        SpotifyApi api = new SpotifyApi();
                        final SpotifyService spotify = api.getService();
                        spotify.searchArtists(query, new Callback<ArtistsPager>() {

                            @Override
                            public void success(ArtistsPager artistsPager, Response response) {
                                List<Artist> artists = artistsPager.artists.items;
                                if (artists.size() > 0) {
                                    for (int i = 0; i < artists.size(); i++) {
                                        Artist artist = artists.get(i);
                                        String artistName = artist.name;
                                        String imageUrl = null;
                                        String artistId = artist.id;
                                        List<Image> images = artist.images;
                                        if (images.size() > 0) {
                                            for (int j = 0; j < images.size(); j++) {
                                                Image image = images.get(j);
                                                int height = image.height;
                                                if (height <= 200 && height <= 220) {
                                                    imageUrl = image.url;
                                                }
                                            }
                                        }
                                        ParcelableArtist
                                                parcelableArtist = new ParcelableArtist(artistName, imageUrl, artistId);
                                        mArtistArray.add(parcelableArtist);
                                    }

                                    if (mAdapter == null) {
                                        mAdapter = new ArtistsAdapter(getActivity(), R.layout.list_item_artist, mArtistArray);
                                        
                                    }else {
                                        //If there's already a top tracks fragment shown, remove it
                                        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("toptracks");
                                        if(fragment!= null) {
                                            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                                        }

                                        //update adapter with new data
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        });

                                    }

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mListView.setAdapter(mAdapter);
                                        }
                                    });

                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), getString(R.string.toast_no_artist_error), Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                error.printStackTrace();
                            }
                        });
                        return true;
                    }
                    return false;
                }
            });
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(OUTSTATE_ARTIST_ARRAY, mArtistArray);
        super.onSaveInstanceState(outState);
    }
}

package com.example.discoteca.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.discoteca.interactions.EndlessScrolling;
import com.example.discoteca.R;
import com.example.discoteca.spotify.Spotify;
import com.example.discoteca.adapters.AlbumAdapter;
import com.example.discoteca.adapters.SongAdapter;
import com.example.discoteca.models.Album;
import com.example.discoteca.models.Song;
import com.google.android.material.tabs.TabLayout;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchFragment extends Fragment implements AlbumAdapter.OnAlbumClickListener, SongAdapter.OnSongClickListener{

    private static final String TAG = "SearchFragment";
    FrameLayout flChild;
    RecyclerView rvSearch;
    SongAdapter songAdapter;
    AlbumAdapter albumAdapter;
    ProgressBar searchProgress;
    List<Song> songs;
    List<Album> albums;
    SearchView searchBar;
    TabLayout tabLayout;
    Spotify client;
    private EndlessScrolling scrollListener;
    String queryS;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Progress bar
        searchProgress = view.findViewById(R.id.searchProgress);

        // Frame layout is initialized
        flChild = view.findViewById(R.id.flChild);
        flChild.setVisibility(View.GONE);

        // Tab layout functionality
        tabLayout = view.findViewById(R.id.tabLayout);

        searchBar = view.findViewById(R.id.searchBar);

        // Recycler view is defined
        rvSearch = view.findViewById(R.id.rvResults);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        scrollListener = new EndlessScrolling (linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                page = page * 20;
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvSearch.addOnScrollListener(scrollListener);

        // Arrays are initialized
        songs = new ArrayList<>();
        albums = new ArrayList<>();

        // Adapter is created for each possible result
        songAdapter = new SongAdapter(getContext(), songs, this);
        albumAdapter = new AlbumAdapter(getContext(), albums, this);

        // Adapter and layout manager is defined
        rvSearch.setLayoutManager(linearLayoutManager);

        // Getting the access token from shared preferences
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.share_preferences_file), Context.MODE_PRIVATE);
        client = new Spotify(context, sharedPref);

        //The default tab is the song tab
        songTab();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0){
                    songAdapter.clearAll(true);
                    searchBar.setQuery("",false);
                    songTab();
                }
                if (tabLayout.getSelectedTabPosition() == 1){
                    albumAdapter.clearAll(true);
                    searchBar.setQuery("",false);
                    albumTab();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    // Infinite scroll, loads more data into the recycler view
    private void loadNextDataFromApi(int page) {
        if (tabLayout.getSelectedTabPosition() == 0){
            client.makeSearchSongRequest(queryS, page).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Failed to fetch data: " + e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    List<Song> results = new ArrayList<>();
                    results.addAll(client.createSongs(response, results));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            songAdapter.addAll(results);
                        }
                    });
                }
            });
        }
        if (tabLayout.getSelectedTabPosition() == 1){
            client.makeSearchAlbumRequest(queryS, page).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Failed to fetch data: " + e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    List<Album> results = new ArrayList<>();
                    results.addAll(client.createAlbum(response,results));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            albumAdapter.addAll(results);
                        }
                    });
                }
            });
        }
    }

    private void songTab(){
        rvSearch.setAdapter(songAdapter);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryS = query;
                searchProgress.setVisibility(View.VISIBLE);
                songAdapter.clearAll(true);
                client.makeSearchSongRequest(query, 0).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Failed to fetch data: " + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        List<Song> results = new ArrayList<>();
                        results.addAll(client.createSongs(response, results));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                songAdapter.clearAll(false);
                                songAdapter.addAll(results);
                                searchProgress.setVisibility(View.GONE);
                            }
                        });
                    }
                });;;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

    }

    private void albumTab(){
        rvSearch.setAdapter(albumAdapter);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProgress.setVisibility(View.VISIBLE);
                albumAdapter.clearAll(true);
                client.makeSearchAlbumRequest(query, 0).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Failed to fetch data: " + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        List<Album> results = new ArrayList<>();
                        results.addAll(client.createAlbum(response,results));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                albumAdapter.clearAll(false);
                                albumAdapter.addAll(results);
                                searchProgress.setVisibility(View.GONE);
                            }
                        });
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

    }


    @Override
    public void onAlbumClick(int position) {
        Fragment fragment = new AlbumFragment();

        // Pass song data to the detail fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("album", Parcels.wrap(albums.get(position)));
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
        transaction.replace(R.id.flChild, fragment).commit();
        flChild.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSongClick(int position) {
        Fragment fragment = new SongFragment();

        // Pass song data to the detail fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("song", Parcels.wrap(songs.get(position)));
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
        transaction.replace(R.id.flChild, fragment).commit();
        flChild.setVisibility(View.VISIBLE);
    }
}
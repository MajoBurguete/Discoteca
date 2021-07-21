package com.example.discoteca.fragments;

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
import android.widget.Toast;

import com.example.discoteca.R;
import com.example.discoteca.Spotify;
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
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class SearchFragment extends Fragment implements AlbumAdapter.OnAlbumClickListener, SongAdapter.OnSongClickListener{

    private static final String TAG = "SearchFragment";
    FrameLayout flChild;
    RecyclerView rvSearch;
    SongAdapter songAdapter;
    AlbumAdapter albumAdapter;
    List<Song> songs;
    List<Album> albums;
    SearchView searchBar;
    private String accessToken;
    TabLayout tabLayout;
    Spotify client;

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

        // Frame layout is initialized
        flChild = view.findViewById(R.id.flChild);
        flChild.setVisibility(View.GONE);

        // Tab layout functionality
        tabLayout = view.findViewById(R.id.tabLayout);

        searchBar = view.findViewById(R.id.searchBar);

        // Recycler view is defined
        rvSearch = view.findViewById(R.id.rvResults);

        // Arrays are initialized
        songs = new ArrayList<>();
        albums = new ArrayList<>();

        // Adapter is created for each possible result
        songAdapter = new SongAdapter(getContext(), songs, this);
        albumAdapter = new AlbumAdapter(getContext(), albums, this);

        // Adapter and layout manager is defined
        rvSearch.setLayoutManager(new LinearLayoutManager(getContext()));

        // Getting the access token from main activity
        accessToken = this.getArguments().getString("token");
        client = new Spotify(accessToken);

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

    private void songTab(){
        rvSearch.setAdapter(songAdapter);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                client.makeSearchSongRequest(query).enqueue(new Callback() {
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
                client.makeSearchAlbumRequest(query).enqueue(new Callback() {
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
        Toast.makeText(getContext(), "ALbum clickedd", Toast.LENGTH_SHORT).show();
        Fragment fragment = new AlbumFragment();

        // Pass song data to the detail fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("album", Parcels.wrap(albums.get(position)));
        bundle.putString("token", accessToken);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.flChild, fragment).commit();
        flChild.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSongClick(int position) {
        Toast.makeText(getContext(), "Song clickedd", Toast.LENGTH_SHORT).show();
        Fragment fragment = new SongFragment();

        // Pass song data to the detail fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("song", Parcels.wrap(songs.get(position)));
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.flChild, fragment).commit();
        flChild.setVisibility(View.VISIBLE);
    }
}
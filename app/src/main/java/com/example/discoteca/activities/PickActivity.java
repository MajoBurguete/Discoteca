package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.discoteca.interactions.EndlessScrolling;
import com.example.discoteca.R;
import com.example.discoteca.spotify.Spotify;
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

public class PickActivity extends AppCompatActivity implements SongAdapter.OnSongClickListener{

    public static final String TAG = "PickActivity";
    SearchView searchBar;
    ImageButton ibBack;
    ProgressBar progressBar;
    RecyclerView rvPickSongs;
    List<Song> songs;
    SongAdapter adapter;
    TabLayout tabLayout;
    Spotify client;
    private EndlessScrolling scrollListener;
    String queryS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        // Get access token from shared preferences
        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.share_preferences_file), Context.MODE_PRIVATE);
        client = new Spotify(context, sharedPref);

        // Components references from the pick activity layout
        searchBar = findViewById(R.id.svPickSong);
        rvPickSongs = findViewById(R.id.rvPickSongs);
        tabLayout = findViewById(R.id.tabPick);
        ibBack = findViewById(R.id.ibBack);
        progressBar = findViewById(R.id.loadSongs);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

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
        rvPickSongs.addOnScrollListener(scrollListener);

        // Initialize list of objects
        songs = new ArrayList<>();

        // Initialize song adapter
        adapter = new SongAdapter(PickActivity.this, songs, this);

        // Song adapter and layout manager
        rvPickSongs.setAdapter(adapter);
        rvPickSongs.setLayoutManager(linearLayoutManager);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Default tab
        songTab();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0){
                    adapter.clearAll(true);
                    searchBar.setQuery("",false);
                    songTab();
                }
                if (tabLayout.getSelectedTabPosition() == 1){
                    adapter.clearAll(true);
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
                    PickActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addAll(results);
                        }
                    });
                }
            });
        }
        if (tabLayout.getSelectedTabPosition() == 1){
            List<Album> results = new ArrayList<>();
            List<Song> albumSongs = new ArrayList<>();
            client.makeSearchAlbumRequest(queryS, page).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Failed to fetch data: " + e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    albumSongs.addAll(client.createAlbumForSongs(response,results));
                    PickActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addAll(albumSongs);
                        }
                    });
                }
            });
        }
    }

    private void songTab() {
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryS = query;
                adapter.clearAll(true);
                progressBar.setVisibility(View.VISIBLE);
                // Request call
                client.makeSearchSongRequest(query,0).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Failed to fetch data: " + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        List<Song> results = new ArrayList<>();
                        results.addAll(client.createSongs(response, results));
                        PickActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.clearAll(false);
                                adapter.addAll(results);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                });;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void albumTab() {
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryS = query;
                adapter.clearAll(true);
                progressBar.setVisibility(View.VISIBLE);
                // Request call
                List<Album> results = new ArrayList<>();
                List<Song> albumSongs = new ArrayList<>();
                client.makeSearchAlbumRequest(query,0).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Failed to fetch data: " + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        albumSongs.addAll(client.createAlbumForSongs(response,results));
                        PickActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.clearAll(false);
                                adapter.addAll(albumSongs);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onSongClick(int position) {
        Intent result = new Intent();
        result.putExtra("song", Parcels.wrap(songs.get(position)));
        setResult(RESULT_OK, result);
        finish();
    }
}
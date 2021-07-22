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

import com.example.discoteca.R;
import com.example.discoteca.Spotify;
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
    private String accessToken;
    SearchView searchBar;
    ImageButton ibBack;
    RecyclerView rvPickSongs;
    List<Song> songs;
    SongAdapter adapter;
    TabLayout tabLayout;
    Spotify client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        // Get access token from shared preferences
        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.share_preferences_file), Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("token",null);
        client = new Spotify(accessToken);

        // Components references from the pick activity layout
        searchBar = findViewById(R.id.svPickSong);
        rvPickSongs = findViewById(R.id.rvPickSongs);
        tabLayout = findViewById(R.id.tabPick);
        ibBack = findViewById(R.id.ibBack);

        // Initialize list of objects
        songs = new ArrayList<>();

        // Initialize song adapter
        adapter = new SongAdapter(PickActivity.this, songs, this);

        // Song adapter and layout manager
        rvPickSongs.setAdapter(adapter);
        rvPickSongs.setLayoutManager(new LinearLayoutManager(this));

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

    private void songTab() {
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Request call
                client.makeSearchSongRequest(query).enqueue(new Callback() {
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
                // Request call
                List<Album> results = new ArrayList<>();
                List<Song> albumSongs = new ArrayList<>();
                client.makeSearchAlbumRequest(query).enqueue(new Callback() {
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
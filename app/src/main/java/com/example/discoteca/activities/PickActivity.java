package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.example.discoteca.R;
import com.example.discoteca.adapters.SongAdapter;
import com.example.discoteca.models.Song;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

public class PickActivity extends AppCompatActivity implements SongAdapter.OnSongClickListener{

    public static final String TAG = "PickActivity";
    private String accessToken;
    SearchView searchBar;
    RecyclerView rvPickSongs;
    List<Song> songs;
    SongAdapter adapter;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        accessToken = getIntent().getStringExtra("token");

        // Components references from the pick activity layout
        searchBar = findViewById(R.id.svPickSong);
        rvPickSongs = findViewById(R.id.rvPickSongs);
        tabLayout = findViewById(R.id.tabPick);

        // Initialize list of objects
        songs = new ArrayList<>();

        // Initialize song adapter
        adapter = new SongAdapter(PickActivity.this, songs, this);

        // Song adapter and layout manager
        rvPickSongs.setAdapter(adapter);
        rvPickSongs.setLayoutManager(new LinearLayoutManager(this));

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

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Request call
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }

    private void songTab() {
    }

    private void albumTab() {
    }

    @Override
    public void onSongClick(int position) {

    }
}
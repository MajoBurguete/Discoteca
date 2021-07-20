package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;

import com.example.discoteca.R;
import com.example.discoteca.adapters.SongAdapter;
import com.example.discoteca.models.Song;

import java.util.ArrayList;
import java.util.List;

public class PickActivity extends AppCompatActivity implements SongAdapter.OnSongClickListener{

    private String accessToken;
    SearchView searchBar;
    RecyclerView rvPickSongs;
    List<Song> songs;
    SongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        accessToken = getIntent().getStringExtra("token");

        // Components references from the pick activity layout
        searchBar = findViewById(R.id.svPickSong);
        rvPickSongs = findViewById(R.id.rvPickSongs);

        // Initialize list of objects
        songs = new ArrayList<>();

        // Initialize song adapter
        adapter = new SongAdapter(PickActivity.this, songs, this);

        // Song adapter and layout manager
        rvPickSongs.setAdapter(adapter);
        rvPickSongs.setLayoutManager(new LinearLayoutManager(this));

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

    @Override
    public void onSongClick(int position) {

    }
}
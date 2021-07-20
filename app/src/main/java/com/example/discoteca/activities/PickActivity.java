package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;

import com.example.discoteca.R;
import com.example.discoteca.adapters.SongAdapter;
import com.example.discoteca.models.Song;

import java.util.List;

public class PickActivity extends AppCompatActivity {

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
    }
}
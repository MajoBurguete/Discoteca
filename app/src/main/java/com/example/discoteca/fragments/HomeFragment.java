package com.example.discoteca.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.discoteca.R;
import com.example.discoteca.Spotify;
import com.example.discoteca.adapters.FactAdapter;
import com.example.discoteca.models.Fact;
import com.example.discoteca.models.Song;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    RecyclerView rvFacts;
    List<Fact> factsL;
    FactAdapter adapter;
    Spotify client;
    List<Song> songList;
    private String accessToken;
    List<Fact> factsNew = new ArrayList<>();
    List<Song> songNew = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Access Token and create spotify object
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.share_preferences_file), Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("token",null);
        client = new Spotify(accessToken);

        // Get references
        rvFacts = view.findViewById(R.id.rvFacts);

        // Initialize fact list
        factsL = new ArrayList<>();
        songList = new ArrayList<>();

        // Initialize fact adapter
        adapter = new FactAdapter(getContext(), factsL, songList);

        // Assign adapter nd layout manager to the recycler view
        rvFacts.setAdapter(adapter);
        rvFacts.setLayoutManager(new LinearLayoutManager(getContext()));


    }
}
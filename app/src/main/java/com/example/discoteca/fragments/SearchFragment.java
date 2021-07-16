package com.example.discoteca.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.discoteca.R;
import com.example.discoteca.adapters.SongAdapter;
import com.example.discoteca.models.Album;
import com.example.discoteca.models.Song;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    RecyclerView rvSearch;
    SongAdapter songAdapter;
    List<Song> songs;
    List<Album> albums;
    SearchView searchBar;
    private String accessToken;
    TabLayout tabLayout;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();

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

        // Tab layout functionality
        tabLayout = view.findViewById(R.id.tabLayout);

        searchBar = view.findViewById(R.id.searchBar);

        // Recycler view is defined
        rvSearch = view.findViewById(R.id.rvResults);

        // Arrays are initialized
        songs = new ArrayList<>();

        // Adapter is created for each possible result
        songAdapter = new SongAdapter(getContext(), songs);

        // Adapter and layout manager is defined
        rvSearch.setAdapter(songAdapter);
        rvSearch.setLayoutManager(new LinearLayoutManager(getContext()));

        // Getting the access token from main activity
        accessToken = this.getArguments().getString("token");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0){

                    String query = String.valueOf(searchBar.getQuery());

                    Toast.makeText(getContext(), "Tab " + query, Toast.LENGTH_LONG).show();
                }
                if (tabLayout.getSelectedTabPosition() == 1){
                    Toast.makeText(getContext(), "Tab 2 " + tabLayout.getSelectedTabPosition(), Toast.LENGTH_LONG).show();
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

}
package com.example.discoteca.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.discoteca.R;
import com.example.discoteca.models.Album;
import com.example.discoteca.models.Song;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class SearchFragment extends Fragment {

    RecyclerView rvSearch;
    List<Song> songs;
    List<Album> albums;

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
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0){
                    Toast.makeText(getContext(), "Tab " + tabLayout.getSelectedTabPosition(), Toast.LENGTH_LONG).show();
                }
                if (tabLayout.getSelectedTabPosition() == 1){
                    Toast.makeText(getContext(), "Tab 2" + tabLayout.getSelectedTabPosition(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Recycler view is defined
        rvSearch = view.findViewById(R.id.rvResults);

        // Adapter is defined


    }

}
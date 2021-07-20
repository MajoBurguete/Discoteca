package com.example.discoteca.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.discoteca.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SongFragment extends Fragment {

    public static final String TAG = "SongFragment";
    ImageView ivSongDetail;
    TextView tvNameDetail;
    TextView tvArtistSong;
    RecyclerView rvSongsFacts;
    FloatingActionButton fabAdd;
    RelativeLayout rlSong;

    public SongFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Views references
        ivSongDetail = view.findViewById(R.id.ivSongDetail);
        tvNameDetail = view.findViewById(R.id.tvNameDetail);
        tvArtistSong  = view.findViewById(R.id.tvArtistSong);
        rvSongsFacts = view.findViewById(R.id.rvSongFacts);
        fabAdd = view.findViewById(R.id.fabAdd);
        rlSong = view.findViewById(R.id.rlSong);

    }
}
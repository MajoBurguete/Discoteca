package com.example.discoteca.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.discoteca.R;
import com.example.discoteca.activities.ComposeActivity;
import com.example.discoteca.adapters.FactAdapter;
import com.example.discoteca.models.Fact;
import com.example.discoteca.models.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class SongFragment extends Fragment {

    public static final String TAG = "SongFragment";
    ImageView ivSongDetail;
    TextView tvNameDetail;
    TextView tvArtistSong;
    TextView tvSongAlbum;
    ImageButton ibClose;
    RecyclerView rvSongsFacts;
    FactAdapter adapter;
    List<Fact> songFacts;
    FloatingActionButton fabAdd;
    RelativeLayout rlSong;
    Song song;

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
        tvSongAlbum = view.findViewById(R.id.tvSongAlbum);
        ibClose = view.findViewById(R.id.ibCloseSong);
        rvSongsFacts = view.findViewById(R.id.rvSongFacts);
        fabAdd = view.findViewById(R.id.fabAdd);
        rlSong = view.findViewById(R.id.rlSong);

        song = Parcels.unwrap(this.getArguments().getParcelable("song"));

        // Initialize fact array
        songFacts = new ArrayList<>();

        // Initialize adapter
        adapter = new FactAdapter(getContext(), songFacts);

        // Assign adapter and layout manager
        rvSongsFacts.setAdapter(adapter);
        rvSongsFacts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Close button
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlSong.setClickable(false);
                getParentFragment().getChildFragmentManager().beginTransaction().remove(SongFragment.this).commit();
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fact = new Intent(getActivity(), ComposeActivity.class);
                fact.putExtra("song", Parcels.wrap(song));
                startActivity(fact);
            }
        });

        // Bind song data into the layout
        Glide.with(getContext()).load(song.getImageUrl()).transform(new RoundedCorners(10)).into(ivSongDetail);
        tvNameDetail.setText(song.getSongName());
        tvArtistSong.setText(song.getArtistName());
        tvSongAlbum.setText(song.getAlbumName());
        
    }
}
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
import android.widget.TextView;

import com.example.discoteca.R;

public class AlbumFragment extends Fragment {

    ImageView ivAlbumDet;
    TextView tvAlbumNameD;
    TextView tvArtistAlbum;
    TextView tvAlbumYear;
    RecyclerView rvAlbumSongs;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivAlbumDet = view.findViewById(R.id.ivAlbumDet);
        tvAlbumNameD = view.findViewById(R.id.tvAlbumNameD);
        tvArtistAlbum = view.findViewById(R.id.tvArtistAlbum);
        tvAlbumYear = view.findViewById(R.id.tvAlbumYear);
        rvAlbumSongs = view.findViewById(R.id.rvAlbumSongs);

    }
}
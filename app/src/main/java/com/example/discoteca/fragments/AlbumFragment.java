package com.example.discoteca.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.discoteca.Spotify;
import com.example.discoteca.adapters.SongAdapter;
import com.example.discoteca.models.Album;
import com.example.discoteca.models.Song;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class AlbumFragment extends Fragment implements SongAdapter.OnSongClickListener{

    public static final String TAG = "AlbumFragment";
    ImageView ivAlbumDet;
    TextView tvAlbumNameD;
    TextView tvArtistAlbum;
    TextView tvAlbumYear;
    ImageButton ibClose;
    RelativeLayout rlAlbum;
    List<Song> songList;
    RecyclerView rvAlbumSongs;
    String accessToken;
    Album album;
    SongAdapter adapter;
    Spotify client;

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
        ibClose = view.findViewById(R.id.ibCloseAlbum);
        rlAlbum = view.findViewById(R.id.rlAlbum);

        album = Parcels.unwrap(this.getArguments().getParcelable("album"));
        accessToken = this.getArguments().getString("token");
        client = new Spotify(accessToken);

        Glide.with(getContext()).load(album.getImageUrl()).transform(new RoundedCorners(10)).into(ivAlbumDet);
        tvAlbumNameD.setText(album.getAlbumName());
        tvArtistAlbum.setText(album.getArtistName());
        tvAlbumYear.setText(album.getReleaseDate());
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlAlbum.setClickable(false);
                getParentFragment().getChildFragmentManager().beginTransaction().remove(AlbumFragment.this).commit();
            }
        });

        // Initialize song list
        songList = new ArrayList<>();

        // Adapter is initialized
        adapter = new SongAdapter(getContext(), songList, this);

        // Recycler view adapter and layout manager
        rvAlbumSongs.setAdapter(adapter);
        rvAlbumSongs.setLayoutManager(new LinearLayoutManager(getContext()));

        // Make API request
        makeRequest();

    }

    private void makeRequest() {
        List<Song> spotifyResp = client.makeAlbumRequest(album);
        adapter.addAll(spotifyResp);
    }

    @Override
    public void onSongClick(int position) {
        Fragment fragment = new SongFragment();

        // Pass song data to the detail fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("song", Parcels.wrap(songList.get(position)));
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragment().getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.flChild, fragment).commit();
    }
}
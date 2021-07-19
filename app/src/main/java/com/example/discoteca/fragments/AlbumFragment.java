package com.example.discoteca.fragments;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.discoteca.R;
import com.example.discoteca.adapters.SongAdapter;
import com.example.discoteca.models.Album;
import com.example.discoteca.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlbumFragment extends Fragment implements SongAdapter.OnSongClickListener{

    public static final String TAG = "AlbumFragment";
    ImageView ivAlbumDet;
    TextView tvAlbumNameD;
    TextView tvArtistAlbum;
    TextView tvAlbumYear;
    List<Song> songList;
    RecyclerView rvAlbumSongs;
    String accessToken;
    Album album;
    SongAdapter adapter;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();

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

        album = Parcels.unwrap(this.getArguments().getParcelable("album"));
        accessToken = this.getArguments().getString("token");

        Glide.with(getContext()).load(album.getImageUrl()).transform(new RoundedCorners(10)).into(ivAlbumDet);
        tvAlbumNameD.setText(album.getAlbumName());
        tvArtistAlbum.setText(album.getArtistName());
        tvAlbumYear.setText(album.getReleaseDate());

        // Initialize song list
        songList = new ArrayList<>();

        // Adapter is initialized
        adapter = new SongAdapter(getContext(), songList, this);

        // Recycler view adapter and layout manager
        rvAlbumSongs.setAdapter(adapter);
        rvAlbumSongs.setLayoutManager(new LinearLayoutManager(getContext()));

        makeRequest(album.getAlbumId());


    }

    private void makeRequest(String id){

        if (accessToken == null){
            Log.e(TAG, "No token");
            return;
        }
        String url = getUrl(id);

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        call(request);
    }

    private void call(Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONObject("tracks").getJSONArray("items");
                    for (int i = 0 ; i < jsonArray.length(); i++){
                        Song song = new Song();
                        JSONObject responseObject = jsonArray.getJSONObject(i);
                        song.setAlbumName(album.getAlbumName());
                        song.setArtistName(album.getArtistName());
                        song.setImageUrl(album.getImageUrl());
                        song.setReleaseDate(album.getReleaseDate());
                        song.setSongName(responseObject.getString("name"));
                        song.setSongId(responseObject.getString("id"));
                        song.setDuration(responseObject.getLong("duration_ms"));

                        songList.add(song);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getUrl(String id){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/albums/"+id).newBuilder();
        String url = urlBuilder.build().toString();
        return url;
    }

    @Override
    public void onSongClick(int position) {

    }
}
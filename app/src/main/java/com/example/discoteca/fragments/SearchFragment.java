package com.example.discoteca.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.discoteca.R;
import com.example.discoteca.adapters.AlbumAdapter;
import com.example.discoteca.adapters.SongAdapter;
import com.example.discoteca.models.Album;
import com.example.discoteca.models.Song;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchFragment extends Fragment implements AlbumAdapter.OnAlbumClickListener{

    private static final String TAG = "SearchFragment";
    FrameLayout flChild;
    RecyclerView rvSearch;
    SongAdapter songAdapter;
    AlbumAdapter albumAdapter;
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

        // Frame layout is initialized 
        flChild = view.findViewById(R.id.flChild);
        flChild.setVisibility(View.GONE);

        // Tab layout functionality
        tabLayout = view.findViewById(R.id.tabLayout);

        searchBar = view.findViewById(R.id.searchBar);

        // Recycler view is defined
        rvSearch = view.findViewById(R.id.rvResults);

        // Arrays are initialized
        songs = new ArrayList<>();
        albums = new ArrayList<>();

        // Adapter is created for each possible result
        songAdapter = new SongAdapter(getContext(), songs);
        albumAdapter = new AlbumAdapter(getContext(), albums, this);

        // Adapter and layout manager is defined
        rvSearch.setLayoutManager(new LinearLayoutManager(getContext()));

        // Getting the access token from main activity
        accessToken = this.getArguments().getString("token");

        //The default tab is the song tab
        songTab();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0){
                    songAdapter.clearAll(true);
                    searchBar.setQuery("",false);
                    songTab();
                }
                if (tabLayout.getSelectedTabPosition() == 1){
                    albumAdapter.clearAll(true);
                    searchBar.setQuery("",false);
                    albumTab();
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

    private void songTab(){
        rvSearch.setAdapter(songAdapter);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                makeRequest(query, "song");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

    }

    private void albumTab(){
        rvSearch.setAdapter(albumAdapter);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                makeRequest(query, "album");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

    }

    private void makeRequest(String query, String type){

        if (accessToken == null){
            Log.e(TAG, "No token");
            return;
        }
        String url = getUrl(query, type);

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        if (type == "song") {
            callSong(request);
        }
        if (type == "album"){
            callAlbum(request);
        }
    }

    private void callAlbum(Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    albumAdapter.clearAll(false);
                    List<Album> results = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONObject("albums").getJSONArray("items");
                    for (int i = 0; i < jsonArray.length()-1; i++){
                        Album albumR = new Album();
                        JSONObject album = jsonArray.getJSONObject(i);
                        albumR.setAlbumId(album.getString("id"));
                        albumR.setAlbumName(album.getString("name"));
                        albumR.setArtistName(album.getJSONArray("artists").getJSONObject(0).getString("name"));
                        albumR.setImageUrl(album.getJSONArray("images").getJSONObject(1).getString("url"));
                        albumR.setNoTracks(album.getInt("total_tracks"));
                        albumR.setReleaseDate(album.getString("release_date"));
                        results.add(albumR);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            albumAdapter.addAll(results);
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse data: " + e);
                }

            }
        });
    }

    private void callSong(Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    songAdapter.clearAll(false);
                    List<Song> results = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONObject("tracks").getJSONArray("items");
                    for (int i = 0; i < jsonArray.length()-1; i++){
                        Song song = new Song();
                        JSONObject track = jsonArray.getJSONObject(i);
                        song.setSongId(track.getString("id"));
                        song.setSongName(track.getString("name"));
                        song.setReleaseDate(track.getJSONObject("album").getString("release_date"));
                        song.setImageUrl(track.getJSONObject("album").getJSONArray("images").getJSONObject(1).getString("url"));
                        song.setDuration(track.getLong("duration_ms"));
                        song.setArtistName(track.getJSONArray("artists").getJSONObject(0).getString("name"));
                        song.setAlbumName(track.getJSONObject("album").getString("name"));
                        results.add(song);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            songAdapter.addAll(results);
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse data: " + e);
                }
            }
        });

    }

    private String getUrl(String query, String type){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/search").newBuilder();
        urlBuilder.addQueryParameter("q", query);
        if (type == "song"){
            urlBuilder.addQueryParameter("type", "track");
        }
        if (type == "album"){
            urlBuilder.addQueryParameter("type", "album");
        }
        String url = urlBuilder.build().toString();
        return url;
    }


    @Override
    public void onAlbumClick(int position) {
        Toast.makeText(getContext(), "ALbum clickedd", Toast.LENGTH_SHORT).show();


    }

}
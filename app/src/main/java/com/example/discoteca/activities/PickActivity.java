package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.discoteca.R;
import com.example.discoteca.adapters.SongAdapter;
import com.example.discoteca.models.Album;
import com.example.discoteca.models.Song;
import com.google.android.material.tabs.TabLayout;

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

public class PickActivity extends AppCompatActivity implements SongAdapter.OnSongClickListener{

    public static final String TAG = "PickActivity";
    private String accessToken;
    SearchView searchBar;
    ImageButton ibBack;
    RecyclerView rvPickSongs;
    List<Song> songs;
    List<Song> albumList;
    SongAdapter adapter;
    TabLayout tabLayout;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        accessToken = getIntent().getStringExtra("token");

        // Components references from the pick activity layout
        searchBar = findViewById(R.id.svPickSong);
        rvPickSongs = findViewById(R.id.rvPickSongs);
        tabLayout = findViewById(R.id.tabPick);
        ibBack = findViewById(R.id.ibBack);

        // Initialize list of objects
        songs = new ArrayList<>();

        // Initialize song adapter
        adapter = new SongAdapter(PickActivity.this, songs, this);

        // Song adapter and layout manager
        rvPickSongs.setAdapter(adapter);
        rvPickSongs.setLayoutManager(new LinearLayoutManager(this));

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Default tab
        songTab();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0){
                    adapter.clearAll(true);
                    searchBar.setQuery("",false);
                    songTab();
                }
                if (tabLayout.getSelectedTabPosition() == 1){
                    adapter.clearAll(true);
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

    private void songTab() {
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Request call
                client.makeSearchSongRequest(query).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Failed to fetch data: " + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        List<Song> results = new ArrayList<>();
                        results.addAll(client.createSongs(response, results));
                        PickActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.clearAll(false);
                                adapter.addAll(results);
                            }
                        });
                    }
                });;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void albumTab() {
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Request call
                makeRequest(query, "album");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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
                    albumList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONObject("albums").getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++){
                        Album albumR = new Album();
                        JSONObject album = jsonArray.getJSONObject(i);
                        albumR.setAlbumId(album.getString("id"));
                        albumR.setAlbumName(album.getString("name"));
                        albumR.setArtistName(album.getJSONArray("artists").getJSONObject(0).getString("name"));
                        albumR.setImageUrl(album.getJSONArray("images").getJSONObject(1).getString("url"));
                        albumR.setNoTracks(album.getInt("total_tracks"));
                        albumR.setReleaseDate(album.getString("release_date"));
                        getAlbumSongs(albumR);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse data: " + e);
                }

            }
        });
    }

    private void getAlbumSongs(Album album){
        if (accessToken == null){
            Log.e(TAG, "No token");
            return;
        }
        // URL builder
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/albums/"+album.getAlbumId()).newBuilder();
        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

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
                        albumList.add(song);
                    }
                    PickActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clearAll(false);
                            adapter.addAll(albumList);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
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
                    adapter.clearAll(false);
                    List<Song> results = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONObject("tracks").getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++){
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
                    PickActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addAll(results);
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
    public void onSongClick(int position) {
        Intent result = new Intent();
        result.putExtra("song", Parcels.wrap(songs.get(position)));
        setResult(RESULT_OK, result);
        finish();
    }
}
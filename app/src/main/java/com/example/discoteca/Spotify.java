package com.example.discoteca;

import android.util.Log;
import com.example.discoteca.models.Album;
import com.example.discoteca.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Spotify {

    public static final String TAG = "SpotifyClient";
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    String accessToken;
    Album album;
    List<Song> songList = new ArrayList<>();
    List<Album> albumList = new ArrayList<>();

    public Spotify(String accessToken) {
        this.accessToken = accessToken;
    }

    public Call makeSearchSongRequest(String query){

        if (accessToken == null){
            Log.e(TAG, "No token");
        }
        String url = getSearchUrl(query, "song");

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        return callSong(request);
    }

    private Call callSong(Request request) {
        return mOkHttpClient.newCall(request);

    }

        try {
            executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return results;
    }

    public List<Album> makeSearchAlbumRequest(String query){

        if (accessToken == null){
            Log.e(TAG, "No token");
        }
        String url = getSearchUrl(query, "album");

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                callAlbum(request);
            }
        };

        // Creates a new thread to wait for the API response
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(runnable);
        try {
            executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return albumList;
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
                        albumList.add(albumR);
                    }

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
                    songList.clear();
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
                        songList.add(song);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse data: " + e);
                }
            }
        });

    }

    private String getSearchUrl(String query, String type){
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

    public List<Song> makeAlbumRequest(Album album){
        this.album = album;
        if (accessToken == null){
            Log.e(TAG, "No token");
        }
        String url = getAlbumUrl(album.getAlbumId());

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                callAlbumSongs(request);
            }
        };

        // Creates a new thread to wait for the API response
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(runnable);
        try {
            executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Once the thread is finished, the list is returned
        return songList;
    }

    private void callAlbumSongs(Request request) {
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
                    Log.e(TAG, "Failed to parse data: " + e);
                }
            }
        });
    }

    private String getAlbumUrl(String id){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/albums/"+id).newBuilder();
        String url = urlBuilder.build().toString();
        return url;
    }
}

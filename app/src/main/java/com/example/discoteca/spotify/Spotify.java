package com.example.discoteca.spotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.discoteca.BuildConfig;
import com.example.discoteca.activities.WelcomeActivity;
import com.example.discoteca.models.Album;
import com.example.discoteca.models.Song;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
    private static final String CLIENT_ID = BuildConfig.CONSUMER_KEY;
    public static final String CLIENT_SECRET = BuildConfig.CONSUMER_SECRET;
    private static final String REDIRECT_URI = "https://example.com/spotify-redirect";
    private static final URI redirectUri = SpotifyHttpManager.makeUri(REDIRECT_URI);

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setClientSecret(CLIENT_SECRET)
            .setRedirectUri(redirectUri)
            .build();

    private static final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
            .build();

    String accessToken;
    Context context;
    SharedPreferences sharedPref;

    public Spotify(Context context, SharedPreferences sharedPref) {
        this.context = context;
        this.sharedPref = sharedPref;
        refreshToken();
    }

    private boolean isExpired (int expired){
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int currentHour  = (hourOfDay * 60) + minute;

        if (expired >= 1440){
        }

        if (currentHour >= expired){
            return true;
        }
        return false;
    }

    // Refresh token

    public void refreshToken(){
        int expired = sharedPref.getInt("expires", 0);

        if (isExpired(expired)){
            final CompletableFuture<ClientCredentials> clientCredentialsFuture = clientCredentialsRequest.executeAsync();

            // Example Only. Never block in production code.
            final ClientCredentials clientCredentials = clientCredentialsFuture.join();

            // Set access token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
            accessToken = spotifyApi.getAccessToken();

            //Store access token on shared preferences
            WelcomeActivity welcomeActivity = new WelcomeActivity();
            welcomeActivity.storeAccessToken(accessToken, sharedPref);
        }
        else {
            this.accessToken = sharedPref.getString("token",null);
        }
    }

    // Search Song Request

    public Call makeSearchSongRequest(String query, int page){

        if (accessToken == null){
            Log.e(TAG, "No token");
        }
        String url = getSearchUrl(query, "song", page);

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        return callSong(request);
    }


    private Call callSong(Request request) {
        return mOkHttpClient.newCall(request);

    }

    public List<Song> createSongs(Response response, List<Song> results){
        try {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse data: " + e);
        }

        return results;
    }

    // Search Album Request

    public Call makeSearchAlbumRequest(String query, int page){

        if (accessToken == null){
            Log.e(TAG, "No token");
        }
        String url = getSearchUrl(query, "album", page);

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        return callAlbum(request);
    }

    private Call callAlbum(Request request) {
        return mOkHttpClient.newCall(request);
    }

    public List<Album> createAlbum(Response response, List<Album> results){
        try {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                results.add(albumR);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse data: " + e);
        }

        return results;
    }

    private String getSearchUrl(String query, String type, int page){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/search").newBuilder();
        urlBuilder.addQueryParameter("q", query);
        urlBuilder.addQueryParameter("offset", String.valueOf(page));
        if (type == "song"){
            urlBuilder.addQueryParameter("type", "track");
        }
        if (type == "album"){
            urlBuilder.addQueryParameter("type", "album");
        }
        String url = urlBuilder.build().toString();
        return url;
    }

    // Search album by id request

    public Call makeAlbumRequest(Album album){
        if (accessToken == null){
            Log.e(TAG, "No token");
        }
        String url = getAlbumUrl(album.getAlbumId());

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        return callAlbumSongs(request);
    }

    // Songs of an album

    public List<Song> createAlbumForSongs(Response response, List<Album> results){
        List<Song> albumSongs = new ArrayList<>();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                        results.add(albumR);
                    }
                    for (int i = 0; i < results.size(); i++){
                        int count = i;
                        List<Song> songsRes = new ArrayList<>();
                        Album album = results.get(i);
                        makeAlbumRequest(album).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e(TAG, "Failed to fetch data: " + e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                songsRes.clear();
                                albumSongs.addAll(createAlbumSongs(response, songsRes, album));
                            }
                        });;
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse data: " + e);
                }
            }
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(runnable);

        try {
            executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return albumSongs;
    }

    private Call callAlbumSongs(Request request) {
        return mOkHttpClient.newCall(request);
    }

    public List<Song> createAlbumSongs(Response response, List<Song> songs, Album album){
        JSONObject jsonObject = null;
        try {
            try {
                jsonObject = new JSONObject(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
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

                songs.add(song);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse data: " + e);
        }

        return songs;
    }

    private String getAlbumUrl(String id){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/albums/"+id).newBuilder();
        String url = urlBuilder.build().toString();
        return url;
    }

    // Song by id request

    public Call makeSongRequest(String id){
        if (accessToken == null){
            Log.e(TAG, "No token");
        }
        String url = getSongUrl(id);

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        return callSongId(request);
    }

    private Call callSongId(Request request) {
        return mOkHttpClient.newCall(request);
    }

    public Song createSong(Response response, Song song){
        JSONObject songResponse = null;
        try {
            try {
                songResponse = new JSONObject(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            };
            JSONObject album = songResponse.getJSONObject("album");
            song.setAlbumName(album.getString("name"));
            song.setArtistName(album.getJSONArray("artists").getJSONObject(0).getString("name"));
            song.setImageUrl(album.getJSONArray("images").getJSONObject(1).getString("url"));
            song.setReleaseDate(album.getString("release_date"));
            song.setSongName(songResponse.getString("name"));
            song.setSongId(songResponse.getString("id"));
            song.setDuration(songResponse.getLong("duration_ms"));
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse data: " + e);
        }

        return song;
    };

    private String getSongUrl(String id) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/tracks/"+id).newBuilder();
        String url = urlBuilder.build().toString();
        return url;
    }
}

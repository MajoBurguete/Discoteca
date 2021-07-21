package com.example.discoteca;

import android.util.Log;

import com.example.discoteca.models.Album;
import com.example.discoteca.models.Song;


public class Spotify {

    public static final String TAG = "SpotifyClient";
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    String accessToken;

    public Spotify(String accessToken) {
        this.accessToken = accessToken;
    }


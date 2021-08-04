package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.discoteca.R;

public class WelcomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SIGN = 7;
    private static final String CLIENT_ID = BuildConfig.CONSUMER_KEY;
    public static final String CLIENT_SECRET = BuildConfig.CONSUMER_SECRET;
    private static final String REDIRECT_URI = "https://example.com/spotify-redirect";
    private static final URI redirectUri = SpotifyHttpManager.makeUri(REDIRECT_URI);
    private String accessToken = "";
    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setClientSecret(CLIENT_SECRET)
            .setRedirectUri(redirectUri)
            .build();

    private static final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }
}
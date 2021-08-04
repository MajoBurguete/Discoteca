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
    private void authorizeUser(){

        final CompletableFuture<ClientCredentials> clientCredentialsFuture = clientCredentialsRequest.executeAsync();

        // Example Only. Never block in production code.
        final ClientCredentials clientCredentials = clientCredentialsFuture.join();

        // Set access token for further "spotifyApi" object usage
        spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        accessToken = spotifyApi.getAccessToken();
        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.share_preferences_file), Context.MODE_PRIVATE);
        storeAccessToken(spotifyApi.getAccessToken(), sharedPref);

    }

    public void storeAccessToken(String token, SharedPreferences sharedPref){

        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int currentHour  = (hourOfDay * 60) + minute + 60;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.putInt("expires", currentHour);
        editor.apply();

    }
}
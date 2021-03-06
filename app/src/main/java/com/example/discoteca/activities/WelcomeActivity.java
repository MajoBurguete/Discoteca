package com.example.discoteca.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.discoteca.BuildConfig;
import com.example.discoteca.R;
import com.parse.ParseUser;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.net.URI;
import java.util.Calendar;
import java.util.concurrent.CompletableFuture;

public class WelcomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SIGN = 7;
    private static final String CLIENT_ID = BuildConfig.CONSUMER_KEY;
    public static final String CLIENT_SECRET = BuildConfig.CONSUMER_SECRET;
    private static final String REDIRECT_URI = "https://example.com/spotify-redirect";
    private static final URI redirectUri = SpotifyHttpManager.makeUri(REDIRECT_URI);
    private String accessToken = "";
    LoginActivity loginActivity = new LoginActivity();
    Button btnLogin;
    Button btnSignup;

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


        //Layout reference
        btnLogin = findViewById(R.id.btnLoginWel);
        btnSignup = findViewById(R.id.btnSignupWel);

        if (ParseUser.getCurrentUser() != null){
            loginActivity.goMainActivity(WelcomeActivity.this);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(login);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign = new Intent(WelcomeActivity.this, SignupActivity.class);
                startActivityForResult(sign, REQUEST_CODE_SIGN);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        authorizeUser();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SIGN && resultCode == RESULT_OK){
            String username = data.getStringExtra("username");
            String password = data.getStringExtra("password");
            Toast.makeText(WelcomeActivity.this, "Account created successfully", Toast.LENGTH_LONG).show();
            loginActivity.loginUser(username, password, WelcomeActivity.this);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

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
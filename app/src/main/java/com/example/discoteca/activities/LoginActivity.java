package com.example.discoteca.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.discoteca.BuildConfig;
import com.example.discoteca.R;

import com.example.discoteca.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.spotify.sdk.android.authentication.AuthenticationClient;

import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SIGN = 7;
    private static final int REQUEST_CODE = 8;
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
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (ParseUser.getCurrentUser() != null){
            goMainActivity();
        }

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accessToken.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Remember to login to your spotify account!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                if ( username.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Make sure not to leave anything blank!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Method to login
                loginUser(username, password);
            }
        });

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign = new Intent(LoginActivity.this, SignupActivity.class);
                startActivityForResult(sign, REQUEST_CODE_SIGN);
            }
        });

        authorizeUser();
    }

    private void authorizeUser(){

        final CompletableFuture<ClientCredentials> clientCredentialsFuture = clientCredentialsRequest.executeAsync();

        // Example Only. Never block in production code.
        final ClientCredentials clientCredentials = clientCredentialsFuture.join();

        // Set access token for further "spotifyApi" object usage
        spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        accessToken = spotifyApi.getAccessToken();
        storeAccessToken(spotifyApi.getAccessToken(), this);
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SIGN && resultCode == RESULT_OK){
            String username = data.getStringExtra("username");
            String password = data.getStringExtra("password");
            Toast.makeText(LoginActivity.this, "Account created successfully", Toast.LENGTH_LONG).show();
            loginUser(username, password);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getAccessToken(){
        return accessToken;
    }

        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.share_preferences_file), Context.MODE_PRIVATE);
    public void storeAccessToken(String token, SharedPreferences sharedPref){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.apply();

    }

    private void loginUser(String username, String password) {
        // Navigate to the main activity if the user has logged in properly
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null){
                    Toast.makeText(LoginActivity.this, "Failed to login, username or password incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Go to main activity
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
}
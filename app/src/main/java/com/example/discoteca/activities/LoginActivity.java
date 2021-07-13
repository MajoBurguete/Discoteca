package com.example.discoteca.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.example.discoteca.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SIGN = 7;
    private static final int REQUEST_CODE = 8;
    private static final String CLIENT_ID = "a3dd6fa3d5af4f029264e77f1d5f629b";
    private static final String REDIRECT_URI = "intent://";
    private SpotifyAppRemote mSpotifyAppRemote;

    // Set the connection parameters
    ConnectionParams connectionParams =
            new ConnectionParams.Builder(CLIENT_ID)
                    .setRedirectUri(REDIRECT_URI)
                    .showAuthView(true)
                    .build();

    AuthenticationRequest.Builder builder =
            new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ParseUser.getCurrentUser() != null){
            goMainActivity();
        }

        binding.btnSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setScopes(new String[]{"streaming"});
                AuthenticationRequest request = builder.build();
                AuthenticationClient.openLoginActivity(LoginActivity.this, REQUEST_CODE, request);
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SIGN && resultCode == RESULT_OK){
            String username = data.getStringExtra("username");
            String password = data.getStringExtra("password");
            Toast.makeText(LoginActivity.this, "Account created successfully", Toast.LENGTH_LONG).show();
            loginUser(username, password);
        }
        if (requestCode == REQUEST_CODE ){
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode,data);
            if (response.getType() == AuthenticationResponse.Type.TOKEN){
                SpotifyAppRemote.connect(LoginActivity.this, connectionParams, new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        Log.e("MainActivity", error.getMessage(), error);
                    }
                });
            }
            if (response.getType() == AuthenticationResponse.Type.ERROR){
                Log.e("API call", response.getError());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        finish();
    }
}
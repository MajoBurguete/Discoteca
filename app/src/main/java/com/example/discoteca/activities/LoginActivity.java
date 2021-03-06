package com.example.discoteca.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.discoteca.R;

import com.example.discoteca.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SIGN = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (ParseUser.getCurrentUser() != null){
            goMainActivity(LoginActivity.this);
        }

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
                loginUser(username, password, LoginActivity.this);
            }
        });

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign = new Intent(LoginActivity.this, SignupActivity.class);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
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
            loginUser(username, password, LoginActivity.this);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loginUser(String username, String password, Context context) {
        // Navigate to the main activity if the user has logged in properly
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null){
                    Toast.makeText(context, "Failed to login, username or password incorrect", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Go to main activity
                goMainActivity(context);
                Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goMainActivity(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
}
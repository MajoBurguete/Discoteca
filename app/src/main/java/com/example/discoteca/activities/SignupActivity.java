package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.discoteca.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    private String TAG = "SignupActivity";
    Button btnNewus;
    EditText etUsersign;
    EditText etPassign;
    EditText etEmail;
    ImageView ivSignPicture;
    FloatingActionButton fabImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btnNewus = findViewById(R.id.btnNewus);
        etUsersign = findViewById(R.id.etUsersign);
        etPassign = findViewById(R.id.etPassign);
        etEmail = findViewById(R.id.etEmail);
        ivSignPicture = findViewById(R.id.ivSignPicture);
        fabImage = findViewById(R.id.fabImage);

        btnNewus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsersign.getText().toString();
                String password = etPassign.getText().toString();
                String email = etEmail.getText().toString();
                if (username.isEmpty() || password.isEmpty() || email.isEmpty()){
                    Toast.makeText(SignupActivity.this, "Make sure not to leave anything blank!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Create the ParseUser
                ParseUser user = new ParseUser();

                // Set properties
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(SignupActivity.this, "Failed to signup, try again!", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Failed to signup", e);
                            return;
                        }
                        Toast.makeText(SignupActivity.this, "Signup successfully!", Toast.LENGTH_LONG).show();
                        Intent result = new Intent();
                        result.putExtra("username", username);
                        result.putExtra("password", password);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                });
            }

        });

        fabImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
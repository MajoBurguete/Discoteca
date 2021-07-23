package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.discoteca.R;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_USERNAME = "username";
    private String TAG = "SettingsActivity";
    ImageView ivUserPict;
    EditText etNameEdit;
    TextView tvEmail;
    Button btnSaveProf;
    ImageButton ibClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Gets current user data
        ParseUser user = ParseUser.getCurrentUser();

        // Get references
        ivUserPict = findViewById(R.id.ivUserPict);
        etNameEdit = findViewById(R.id.etNameEdit);
        tvEmail = findViewById(R.id.tvEmail);
        btnSaveProf = findViewById(R.id.btnSaveProf);
        ibClose = findViewById(R.id.ibClose);

        // Bind data
        etNameEdit.setText(user.getUsername());
        tvEmail.setText(user.getEmail());

        btnSaveProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currUser = ParseUser.getCurrentUser();
                String newUsername = etNameEdit.getText().toString();
                saveUser(newUsername, currUser);

            }
        });

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void saveUser(String newUsername, ParseUser currentUser) {
        currentUser.put(KEY_USERNAME, newUsername);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving description", e);
                    Toast.makeText(SettingsActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(SettingsActivity.this, "Your username was save successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
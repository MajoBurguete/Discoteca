package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.discoteca.R;
import com.parse.ParseUser;

public class SettingsActivity extends AppCompatActivity {

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


    }
}
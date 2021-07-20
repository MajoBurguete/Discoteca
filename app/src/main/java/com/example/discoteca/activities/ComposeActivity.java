package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.discoteca.R;

public class ComposeActivity extends AppCompatActivity {

    ImageView ivComposeAlbum;
    TextView tvAlbumName;
    TextView tvArtistName;
    TextView tvSongName;
    Button btnPick;
    EditText etDescription;
    Button btnAddFact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // Components references from compose layout
        ivComposeAlbum = findViewById(R.id.ivComposeAlbum);
        tvAlbumName = findViewById(R.id.tvAlbumName);
        tvArtistName = findViewById(R.id.tvArtistName);
        tvSongName = findViewById(R.id.tvSongName);
        btnPick = findViewById(R.id.btnPick);
        etDescription = findViewById(R.id.etDescriptionFact);
        btnAddFact = findViewById(R.id.btnAddFact);

    }
}
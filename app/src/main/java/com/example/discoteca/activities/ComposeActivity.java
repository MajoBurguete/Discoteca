package com.example.discoteca.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.discoteca.R;
import com.example.discoteca.models.Song;

import org.parceler.Parcels;

public class ComposeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 9;

    ImageView ivComposeAlbum;
    TextView tvAlbumName;
    TextView tvArtistName;
    TextView tvSongName;
    Button btnPick;
    EditText etDescription;
    Button btnAddFact;
    Song song;

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

        if (song != null){
        }

        onPickClick();

    }

    private void onPickClick() {
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pick = new Intent(ComposeActivity.this, PickActivity.class);
                String accessToken = getIntent().getStringExtra("token");
                pick.putExtra("token", accessToken);
                startActivityForResult(pick, REQUEST_CODE);
            }
        });
    }
}
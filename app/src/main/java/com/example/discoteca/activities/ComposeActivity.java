package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.discoteca.R;

public class ComposeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 9;

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
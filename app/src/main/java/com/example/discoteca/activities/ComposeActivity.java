package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.discoteca.R;
import com.example.discoteca.models.Fact;
import com.example.discoteca.models.Song;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
    ImageButton ibReturn;
    Song song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Song intentSong = Parcels.unwrap(getIntent().getParcelableExtra("song"));
        if (intentSong != null){
            song = intentSong;
        }

        // Components references from compose layout
        ivComposeAlbum = findViewById(R.id.ivComposeAlbum);
        tvAlbumName = findViewById(R.id.tvAlbumName);
        tvArtistName = findViewById(R.id.tvArtistName);
        tvSongName = findViewById(R.id.tvSongName);
        btnPick = findViewById(R.id.btnPick);
        etDescription = findViewById(R.id.etDescriptionFact);
        btnAddFact = findViewById(R.id.btnAddFact);
        ibReturn = findViewById(R.id.ibReturn);

        if (song != null){
            updateSong();
        }

        ibReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getText().toString();
                if (description.isEmpty() || song == null){
                    Toast.makeText(ComposeActivity.this, "Make sure not to leave anything blank!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Fact fact = new Fact();
                fact.setId(song.getSongId());
                fact.setSong(song.getSongName());
                fact.setAlbum(song.getAlbumName());
                fact.setArtist(song.getArtistName());
                fact.setUrl(song.getImageUrl());
                fact.setDescription(description);
                fact.setUser(ParseUser.getCurrentUser());

                fact.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Toast.makeText(ComposeActivity.this, "There was an issue posting the fact, try again!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ParseUser user = ParseUser.getCurrentUser();
                        long number = user.getLong("factNumber");
                        number = number + 1;
                        user.put("factNumber", number);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                finish();
                            }
                        });

                    }
                });
            }
        });

        onPickClick();

    }

    private void updateSong(){
        Glide.with(this).load(song.getImageUrl()).into(ivComposeAlbum);
        tvAlbumName.setText(song.getAlbumName());
        tvArtistName.setText(song.getArtistName());
        tvSongName.setText(song.getSongName());
    }

    private void onPickClick() {
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pick = new Intent(ComposeActivity.this, PickActivity.class);
                String accessToken = getIntent().getStringExtra("token");
                pick.putExtra("token", accessToken);
                startActivityForResult(pick, REQUEST_CODE);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            song = Parcels.unwrap(data.getParcelableExtra("song"));
            updateSong();
        }
    }
}
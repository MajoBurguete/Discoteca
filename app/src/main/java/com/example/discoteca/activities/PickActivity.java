package com.example.discoteca.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.discoteca.R;

public class PickActivity extends AppCompatActivity {

    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        accessToken = getIntent().getStringExtra("token");
    }
}
package com.example.discoteca.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.example.discoteca.R;
import com.example.discoteca.fragments.HomeFragment;
import com.example.discoteca.fragments.ProfileFragment;
import com.example.discoteca.fragments.SearchFragment;
import com.example.discoteca.fragments.SearchUserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    BottomNavigationView bottomNav;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private String accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accessToken = getIntent().getStringExtra("token");

        // Find the toolbar inside the layout
        Toolbar toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);
         // Bottom navigation is defined
        bottomNav = findViewById(R.id.bottomNav);
        bottomItemSelected();

    }

    private void bottomItemSelected() {
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new HomeFragment();
                if (item.getItemId() == R.id.btnHome){
                    fragment = new HomeFragment();
                }
                if (item.getItemId() == R.id.btnCompose){
                    Intent compose = new Intent(MainActivity.this, ComposeActivity.class);
                    startActivity(compose);
                    overridePendingTransition(R.anim.up_in, R.anim.up_out);
                }
                if (item.getItemId() == R.id.btnSearch){
                    fragment = new SearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("token", accessToken);
                    fragment.setArguments(bundle);
                }
                if (item.getItemId() == R.id.btnProfile){
                    fragment = new ProfileFragment();
                }
                if (item.getItemId() == R.id.btnUser){
                    fragment = new SearchUserFragment();
                }
                FragmentTransaction fts =fragmentManager.beginTransaction();
                fts.setCustomAnimations(R.anim.right_in, R.anim.left_out);
                fts.replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        bottomNav.setSelectedItemId(R.id.btnHome);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btnLogout){
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser();
            Intent login = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(login);
            finish();
        }
        if (item.getItemId() == R.id.settings){
            Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settings);
        }
        return super.onOptionsItemSelected(item);
    }
}

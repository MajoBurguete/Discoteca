package com.example.discoteca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.discoteca.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
            }
        });
    }
}
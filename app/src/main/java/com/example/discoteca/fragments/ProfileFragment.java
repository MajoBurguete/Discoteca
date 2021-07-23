package com.example.discoteca.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.discoteca.R;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment {

    private String TAG = "ProfileFragment";
    ImageView ivProfPict;
    TextView tvNameP;
    TextView tvNumber;
    RecyclerView rvUserFacts;
    public static final String KEY_PROFILE = "profile";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Layout references
        ivProfPict = view.findViewById(R.id.ivProfPict);
        tvNameP = view.findViewById(R.id.tvNameP);
        tvNumber = view.findViewById(R.id.tvNumber);
        rvUserFacts = view.findViewById(R.id.rvUserFacts);

        ParseUser user = ParseUser.getCurrentUser();

        Glide.with(getContext()).load(user.getParseFile(KEY_PROFILE)).circleCrop().into(ivProfPict);
        tvNameP.setText(user.getUsername());
}
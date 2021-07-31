package com.example.discoteca.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.discoteca.R;
import com.example.discoteca.adapters.FactAdapter;
import com.example.discoteca.models.Fact;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends Fragment implements FactAdapter.OnFactClickListener {

    private static final String TAG = "UserProfileFragment";
    TextView tvUserName;
    ImageView ivOtherUserPict;
    SwipeRefreshLayout srUserProfile;
    RecyclerView rvUserFacts;
    List<Fact> userFacts;
    FactAdapter factAdapter;
    TextView tvUserNumber;
    ParseUser user;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Layout references
        tvUserName = view.findViewById(R.id.tvUserName);
        ivOtherUserPict = view.findViewById(R.id.ivOtherUserPict);
        srUserProfile = view.findViewById(R.id.srUserProfile);
        rvUserFacts = view.findViewById(R.id.rvOtherUserFacts);
        tvUserNumber = view.findViewById(R.id.tvUserNumber);

        // user gotten from arguments
        user = Parcels.unwrap(this.getArguments().getParcelable("user"));

        // Array for the adapter is initialized
        userFacts = new ArrayList<>();

        //Adapter initialized
        factAdapter = new FactAdapter(getContext(), userFacts, false,this);

        //Linear layout manager created and initialized
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // Recycler view
        rvUserFacts.setAdapter(factAdapter);
        rvUserFacts.setLayoutManager(linearLayoutManager);

        // Bind user data into the layout
        tvUserName.setText(user.getUsername());
        Glide.with(getContext()).load(user.getParseFile("profile").getUrl()).circleCrop().into(ivOtherUserPict);

    @Override
    public void onDeleteClick(int position) {

    }

    @Override
    public void onLikeClick(int position) {

    }

    @Override
    public void onSongFactClick(int position) {

    }
}
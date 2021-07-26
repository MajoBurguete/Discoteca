package com.example.discoteca.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.discoteca.R;
import com.example.discoteca.adapters.FactAdapter;
import com.example.discoteca.models.Fact;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment implements FactAdapter.OnFactClickListener{

    private String TAG = "ProfileFragment";
    ImageView ivProfPict;
    TextView tvNameP;
    TextView tvNumber;
    RecyclerView rvUserFacts;
    FactAdapter adapter;
    List<Fact> userFacts;
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

        // Bind data
        Glide.with(getContext()).load(user.getParseFile(KEY_PROFILE).getUrl()).circleCrop().into(ivProfPict);
        tvNameP.setText(user.getUsername());

        // Array initialized
        userFacts = new ArrayList<>();

        // Adapter initialized
        adapter = new FactAdapter(getContext(), userFacts, true, this);

        // Assign adapter and layout manager
        rvUserFacts.setAdapter(adapter);
        rvUserFacts.setLayoutManager(new LinearLayoutManager(getContext()));

        queryFacts();

    }

    private void queryFacts() {
        ParseQuery<Fact> query = ParseQuery.getQuery(Fact.class);
        query.include(Fact.KEY_USER);
        query.whereEqualTo(Fact.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Fact>() {
            @Override
            public void done(List<Fact> facts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting facts", e);
                    return;
                }
                adapter.clearAll(true);
                adapter.addAll(facts);

                // Set number of facts
                int value  = adapter.getItemCount();
                tvNumber.setText(String.valueOf(value));

            }
        });
    }

    @Override
    public void onDeleteClick(int position) {
    }

}
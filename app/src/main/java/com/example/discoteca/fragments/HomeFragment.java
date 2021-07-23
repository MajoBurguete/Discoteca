package com.example.discoteca.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.discoteca.R;
import com.example.discoteca.adapters.FactAdapter;
import com.example.discoteca.models.Fact;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    RecyclerView rvFacts;
    List<Fact> factsL;
    FactAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get references
        rvFacts = view.findViewById(R.id.rvFacts);

        // Initialize fact list
        factsL = new ArrayList<>();
        songList = new ArrayList<>();

        // Initialize fact adapter
        adapter = new FactAdapter(getContext(), factsL, songList);

        // Assign adapter nd layout manager to the recycler view
        rvFacts.setAdapter(adapter);
        rvFacts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Query facts from database
        queryFacts();

    }

    private void queryFacts() {
        ParseQuery<Fact> query = ParseQuery.getQuery(Fact.class);
        query.include(Fact.KEY_USER);
        query.setLimit(20);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Fact>() {
            @Override
            public void done(List<Fact> facts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting facts", e);
                    return;
                }
                factsNew.clear();
                songNew.clear();
            }
        });
    }

    }
}
package com.example.discoteca.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.discoteca.EndlessScrolling;
import com.example.discoteca.R;
import com.example.discoteca.adapters.FactAdapter;
import com.example.discoteca.models.Fact;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements FactAdapter.OnFactClickListener{

    private static final String TAG = "HomeFragment";
    RecyclerView rvFacts;
    List<Fact> factsL;
    FactAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    EndlessScrolling scrollListener;
    public static final String KEY_LIST = "factsLiked";

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
        swipeRefreshLayout = view.findViewById(R.id.refreshFeed);

        // Setting the listener for the swipe container
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryFacts();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Initialize fact list
        factsL = new ArrayList<>();

        // Initialize fact adapter
        adapter = new FactAdapter(getContext(), factsL, false, this);

        // Infinite scrolling
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());

        scrollListener = new EndlessScrolling(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                page = page * 10;
                loadNextData(page);
            }
        };


        // Assign adapter nd layout manager to the recycler view
        rvFacts.setAdapter(adapter);
        rvFacts.setLayoutManager(linearLayoutManager);
        rvFacts.addOnScrollListener(scrollListener);

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
                adapter.clearAll(false);
                adapter.addAll(facts);

            }
        });
    }

    @Override
    public void onDeleteClick(int position) {
    }

    @Override
    public void onLikeClick(int position) {
        ParseUser user = ParseUser.getCurrentUser();
        List<String> likeFacts= user.getList(KEY_LIST);
        Fact fact = factsL.get(position);
        String objectID = fact.getObjectId();

        for (int i = 0; i < likeFacts.size(); i++){
            if (likeFacts.get(i).equals(objectID)){
                Toast.makeText(getContext(), "Liked already", Toast.LENGTH_SHORT).show();
                likeFacts.remove(i);
                user.put(KEY_LIST, likeFacts);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e(TAG, "Issue with disliking facts", e);
                            return;
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
                break;
            } else if (i == likeFacts.size()-1){
                Toast.makeText(getContext(), "Liking ...", Toast.LENGTH_SHORT).show();
                likeFacts.add(0, fact.getObjectId());
                user.put(KEY_LIST, likeFacts);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e(TAG, "Issue with liking facts", e);
                            return;
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
                break;
            }
        }

    }
}

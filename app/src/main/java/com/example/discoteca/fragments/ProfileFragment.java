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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.discoteca.R;
import com.example.discoteca.adapters.FactAdapter;
import com.example.discoteca.models.Fact;
import com.google.android.material.tabs.TabLayout;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment implements FactAdapter.OnFactClickListener{

    private String TAG = "ProfileFragment";
    ImageView ivProfPict;
    TextView tvNameP;
    TextView tvNumber;
    RecyclerView rvUserFacts;
    FactAdapter adapter;
    FactAdapter likeAdapter;
    List<Fact> userFacts;
    List<Fact> likedFacts;
    TabLayout tabLayout;
    SwipeRefreshLayout srProfile;
    public static final String KEY_PROFILE = "profile";
    public static final String KEY_LIST = "factsLiked";

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
        tabLayout = view.findViewById(R.id.tabProfile);
        srProfile = view.findViewById(R.id.srProfile);

        srProfile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            }
        });

        ParseUser user = ParseUser.getCurrentUser();

        // Bind data
        Glide.with(getContext()).load(user.getParseFile(KEY_PROFILE).getUrl()).circleCrop().into(ivProfPict);
        tvNameP.setText(user.getUsername());

        // Array initialized
        userFacts = new ArrayList<>();
        likedFacts = new ArrayList<>();

        // Adapter initialized
        adapter = new FactAdapter(getContext(), userFacts, true, this);
        likeAdapter = new FactAdapter(getContext(), likedFacts, false, this);

        // Assign adapter and layout manager
        rvUserFacts.setLayoutManager(new LinearLayoutManager(getContext()));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0){
                    queryMyFacts();
                }
                if (tabLayout.getSelectedTabPosition() == 1){
                    queryLikedFacts();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Default tab
        queryMyFacts();

    }

    private void queryLikedFacts() {
        rvUserFacts.setAdapter(likeAdapter);

        ParseQuery<Fact> query = ParseQuery.getQuery(Fact.class);
        query.include(Fact.KEY_USER);
        Log.i(TAG, "queryLikedFacts: " + ParseUser.getCurrentUser().getList("factsLiked"));
        query.whereContainedIn(Fact.KEY_OBJECT_ID, ParseUser.getCurrentUser().getList(KEY_LIST));
        query.setLimit(20);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Fact>() {
            @Override
            public void done(List<Fact> facts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting facts", e);
                    return;
                }
                likeAdapter.clearAll(true);
                likeAdapter.addAll(facts);

            }
        });
    }

    private void queryMyFacts() {
        rvUserFacts.setAdapter(adapter);
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
        deleteFact(position);
    }

    private void deleteFact(int position) {
        ParseQuery<Fact> query = ParseQuery.getQuery(Fact.class);
        query.whereEqualTo(Fact.KEY_OBJECT_ID, userFacts.get(position).getObjectId());
        query.findInBackground(new FindCallback<Fact>() {
            @Override
            public void done(List<Fact> objects, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting facts", e);
                    return;
                }
                objects.get(0).deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            Log.e(TAG, "Issue with deleting fact", e);
                            return;
                        }
                        userFacts.remove(position);
                        adapter.notifyDataSetChanged();
                        rvUserFacts.smoothScrollToPosition(0);
                    }
                });
            }
        });
    }

    @Override
    public void onLikeClick(int position) {
        ParseUser user = ParseUser.getCurrentUser();
        List<String> likeFacts= user.getList(KEY_LIST);

        if (tabLayout.getSelectedTabPosition() == 0){
            Fact fact = userFacts.get(position);
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
        if (tabLayout.getSelectedTabPosition() == 1){
            Fact fact = likedFacts.get(position);
            String objectID = fact.getObjectId();

            for (int i = 0; i < likeFacts.size(); i++){
                if (likeFacts.get(i).equals(objectID)){
                    Toast.makeText(getContext(), "Liked already", Toast.LENGTH_SHORT).show();
                    likeFacts.remove(i);
                    user.put(KEY_LIST, likeFacts);
                    user.saveInBackground();
                    queryLikedFacts();
                    break;
                } else if (i == likeFacts.size()-1){
                    Toast.makeText(getContext(), "Liking ...", Toast.LENGTH_SHORT).show();
                    likeFacts.add(0, fact.getObjectId());
                    user.put(KEY_LIST, likeFacts);
                    user.saveInBackground();
                    queryLikedFacts();
                    break;
                }
            }
        }
    }

}
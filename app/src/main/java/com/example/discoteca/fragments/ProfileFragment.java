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
import com.example.discoteca.EndlessScrolling;
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
    EndlessScrolling scrollListener;
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
                if (tabLayout.getSelectedTabPosition() == 0){
                    queryMyFacts(0, true);
                    srProfile.setRefreshing(false);
                }
                if (tabLayout.getSelectedTabPosition() == 1){
                    queryLikedFacts(0, true);
                    srProfile.setRefreshing(false);
                }
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        scrollListener = new EndlessScrolling(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                page = page * 10;
                loadNextData(page);
            }
        };

        // Assign scroll listener and layout manager
        rvUserFacts.setLayoutManager(linearLayoutManager);
        rvUserFacts.addOnScrollListener(scrollListener);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0){
                    queryMyFacts(0,true);
                }
                if (tabLayout.getSelectedTabPosition() == 1){
                    queryLikedFacts(0, true);
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
        queryMyFacts(0, true);

    }

    private void loadNextData(int page) {
        if (tabLayout.getSelectedTabPosition() == 0){
            queryMyFacts(page, false);
        }
        if (tabLayout.getSelectedTabPosition() == 1){
            queryLikedFacts(page, false);
        }
    }

    private void queryLikedFacts(int page, boolean clear) {
        rvUserFacts.setAdapter(likeAdapter);

        ParseQuery<Fact> query = ParseQuery.getQuery(Fact.class);
        query.include(Fact.KEY_USER);
        Log.i(TAG, "queryLikedFacts: " + ParseUser.getCurrentUser().getList("factsLiked"));
        query.whereContainedIn(Fact.KEY_OBJECT_ID, ParseUser.getCurrentUser().getList(KEY_LIST));
        query.setLimit(10);
        query.setSkip(page);
        query.findInBackground(new FindCallback<Fact>() {
            @Override
            public void done(List<Fact> facts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting facts", e);
                    return;
                }
                if (clear){
                    likeAdapter.clearAll(true);
                }
                likeAdapter.addAll(facts);

            }
        });
    }

    private void queryMyFacts(int page, boolean clear) {
        rvUserFacts.setAdapter(adapter);
        ParseQuery<Fact> query = ParseQuery.getQuery(Fact.class);
        query.include(Fact.KEY_USER);
        query.whereEqualTo(Fact.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(10);
        query.setSkip(page);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Fact>() {
            @Override
            public void done(List<Fact> facts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting facts", e);
                    return;
                }
                if (clear){
                    adapter.clearAll(true);
                }
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

        if (likeFacts.size() == 0){
            if (tabLayout.getSelectedTabPosition() == 0){
                Fact fact = userFacts.get(position);
                String objectID = fact.getObjectId();
                int likes = fact.getLikes();
                likeFact(true, likeFacts, fact, likes, user);
            }
        } else {
            if (tabLayout.getSelectedTabPosition() == 0){
                Fact fact = userFacts.get(position);
                String objectID = fact.getObjectId();
                int likes = fact.getLikes();

                for (int i = 0; i < likeFacts.size(); i++){
                    if (likeFacts.get(i).equals(objectID)){
                        dislikeFact(true, likeFacts,fact,likes,user,i);

                        break;
                    } else if (i == likeFacts.size()-1){
                        likeFact(true, likeFacts, fact, likes, user);
                        break;
                    }
                }
            }


                }
            }
        }
    }

    private void likeFact(boolean tab1, List<String> likeFacts, Fact fact, int likes, ParseUser user){
        Toast.makeText(getContext(), "Likes " + likes, Toast.LENGTH_SHORT).show();
        if (tab1){
            Toast.makeText(getContext(), "Liking ...", Toast.LENGTH_SHORT).show();
            likeFacts.add(0, fact.getObjectId());

            // Update number of likes on the fact
            likes = likes + 1;
            Toast.makeText(getContext(), "Likes " + likes, Toast.LENGTH_SHORT).show();
            fact.setLikes(likes);
            fact.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    user.put(KEY_LIST, likeFacts);
                    saveUser(user);
                }
            });
        } else {
            Toast.makeText(getContext(), "Liking ...", Toast.LENGTH_SHORT).show();
            likeFacts.add(0, fact.getObjectId());
            likes = likes + 1;
            fact.setLikes(likes);
            fact.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    user.put(KEY_LIST, likeFacts);
                    user.saveInBackground();
                    queryLikedFacts(0,true);
                }
            });
        }
    }

    private void dislikeFact(boolean tab1, List<String> likeFacts, Fact fact, int likes, ParseUser user, int i){
        if (tab1){
            Toast.makeText(getContext(), "Liked already", Toast.LENGTH_SHORT).show();
            likeFacts.remove(i);

            // Update number of likes on the fact
            likes = likes - 1;
            fact.setLikes(likes);
            fact.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    user.put(KEY_LIST, likeFacts);
                    saveUser(user);
                }
            });
        } else {
            Toast.makeText(getContext(), "Liked already", Toast.LENGTH_SHORT).show();
            likeFacts.remove(i);
            // Update number of likes on the fact
            likes = likes - 1;
            fact.setLikes(likes);
            fact.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    user.put(KEY_LIST, likeFacts);
                    user.saveInBackground();
                    queryLikedFacts(0, true);
                }
            });
        }
    }

    private void saveUser(ParseUser user){
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
    }

}
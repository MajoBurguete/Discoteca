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

import com.bumptech.glide.Glide;
import com.example.discoteca.R;
import com.example.discoteca.adapters.FactAdapter;
import com.example.discoteca.models.Fact;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends Fragment implements FactAdapter.OnFactClickListener {

    private static final String TAG = "UserProfileFragment";
    public static final String KEY_LIST = "factsLiked";
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

        queryFacts(true);

        //Swipe refresher
        srUserProfile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryFacts(true);
                srUserProfile.setRefreshing(false);
            }
        });

    }

    private void queryFacts(boolean clear) {

        ParseQuery<Fact> query = ParseQuery.getQuery(Fact.class);
        query.include(Fact.KEY_USER);
        query.whereEqualTo(Fact.KEY_USER, user);
        query.setLimit(10);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Fact>() {
            @Override
            public void done(List<Fact> facts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting facts", e);
                    return;
                }
                if (clear){
                   factAdapter.clearAll(true);
                }
                factAdapter.addAll(facts);

                // Set number of facts
                int value  = factAdapter.getItemCount();
                tvUserNumber.setText(String.valueOf(value));

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
        Fact fact = userFacts.get(position);
        String objectID = fact.getObjectId();
        int likes = fact.getLikes();
        int likeCheck = likeFacts.size();
        if (likeCheck == 0){
            likeFacts.add(0, fact.getObjectId());

            // Update number of likes on the fact
            likes = likes + 1;
            fact.setLikes(likes);
            fact.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    user.put(KEY_LIST, likeFacts);
                    saveUser(user);
                }
            });
        } else{
            for (int i = 0; i < likeFacts.size(); i++){
                if (likeFacts.get(i).equals(objectID)){
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

                    break;
                } else if (i == likeFacts.size()-1){
                    Toast.makeText(getContext(), "Liking ...", Toast.LENGTH_SHORT).show();
                    likeFacts.add(0, fact.getObjectId());

                    // Update number of likes on the fact
                    likes = likes + 1;
                    fact.setLikes(likes);
                    fact.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            user.put(KEY_LIST, likeFacts);
                            saveUser(user);
                        }
                    });

                    break;
                }
            }
        }
    }

    @Override
    public void onSongFactClick(int position) {

    private void saveUser(ParseUser user){
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with disliking facts", e);
                    return;
                }
                factAdapter.notifyDataSetChanged();
            }
        });
    }
}
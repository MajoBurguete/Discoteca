package com.example.discoteca.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.discoteca.R;
import com.example.discoteca.adapters.FactAdapter;
import com.example.discoteca.models.Fact;
import com.example.discoteca.models.Song;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends Fragment implements FactAdapter.OnFactClickListener {

    private static final String TAG = "UserProfileFragment";
    public static final String KEY_LIST = "factsLiked";
    private static final String FRIEND_NUM_KEY = "friendsNumber";
    private static final String FRIENDS_LIST_KEY = "friends";
    TextView tvUserName;
    ImageView ivOtherUserPict;
    ImageButton btnReturn;
    SwipeRefreshLayout srUserProfile;
    RecyclerView rvUserFacts;
    List<Fact> userFacts;
    FactAdapter factAdapter;
    TextView tvUserNumber;
    TextView tvFriendsNumber;
    Button btnFollow;
    ParseUser user;
    int removeAt;
    String returnTo;

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
        tvFriendsNumber = view.findViewById(R.id.tvFriendsNumber);
        btnFollow = view.findViewById(R.id.btnFollow);
        btnReturn = view.findViewById(R.id.btnBackFromP);

        // user gotten from arguments
        user = Parcels.unwrap(this.getArguments().getParcelable("user"));

        //String gotten from arguments
        returnTo = this.getArguments().getString("fragment");

        // Check if the current user follows this user
        boolean check = checkIfFriend();
        if (check){
            btnFollow.setText("Unfollow");
        }

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
        // Set number of facts
        long value  = user.getLong("factNumber");
        tvUserNumber.setText(String.valueOf(value));
        tvFriendsNumber.setText(String.valueOf(user.getLong(FRIEND_NUM_KEY)));

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });

        queryFacts(true);

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToW();
            }
        });

        //Swipe refresher
        srUserProfile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryFacts(true);
                srUserProfile.setRefreshing(false);
            }
        });

    }

    private void returnToW() {
        if (returnTo == "home"){
            Fragment fragmentNew = new HomeFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.left_in, R.anim.right_out);
            transaction.replace(R.id.flContainer, fragmentNew).commit();
        } else if (returnTo == "profile"){
            Fragment fragmentNew = new ProfileFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.left_in, R.anim.right_out);
            transaction.replace(R.id.flContainer, fragmentNew).commit();
        } else if (returnTo == "songFrag"){

            FragmentTransaction transaction = getParentFragment().getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.left_in, R.anim.right_out);
            transaction.remove(UserProfileFragment.this).commit();
        }
        else{
            getParentFragment().getChildFragmentManager().beginTransaction().remove(UserProfileFragment.this).commit();
        }
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

            }
        });
    }

    @Override
    public void onDeleteClick(int position) {

    }

    @Override
    public void onLikeClick(int position) {
        ParseUser user = ParseUser.getCurrentUser();
        List<Fact> likeFacts= user.getList(KEY_LIST);
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
        Fragment fragment = new SongFragment();

        // Pass song data to the detail fragment
        Bundle bundle = new Bundle();
        Fact fact = userFacts.get(position);

        Song song = new Song();
        song.setSongId(fact.getId());
        song.setSongName(fact.getSong());
        song.setAlbumName(fact.getAlbum());
        song.setArtistName(fact.getArtist());
        song.setImageUrl(fact.getUrl());
        bundle.putParcelable("song", Parcels.wrap(song));
        bundle.putString("fragment", "userP");
        fragment.setArguments(bundle);

        //Pass the user
        bundle.putParcelable("user", Parcels.wrap(user));

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
        transaction.replace(R.id.flContainer, fragment).commit();
    }

    @Override
    public void onUserClick(int position) {

    }

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

    public void addFriend(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<ParseUser> friends = currentUser.getList(FRIENDS_LIST_KEY);
        boolean boolFriend = checkIfFriend();

        if (boolFriend){
            friends.remove(removeAt);

            Long number = currentUser.getLong(FRIEND_NUM_KEY);
            number = number - 1;

            currentUser.put(FRIENDS_LIST_KEY, friends);
            currentUser.put(FRIEND_NUM_KEY, number);

            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    btnFollow.setText("Follow");
                }
            });

        }
        else{
            friends.add(user);

            Long number = currentUser.getLong(FRIEND_NUM_KEY);
            number = number + 1;

            currentUser.put(FRIENDS_LIST_KEY, friends);
            currentUser.put(FRIEND_NUM_KEY, number);

            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    btnFollow.setText("Unfollow");
                }
            });
        }
    }

    private boolean checkIfFriend(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<ParseUser> friends = currentUser.getList(FRIENDS_LIST_KEY);

        if (friends.size() == 0){
            return false;
        }
        else {
            for (int i = 0; i < friends.size(); i++){
                if (friends.get(i).getObjectId().equals(user.getObjectId())){
                    removeAt = i;
                    return true;
                } else if (i == friends.size()-1){
                    return false;
                }
            }
        }

        return false;
    }
}
package com.example.discoteca.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.discoteca.EndlessScrolling;
import com.example.discoteca.R;
import com.example.discoteca.adapters.UserAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment implements UserAdapter.OnUserListener{

    public FriendsFragment() {
        // Required empty public constructor
    }

    private static final String TAG = "FriendFragment";
    private static final String FRIENDS_LIST_KEY = "friends";
    private static final String FRIEND_NUM_KEY = "friendsNumber";
    RecyclerView rvUsers;
    List<ParseUser> userList;
    UserAdapter adapter;
    EndlessScrolling endlessScrolling;
    ImageButton ibReturn;
    int removeAt;
    ParseUser user;
    boolean currentProfile;
    ParseUser userProf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Layout references
        rvUsers = view.findViewById(R.id.rvFriends);
        ibReturn = view.findViewById(R.id.ibReturnProfile);

        // Get boolean from arguments
        currentProfile = this.getArguments().getBoolean("friends");

        if (!currentProfile){
            userProf = Parcels.unwrap(FriendsFragment.this.getArguments().getParcelable("user"));
        } else{
            userProf = ParseUser.getCurrentUser();
        }

        // Adapter is created
        userList = new ArrayList<>();
        adapter = new UserAdapter(getContext(), userList, this);

        // Layout manager is created
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // Infinite scrolling
        endlessScrolling = new EndlessScrolling(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                page = page * 10;
                loadMoreData(page);
            }
        };

        // Recycler view
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(linearLayoutManager);
        rvUsers.addOnScrollListener(endlessScrolling);

        ibReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnTo();
            }
        });

        queryFriends(true, 0);

    }

    private void returnTo(){
    }

    private void loadMoreData(int page){
        queryFriends(false, page);
    }

    private void queryFriends(boolean clear, int page){

        List<ParseUser> friends = getFriendsList();

        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereContainedIn("objectId", toObjectId(friends));
        query.setLimit(10);
        query.setSkip(page);
        query.orderByDescending("username");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting facts", e);
                    return;
                }
                if (currentProfile){
                    for (int i=0; i<users.size(); i++){
                        if(users.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                            users.remove(i);
                            break;
                        }
                    }
                }
                if (clear){
                    adapter.clearAll(false);
                }
                adapter.addAll(users);

            }
        });
    }

    private List<ParseUser> getFriendsList() {
        List<ParseUser> friends = userProf.getList(FRIENDS_LIST_KEY);
        return friends;
    }

    private List<ParseUser> getCurrentFriends() {
        List<ParseUser> friends = userProf.getList(FRIENDS_LIST_KEY);
        for (int i=0; i<friends.size(); i++){
            if(friends.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                friends.remove(i);
                break;
            }
        }
        return friends;
    }

    private List<String> toObjectId(List<ParseUser> friends){
        List<String> usersN = new ArrayList<>();
        for (ParseUser user: friends){
            usersN.add(user.getObjectId());
        }
        return usersN;
    }

    @Override
    public void onUserClick(int position) {}

    @Override
    public void onFollowClick(int position) {
        ParseUser currentUs = ParseUser.getCurrentUser();
        List<ParseUser> friends = getCurrentFriends();
        user = userList.get(position);

        boolean boolFriend = checkIfFriend(friends);

        if (boolFriend){
            friends.remove(removeAt);

            Long number = currentUs.getLong(FRIEND_NUM_KEY);
            number = number - 1;

            currentUser.put(FRIENDS_LIST_KEY, friends);
            currentUser.put(FRIEND_NUM_KEY, number);

        }
        else{
            friends.add(user);

            Long number = currentUser.getLong(FRIEND_NUM_KEY);
            number = number + 1;

            currentUser.put(FRIENDS_LIST_KEY, friends);
            currentUser.put(FRIEND_NUM_KEY, number);

        }

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private boolean checkIfFriend(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<ParseUser> friends = currentUser.getList(FRIENDS_LIST_KEY);

        for (int i = 0; i < friends.size(); i++){
            if (friends.get(i).getObjectId().equals(user.getObjectId())){
                removeAt = i;
                return true;
            } else if (i == friends.size()-1){
                return false;
            }
        }

        return false;
    }
}
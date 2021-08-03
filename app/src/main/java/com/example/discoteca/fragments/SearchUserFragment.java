package com.example.discoteca.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.discoteca.EndlessScrolling;
import com.example.discoteca.R;
import com.example.discoteca.adapters.UserAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class SearchUserFragment extends Fragment implements UserAdapter.OnUserListener {

    private static final String TAG = "SearchUserFragment";
    SearchView searchBar;
    RecyclerView rvUsers;
    List<ParseUser> userList;
    UserAdapter adapter;
    EndlessScrolling endlessScrolling;
    String searchQuery;
    FrameLayout flUserChild;


    public SearchUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Layout references
        searchBar = view.findViewById(R.id.svUsers);
        rvUsers = view.findViewById(R.id.rvUsers);
        flUserChild = view.findViewById(R.id.flUserChild);
        flUserChild.setVisibility(View.GONE);

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

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                queryUsers(query, true,0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()){
                    adapter.clearAll(true);
                } else {
                    searchQuery = newText;
                    queryUsers(newText, true,0);
                }
                return true;
            }
        });

    }

    private void loadMoreData(int page){
        queryUsers(searchQuery, false, page);
    }

    private void queryUsers(String search, boolean clear, int page){
        if (search.isEmpty()){
            return;
        }
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereContains("username", search);
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
                for (int i=0; i<users.size(); i++){
                    if(users.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                        users.remove(i);
                        break;
                    }
                }
                if (clear){
                    adapter.clearAll(false);
                }
                adapter.addAll(users);

            }
        });
    }

    @Override
    public void onUserClick(int position) {
        Fragment fragment = new UserProfileFragment();

        // Pass song data to the detail fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", Parcels.wrap(userList.get(position)));
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
        transaction.replace(R.id.flUserChild, fragment).commit();
        flUserChild.setVisibility(View.VISIBLE);

    }

    @Override
    public void onFollowClick(int position) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<ParseUser> friends = currentUser.getList(FRIENDS_LIST_KEY);
    }

}
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
import android.widget.Toast;

import com.example.discoteca.EndlessScrolling;
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
                queryFacts(0, true);
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
        queryFacts(0, true);

    }

    private void loadNextData(int page) {
        queryFacts(page,false);
    }

    private void queryFacts(int page, boolean clear) {
        ParseQuery<Fact> query = ParseQuery.getQuery(Fact.class);
        query.include(Fact.KEY_USER);
        List<ParseUser> usersF = ParseUser.getCurrentUser().getList("friends");
        usersF.add(ParseUser.getCurrentUser());
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
                    adapter.clearAll(false);
                }
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
        List<Fact> likeFacts= user.getList(KEY_LIST);
        Fact fact = factsL.get(position);
        String objectID = fact.getObjectId();
        int likes = fact.getLikes();
        Toast.makeText(getContext(), "Likes " + likes, Toast.LENGTH_SHORT).show();
        int likeCheck = likeFacts.size();
        if (likeCheck == 0){
            Toast.makeText(getContext(), "Liking ...", Toast.LENGTH_SHORT).show();
            likeFacts.add(0, fact);

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
                if (likeFacts.get(i).getObjectId().equals(objectID)){
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
                    likeFacts.add(0, fact);

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
        Fact fact = factsL.get(position);

        Song song = new Song();
        song.setSongId(fact.getId());
        song.setSongName(fact.getSong());
        song.setAlbumName(fact.getAlbum());
        song.setArtistName(fact.getArtist());
        song.setImageUrl(fact.getUrl());
        bundle.putParcelable("song", Parcels.wrap(song));
        bundle.putString("fragment", "home");
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
        transaction.replace(R.id.flContainer, fragment).commit();
    }

    @Override
    public void onUserClick(int position) {
        Fragment fragment = new UserProfileFragment();

        //Check the fact was not made by the current user
        Fact fact = factsL.get(position);
        if (!fact.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){

            // Pass user
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", Parcels.wrap(fact.getUser()));
            bundle.putString("fragment", "home");
            fragment.setArguments(bundle);

        }
        else {
            fragment = new ProfileFragment();
        }

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
        transaction.replace(R.id.flContainer, fragment).commit();
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

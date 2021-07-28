package com.example.discoteca.fragments;

import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.discoteca.EndlessScrolling;
import com.example.discoteca.R;
import com.example.discoteca.activities.ComposeActivity;
import com.example.discoteca.adapters.FactAdapter;
import com.example.discoteca.models.Fact;
import com.example.discoteca.models.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class SongFragment extends Fragment implements FactAdapter.OnFactClickListener{

    public static final String TAG = "SongFragment";
    public static final String KEY_LIST = "factsLiked";
    ImageView ivSongDetail;
    TextView tvNameDetail;
    TextView tvArtistSong;
    TextView tvSongAlbum;
    ImageButton ibClose;
    RecyclerView rvSongsFacts;
    FactAdapter adapter;
    List<Fact> songFacts;
    FloatingActionButton fabAdd;
    SwipeRefreshLayout srSongFacts;
    RelativeLayout rlSong;
    EndlessScrolling scrollListener;
    Song song;

    public SongFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Views references
        ivSongDetail = view.findViewById(R.id.ivSongDetail);
        tvNameDetail = view.findViewById(R.id.tvNameDetail);
        tvArtistSong  = view.findViewById(R.id.tvArtistSong);
        tvSongAlbum = view.findViewById(R.id.tvSongAlbum);
        ibClose = view.findViewById(R.id.ibCloseSong);
        rvSongsFacts = view.findViewById(R.id.rvSongFacts);
        fabAdd = view.findViewById(R.id.fabAdd);
        rlSong = view.findViewById(R.id.rlSong);
        srSongFacts = view.findViewById(R.id.srSongFacts);

        srSongFacts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryFacts();
                srSongFacts.setRefreshing(false);
            }
        });

        song = Parcels.unwrap(this.getArguments().getParcelable("song"));

        // Initialize fact array
        songFacts = new ArrayList<>();

        // Initialize adapter
        adapter = new FactAdapter(getContext(), songFacts, false, this);

        // Assign adapter, layout manager and scroll listener
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        scrollListener = new EndlessScrolling(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                page = page * 10;
                loadNextData(page);
            }
        };

        rvSongsFacts.setAdapter(adapter);
        rvSongsFacts.setLayoutManager(linearLayoutManager);
        rvSongsFacts.addOnScrollListener(scrollListener);

        // Close button
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlSong.setClickable(false);
                getParentFragment().getChildFragmentManager().beginTransaction().remove(SongFragment.this).commit();
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fact = new Intent(getActivity(), ComposeActivity.class);
                fact.putExtra("song", Parcels.wrap(song));
                startActivity(fact);
            }
        });

        // Bind song data into the layout
        Glide.with(getContext()).load(song.getImageUrl()).transform(new RoundedCorners(10)).into(ivSongDetail);
        tvNameDetail.setText(song.getSongName());
        tvArtistSong.setText(song.getArtistName());
        tvSongAlbum.setText(song.getAlbumName());

        queryFacts();
        
    }

    private void loadNextData(int page) {
        queryFacts(page, false);
    }

        ParseQuery<Fact> query = ParseQuery.getQuery(Fact.class);
        query.include(Fact.KEY_USER);
        query.whereEqualTo(Fact.KEY_ID, song.getSongId());
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
        Fact fact = songFacts.get(position);
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
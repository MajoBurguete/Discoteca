package com.example.discoteca.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.tabs.TabLayout;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment implements FactAdapter.OnFactClickListener{

    private String TAG = "ProfileFragment";
    ImageView ivProfPict;
    TextView tvNameP;
    TextView tvNumber;
    RecyclerView rvUserFacts;
    FactAdapter adapter;
    List<Fact> userFacts;
    TabLayout tabLayout;
    public static final String KEY_PROFILE = "profile";

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

        ParseUser user = ParseUser.getCurrentUser();

        // Bind data
        Glide.with(getContext()).load(user.getParseFile(KEY_PROFILE).getUrl()).circleCrop().into(ivProfPict);
        tvNameP.setText(user.getUsername());

        // Array initialized
        userFacts = new ArrayList<>();

        // Adapter initialized
        adapter = new FactAdapter(getContext(), userFacts, true, this);

        // Assign adapter and layout manager
        rvUserFacts.setAdapter(adapter);
        rvUserFacts.setLayoutManager(new LinearLayoutManager(getContext()));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0){
                    queryMyFacts();
                }
                if (tabLayout.getSelectedTabPosition() == 1){
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void queryMyFacts() {
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

}
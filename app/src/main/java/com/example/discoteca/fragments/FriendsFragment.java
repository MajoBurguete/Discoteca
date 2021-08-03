package com.example.discoteca.fragments;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
public class FriendsFragment extends Fragment implements UserAdapter.OnUserListener{

    public FriendsFragment() {
        // Required empty public constructor
    }

    private static final String TAG = "FriendFragment";
    RecyclerView rvUsers;
    ImageButton ibReturn;
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

    }
}
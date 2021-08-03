package com.example.discoteca.fragments;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.example.discoteca.adapters.UserAdapter;
import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment implements UserAdapter.OnUserListener{

    public FriendsFragment() {
        // Required empty public constructor
    }

    private static final String TAG = "FriendFragment";
    RecyclerView rvUsers;
    List<ParseUser> userList;
    UserAdapter adapter;
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

        // Get boolean from arguments
        currentProfile = this.getArguments().getBoolean("friends");

        // Adapter is created
        userList = new ArrayList<>();
        adapter = new UserAdapter(getContext(), userList, this);

        // Layout manager is created
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());


        // Recycler view
        rvUsers.setAdapter(adapter);
        rvUsers.setLayoutManager(linearLayoutManager);
        ibReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentProfile){
                    Fragment fragmentNew = new ProfileFragment();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.left_in, R.anim.right_out);
                    transaction.replace(R.id.flContainer, fragmentNew).commit();
                }
            }
        });

        queryFriends(true, 0);

    }
    private void queryFriends(boolean clear, int page){

        ParseUser currentUser = ParseUser.getCurrentUser();
        List<ParseUser> friends = currentUser.getList(FRIENDS_LIST_KEY);

        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
            }
        });

    }
}
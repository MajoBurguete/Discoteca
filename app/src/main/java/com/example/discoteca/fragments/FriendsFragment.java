package com.example.discoteca.fragments;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
        query.whereContainedIn("objectId", toObjectId(friends));
        query.setLimit(10);
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
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<ParseUser> friends = currentUser.getList(FRIENDS_LIST_KEY);
        user = userList.get(position);

        boolean boolFriend = checkIfFriend();

            }
        });
    }

    private boolean checkIfFriend(){

    }
}
package com.example.discoteca.fragments;
public class FriendsFragment extends Fragment implements UserAdapter.OnUserListener{

    public FriendsFragment() {
        // Required empty public constructor
    }

    private static final String TAG = "FriendFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
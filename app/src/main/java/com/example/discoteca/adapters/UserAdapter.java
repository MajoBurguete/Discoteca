package com.example.discoteca.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.discoteca.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseUser;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    List<ParseUser> rvUsers;
    OnUserListener onUserListener;

    public interface OnUserListener{
        void onUserClick(int position);
        void onFollowClick(int position);
    }

    public UserAdapter(Context context, List<ParseUser> rvUsers, OnUserListener onUserListener) {
        this.onUserListener = onUserListener;
        this.context = context;
        this.rvUsers = rvUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        ParseUser user = rvUsers.get(position);
        holder.bind(user);
    }

    public void clearAll(boolean notify){
        rvUsers.clear();
        if (notify){
            notifyDataSetChanged();
        }
    }

    public void addAll(List<ParseUser> users){
        rvUsers.addAll(users);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return rvUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlUserSearch;
        ImageView ivUserPict;
        TextView tvOtherUser;
        FloatingActionButton fabFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rlUserSearch = itemView.findViewById(R.id.rlUserSearch);
            ivUserPict = itemView.findViewById(R.id.ivUserProfPict);
            tvOtherUser = itemView.findViewById(R.id.tvOtherUser);
            fabFollow = itemView.findViewById(R.id.fabFollow);
        }

        public void bind(ParseUser user) {
            tvOtherUser.setText(user.getUsername());
            Glide.with(context).load(user.getParseFile("profile").getUrl()).circleCrop().into(ivUserPict);

            ParseUser currentUser = ParseUser.getCurrentUser();
            List<ParseUser> friends = currentUser.getList("friends");

            if (friends.size() == 0){
                fabFollow.setImageResource(R.drawable.ic_follow);
            }
            else {
                for (int i = 0; i < friends.size(); i++){
                    if (friends.get(i).getObjectId().equals(user.getObjectId())){
                        fabFollow.setImageResource(R.drawable.ic_check);
                        break;
                    }
                    else if (i == friends.size()-1){
                        fabFollow.setImageResource(R.drawable.ic_follow);
                    }
                }
            }

            rlUserSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUserListener.onUserClick(getAdapterPosition());
                }
            });
        }
    }
}

package com.example.discoteca.adapters;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.parse.ParseUser;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    List<ParseUser> rvUsers;

    public UserAdapter(Context context, List<ParseUser> rvUsers) {
        this.context = context;
        this.rvUsers = rvUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
    public void clearAll(boolean notify){
    }

    public void addAll(List<ParseUser> users){
    }

    @Override
    public int getItemCount() {
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
}

package com.example.discoteca.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discoteca.R;
import com.example.discoteca.Spotify;
import com.example.discoteca.models.Fact;
import com.example.discoteca.models.Song;
import java.util.List;
public class FactAdapter extends RecyclerView.Adapter<FactAdapter.ViewHolder> {

    private static final String TAG = "FactAdapter";
    private Context context;
    private Activity activity;
    List<Fact> rvFact;
    Spotify client;
    Song song;

    public FactAdapter(Context context, List<Fact> rvFact, Spotify client, Activity activity){
        this.context = context;
        this.rvFact = rvFact;
        this.client = client;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FactAdapter.ViewHolder holder, int position) {
        Fact fact = rvFact.get(position);
        holder.bind(fact);
    }

    @Override
    public int getItemCount() {
        return rvFact.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivAlbum;
        TextView tvSongT;
        TextView tvDescription;
        TextView tvSearchInfo;
        TextView tvUserFact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAlbum  = itemView.findViewById(R.id.ivAlbum);
            tvSongT = itemView.findViewById(R.id.tvSongT);
            tvSearchInfo = itemView.findViewById(R.id.tvSearchInfo);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUserFact = itemView.findViewById(R.id.tvUserFact);
        }
    }
}

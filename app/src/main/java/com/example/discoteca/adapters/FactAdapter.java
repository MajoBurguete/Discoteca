package com.example.discoteca.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.discoteca.R;
import com.example.discoteca.Spotify;
import com.example.discoteca.models.Album;
import com.example.discoteca.models.Fact;
import com.example.discoteca.models.Song;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FactAdapter extends RecyclerView.Adapter<FactAdapter.ViewHolder> {

    private static final String TAG = "FactAdapter";
    private Context context;
    List<Fact> rvFact;
    Song song;
    List<Song> songs;

    public FactAdapter(Context context, List<Fact> rvFact, List<Song> songs){
        this.context = context;
        this.rvFact = rvFact;
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FactAdapter.ViewHolder holder, int position) {
        song = songs.get(position);
        Fact fact = rvFact.get(position);
        holder.bind(fact);
    }

    public void clearAll(boolean notify){
        songs.clear();
        rvFact.clear();
        if (notify){
            notifyDataSetChanged();
        }
    }

    public void addAll(List<Fact> facts, List<Song> songList){
        songs.addAll(songList);
        rvFact.addAll(facts);
        notifyDataSetChanged();
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

        public void bind(Fact fact) {
            Glide.with(context).load(song.getImageUrl()).into(ivAlbum);
            tvSongT.setText(song.getSongName());
            String info = song.getAlbumName() + " - " + song.getArtistName();
            tvSearchInfo.setText(info);
            tvDescription.setText(fact.getDescription());
            String user = "@" + fact.getUser().getUsername();
            tvUserFact.setText(user);

        }

    }
}

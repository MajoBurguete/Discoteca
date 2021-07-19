package com.example.discoteca.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.discoteca.R;
import com.example.discoteca.models.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private Context context;
    List<Song> rvSongs;
    OnSongClickListener clickListener;

    public interface OnSongClickListener{
        void onSongClick(int position);
    }

    public SongAdapter(Context context, List<Song> rvSongs, OnSongClickListener songListener) {
        this.context = context;
        this.rvSongs = rvSongs;
        this.clickListener = songListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {
        Song song = rvSongs.get(position);
        holder.bind(song);
    }

    public void clearAll(boolean notify){
        rvSongs.clear();
        if (notify){
            notifyDataSetChanged();
        }
    }

    public void addAll(List<Song> songs){
        rvSongs.addAll(songs);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return rvSongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivSearchAlbum;
        TextView tvSearchSong;
        TextView tvSongInfo;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSearchAlbum = itemView.findViewById(R.id.ivSearchAlbum);
            tvSearchSong =itemView.findViewById(R.id.tvSearchTitle);
            tvSongInfo = itemView.findViewById(R.id.tvSearchInfo);
        }

        public void bind(Song song) {
            Glide.with(context).load(song.getImageUrl()).into(ivSearchAlbum);
            tvSearchSong.setText(song.getSongName());
            tvSongInfo.setText(song.getAlbumName() + " - " + song.getArtistName());
        }
    }
}

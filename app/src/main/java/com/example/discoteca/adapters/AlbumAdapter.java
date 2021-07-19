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
import com.example.discoteca.models.Album;
import com.example.discoteca.models.Song;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private Context context;
    List<Album> rvAlbums;

    public AlbumAdapter(Context context, List<Album> rvAlbums) {
        this.context = context;
        this.rvAlbums = rvAlbums;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.ViewHolder holder, int position) {
        Album album = rvAlbums.get(position);
        holder.bind(album);
    }

    public void clearAll(boolean notify){
        rvAlbums.clear();
        if (notify){
            notifyDataSetChanged();
        }
    }

    public void addAll(List<Album> albums){
        rvAlbums.addAll(albums);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return rvAlbums.size();
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

        public void bind(Album album) {
            Glide.with(context).load(album.getImageUrl()).into(ivSearchAlbum);
            tvSearchSong.setText(album.getAlbumName());
            tvSongInfo.setText(album.getArtistName());
        }
    }
}

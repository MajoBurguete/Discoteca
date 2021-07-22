package com.example.discoteca.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    }

    @Override
    public void onBindViewHolder(@NonNull FactAdapter.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

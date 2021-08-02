package com.example.discoteca.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.discoteca.R;
import com.example.discoteca.models.Fact;
import com.parse.ParseUser;

import java.util.List;

public class FactAdapter extends RecyclerView.Adapter<FactAdapter.ViewHolder> {

    private static final String TAG = "FactAdapter";
    private Context context;
    List<Fact> rvFact;
    boolean delete;
    OnFactClickListener onFactClickListener;

    public interface OnFactClickListener{
        void onDeleteClick(int position);
        void onLikeClick(int position);
        void onSongFactClick(int position);
        void onUserClick(int position);
    }


    public FactAdapter(Context context, List<Fact> rvFact, boolean delete, OnFactClickListener clickListener){
        this.context = context;
        this.rvFact = rvFact;
        this.delete = delete;
        this.onFactClickListener = clickListener;
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

    public void clearAll(boolean notify){
        rvFact.clear();
        if (notify){
            notifyDataSetChanged();
        }
    }

    public void addAll(List<Fact> facts){
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
        ImageButton ibDelete;
        ImageButton ibLike;
        TextView tvLikes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAlbum  = itemView.findViewById(R.id.ivAlbum);
            tvSongT = itemView.findViewById(R.id.tvSongT);
            tvSearchInfo = itemView.findViewById(R.id.tvSearchInfo);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUserFact = itemView.findViewById(R.id.tvUserFact);
            ibDelete = itemView.findViewById(R.id.ibDelete);
            ibLike = itemView.findViewById(R.id.ibLike);
            tvLikes = itemView.findViewById(R.id.tvLikes);
        }

        public void bind(Fact fact) {
            ParseUser user = ParseUser.getCurrentUser();
            List<Fact> likeFacts= user.getList("factsLiked");
            String objectID = fact.getObjectId();

            if (likeFacts.size() == 0){
                ibLike.setImageResource(R.drawable.ic_music_disliked);
            } else {
                for (int i = 0; i < likeFacts.size(); i++){
                    if (likeFacts.get(i).equals(objectID)){
                        ibLike.setImageResource(R.drawable.ic_music_liked);
                        break;
                    } else if(i == likeFacts.size()-1) {
                        ibLike.setImageResource(R.drawable.ic_music_disliked);
                    }
                }
            }

            if (delete){
                ibDelete.setVisibility(View.VISIBLE);
            }
            Glide.with(context).load(fact.getUrl()).into(ivAlbum);
            tvSongT.setText(fact.getSong());
            String info = fact.getAlbum() + " - " + fact.getArtist();
            tvSearchInfo.setText(info);
            tvDescription.setText(fact.getDescription());
            String userSt = "@" + fact.getUser().getUsername();
            tvUserFact.setText(userSt);
            tvLikes.setText(String.valueOf(fact.getLikes()));

            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFactClickListener.onDeleteClick(getAdapterPosition());
                }
            });

            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFactClickListener.onLikeClick(getAdapterPosition());
                }
            });

            ivAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFactClickListener.onSongFactClick(getAdapterPosition());
                }
            });

            tvUserFact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFactClickListener.onUserClick(getAdapterPosition());
                }
            });

        }

    }

}

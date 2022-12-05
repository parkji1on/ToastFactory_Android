package com.example.tykoon.rank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tykoon.R;
import com.example.tykoon.retrofit.model.UserRank;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>{
    private List<UserRank> userRanks;

    @NonNull
    @Override
    public MyRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerAdapter.ViewHolder holder, int position) {
        holder.onBind(userRanks.get(position));
    }

    public void setUserRanks(List<UserRank> list){
        this.userRanks = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userRanks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView rank;
        TextView name;
        TextView score;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rank = (TextView) itemView.findViewById(R.id.rank_ect_rank);
            name = (TextView) itemView.findViewById(R.id.rank_ect_name);
            score = (TextView) itemView.findViewById(R.id.rank_ect_score);
        }

        void onBind(UserRank item){
            rank.setText(String.valueOf(item.getRank()));
            name.setText(item.getName());
            score.setText(String.valueOf(item.getScore()));
        }
    }
}

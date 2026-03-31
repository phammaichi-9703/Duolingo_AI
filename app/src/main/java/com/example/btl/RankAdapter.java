package com.example.btl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder> {

    private List<UserRank> rankList;

    public RankAdapter(List<UserRank> rankList) {
        this.rankList = rankList;
    }

    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank, parent, false);
        return new RankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankViewHolder holder, int position) {
        UserRank user = rankList.get(position);
        holder.tvRank.setText(String.valueOf(user.getRank()));
        holder.tvName.setText(user.getName());
        holder.tvXp.setText(user.getXp() + " XP");
        
        // Style top 3 differently if needed
        if (user.getRank() == 1) holder.tvRank.setTextColor(0xFFFFD700); // Gold
        else if (user.getRank() == 2) holder.tvRank.setTextColor(0xFFC0C0C0); // Silver
        else if (user.getRank() == 3) holder.tvRank.setTextColor(0xFFCD7F32); // Bronze
    }

    @Override
    public int getItemCount() {
        return rankList.size();
    }

    static class RankViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvXp;

        public RankViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvName = itemView.findViewById(R.id.tvName);
            tvXp = itemView.findViewById(R.id.tvXp);
        }
    }
}

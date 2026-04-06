package com.example.btl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.VH> {

    private final List<RankItem> data;

    public RankAdapter(List<RankItem> data) {
        this.data = data;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvXp;
        VH(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvName = itemView.findViewById(R.id.tvName);
            tvXp = itemView.findViewById(R.id.tvXp);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        RankItem item = data.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvName.setText(item.name);
        holder.tvXp.setText(item.xp + " XP");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class RankItem {
        String name;
        int xp;
        public RankItem(String name, int xp) {
            this.name = name;
            this.xp = xp;
        }
    }
}

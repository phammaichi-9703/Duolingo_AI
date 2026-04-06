package com.example.btl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.VH> {

    private final List<Lesson> data;
    private final OnLessonClickListener listener;

    public interface OnLessonClickListener {
        void onLessonRangeClick(Lesson lesson);
    }

    public LessonAdapter(List<Lesson> data, OnLessonClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvXp, tvProgressValue;
        LinearProgressIndicator progress;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvLessonTitle);
            tvDesc = itemView.findViewById(R.id.tvLessonDesc);
            tvXp = itemView.findViewById(R.id.tvXp);
            tvProgressValue = itemView.findViewById(R.id.tvProgressValue);
            progress = itemView.findViewById(R.id.progress);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Lesson item = data.get(position);
        h.tvTitle.setText(item.title);
        h.tvDesc.setText(item.desc);
        h.tvXp.setText("+" + item.xpValue + " XP");
        h.tvProgressValue.setText(item.progress + "%");
        h.progress.setProgress(item.progress);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLessonRangeClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

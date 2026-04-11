package com.example.btn_duolingo;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<Exercise> exerciseList;
    private List<Boolean> lockStatus; // true if locked
    private OnExerciseClickListener listener;

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise, boolean isLocked);
    }

    public ExerciseAdapter(List<Exercise> exerciseList, List<Boolean> lockStatus, OnExerciseClickListener listener) {
        this.exerciseList = exerciseList;
        this.lockStatus = lockStatus;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        boolean isLocked = lockStatus.get(position);

        holder.tvTitle.setText(exercise.getTitle());
        holder.tvDescription.setText(exercise.getDescription());

        if (isLocked) {
            holder.btnStart.setText("LOCKED");
            holder.btnStart.setBackgroundColor(Color.GRAY);
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.btnStart.setText("START");
            holder.btnStart.setBackgroundColor(Color.parseColor("#58CC02")); // Duolingo green
            holder.itemView.setAlpha(1.0f);
        }

        holder.btnStart.setOnClickListener(v -> listener.onExerciseClick(exercise, isLocked));
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        MaterialButton btnStart;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvExerciseTitle);
            tvDescription = itemView.findViewById(R.id.tvExerciseDescription);
            btnStart = itemView.findViewById(R.id.btnStart);
        }
    }
}

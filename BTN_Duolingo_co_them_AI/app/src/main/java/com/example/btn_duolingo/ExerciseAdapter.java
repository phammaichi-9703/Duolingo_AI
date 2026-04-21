package com.example.btn_duolingo;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_EXERCISE = 0;
    private static final int TYPE_CUSTOM_TOPIC = 1;

    private List<Exercise> exerciseList;
    private List<Boolean> lockStatus; // true if locked
    private OnExerciseClickListener listener;

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise, boolean isLocked);
        void onCustomTopicClick(String topic);
    }

    public ExerciseAdapter(List<Exercise> exerciseList, List<Boolean> lockStatus, OnExerciseClickListener listener) {
        this.exerciseList = exerciseList;
        this.lockStatus = lockStatus;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < exerciseList.size()) {
            return TYPE_EXERCISE;
        } else {
            return TYPE_CUSTOM_TOPIC;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_EXERCISE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
            return new ExerciseViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_topic, parent, false);
            return new CustomTopicViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_EXERCISE) {
            ExerciseViewHolder exerciseHolder = (ExerciseViewHolder) holder;
            Exercise exercise = exerciseList.get(position);
            boolean isLocked = lockStatus.get(position);

            exerciseHolder.tvTitle.setText(exercise.getTitle());
            exerciseHolder.tvDescription.setText(exercise.getDescription());

            if (isLocked) {
                exerciseHolder.btnStart.setText("LOCKED");
                exerciseHolder.btnStart.setBackgroundColor(Color.GRAY);
                exerciseHolder.itemView.setAlpha(0.5f);
            } else {
                exerciseHolder.btnStart.setText("START");
                exerciseHolder.btnStart.setBackgroundColor(Color.parseColor("#58CC02")); // Duolingo green
                exerciseHolder.itemView.setAlpha(1.0f);
            }

            exerciseHolder.btnStart.setOnClickListener(v -> listener.onExerciseClick(exercise, isLocked));
        } else {
            CustomTopicViewHolder customHolder = (CustomTopicViewHolder) holder;
            // Chỉ cho phép dùng chủ đề tự chọn nếu bài cuối cùng đã hoàn thành
            boolean lastLessonCompleted = !lockStatus.get(lockStatus.size() - 1); 
            // Hoặc có thể cho phép dùng luôn. Theo yêu cầu là "Sau khi hết bài số 5"
            
            customHolder.btnStartCustom.setOnClickListener(v -> {
                String topic = customHolder.etCustomTopic.getText().toString().trim();
                if (topic.isEmpty()) {
                    Toast.makeText(v.getContext(), "Vui lòng nhập chủ đề!", Toast.LENGTH_SHORT).show();
                } else {
                    listener.onCustomTopicClick(topic);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        // Thêm 1 cho thẻ Custom Topic
        return exerciseList.size() + 1;
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

    static class CustomTopicViewHolder extends RecyclerView.ViewHolder {
        EditText etCustomTopic;
        MaterialButton btnStartCustom;

        public CustomTopicViewHolder(@NonNull View itemView) {
            super(itemView);
            etCustomTopic = itemView.findViewById(R.id.etCustomTopic);
            btnStartCustom = itemView.findViewById(R.id.btnStartCustom);
        }
    }
}

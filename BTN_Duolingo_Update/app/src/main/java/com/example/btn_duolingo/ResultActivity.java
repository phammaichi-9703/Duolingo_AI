package com.example.btn_duolingo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private TextView tvScore;
    private RecyclerView rvWrongAnswers;
    private List<Exercise> wrongExercises;
    private int totalQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvScore = findViewById(R.id.tvScore);
        rvWrongAnswers = findViewById(R.id.rvWrongAnswers);

        String wrongJson = getIntent().getStringExtra("WRONG_EXERCISES");
        totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);

        Gson gson = new Gson();
        Type type = new TypeToken<List<Exercise>>() {}.getType();
        wrongExercises = gson.fromJson(wrongJson, type);

        int correctCount = totalQuestions - (wrongExercises != null ? wrongExercises.size() : 0);
        tvScore.setText("You got " + correctCount + "/" + totalQuestions + " correct");

        if (wrongExercises == null || wrongExercises.isEmpty()) {
            findViewById(R.id.tvWrongTitle).setVisibility(View.GONE);
            rvWrongAnswers.setVisibility(View.GONE);
        } else {
            rvWrongAnswers.setLayoutManager(new LinearLayoutManager(this));
            rvWrongAnswers.setAdapter(new WrongAnswerAdapter(wrongExercises));
        }

        findViewById(R.id.btnFinish).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private static class WrongAnswerAdapter extends RecyclerView.Adapter<WrongAnswerAdapter.ViewHolder> {
        private final List<Exercise> exercises;

        WrongAnswerAdapter(List<Exercise> exercises) {
            this.exercises = exercises;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wrong_answer, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Exercise exercise = exercises.get(position);
            holder.tvQuestion.setText(exercise.getQuestion());
            holder.tvCorrectAnswer.setText("Correct Answer: " + exercise.getAnswer());
        }

        @Override
        public int getItemCount() {
            return exercises.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvQuestion, tvCorrectAnswer;

            ViewHolder(View itemView) {
                super(itemView);
                tvQuestion = itemView.findViewById(R.id.tvWrongQuestion);
                tvCorrectAnswer = itemView.findViewById(R.id.tvCorrectAnswer);
            }
        }
    }
}

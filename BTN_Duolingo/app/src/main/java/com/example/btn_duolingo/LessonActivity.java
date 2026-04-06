package com.example.btn_duolingo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LessonActivity extends AppCompatActivity {

    private TextView tvQuestion, tvFeedback, tvProgress;
    private ChipGroup chipGroupOptions, chipGroupAnswer;
    private Button btnCheck;
    private LinearProgressIndicator lessonProgress;
    private List<Exercise> exerciseList;
    private int currentIndex = 0;
    private DatabaseHelper dbHelper;
    private List<Exercise> wrongExercises = new ArrayList<>();
    private boolean isAnswerCorrect = false;
    private String lessonTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        tvQuestion = findViewById(R.id.tvSentenceToTranslate);
        tvFeedback = findViewById(R.id.tvFeedback);
        tvProgress = findViewById(R.id.tvProgress);
        lessonProgress = findViewById(R.id.lessonProgress);
        chipGroupOptions = findViewById(R.id.chipGroupOptions);
        chipGroupAnswer = findViewById(R.id.chipGroupAnswer);
        btnCheck = findViewById(R.id.btnCheckAnswer);
        dbHelper = new DatabaseHelper(this);

        lessonTitle = getIntent().getStringExtra("EX_TITLE");
        exerciseList = dbHelper.getExercisesByTitle(lessonTitle);

        if (exerciseList != null && !exerciseList.isEmpty()) {
            if (lessonProgress != null) {
                lessonProgress.setMax(exerciseList.size());
            }
            displayExercise();
        } else {
            Toast.makeText(this, "Không có câu hỏi nào!", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnCheck.setOnClickListener(v -> {
            if (btnCheck.getText().toString().equals("CONTINUE")) {
                currentIndex++;
                if (currentIndex < exerciseList.size()) {
                    displayExercise();
                } else {
                    showResult();
                }
            } else {
                checkAnswer();
            }
        });

        findViewById(R.id.btnCloseLesson).setOnClickListener(v -> finish());
    }

    private void displayExercise() {
        isAnswerCorrect = false;
        Exercise currentExercise = exerciseList.get(currentIndex);
        
        if (tvProgress != null) {
            tvProgress.setText((currentIndex + 1) + " / " + exerciseList.size());
        }
        
        if (lessonProgress != null) {
            lessonProgress.setProgress(currentIndex + 1, true);
        }

        tvQuestion.setText(currentExercise.getQuestion());
        tvFeedback.setVisibility(View.INVISIBLE);
        btnCheck.setText("CHECK");
        btnCheck.setBackgroundColor(Color.parseColor("#E5E5E5"));
        
        chipGroupAnswer.removeAllViews();
        chipGroupOptions.removeAllViews();

        String[] options = currentExercise.getOptions().split("\\|");
        List<String> optionList = new ArrayList<>();
        Collections.addAll(optionList, options);
        Collections.shuffle(optionList);

        for (String word : optionList) {
            addOptionChip(word);
        }
    }

    private void addOptionChip(String word) {
        Chip chip = new Chip(this);
        chip.setText(word);
        chip.setOnClickListener(v -> {
            chipGroupOptions.removeView(chip);
            addAnswerChip(word);
        });
        chipGroupOptions.addView(chip);
    }

    private void addAnswerChip(String word) {
        Chip chip = new Chip(this);
        chip.setText(word);
        chip.setOnClickListener(v -> {
            chipGroupAnswer.removeView(chip);
            addOptionChip(word);
        });
        chipGroupAnswer.addView(chip);
    }

    private void checkAnswer() {
        StringBuilder userAsnwer = new StringBuilder();
        for (int i = 0; i < chipGroupAnswer.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupAnswer.getChildAt(i);
            userAsnwer.append(chip.getText().toString());
            if (i < chipGroupAnswer.getChildCount() - 1) {
                userAsnwer.append(" ");
            }
        }

        String correctAnswer = exerciseList.get(currentIndex).getAnswer();
        tvFeedback.setVisibility(View.VISIBLE);
        
        if (userAsnwer.toString().trim().equals(correctAnswer.trim())) {
            isAnswerCorrect = true;
            tvFeedback.setText("Chính xác! Làm tốt lắm.");
            tvFeedback.setTextColor(Color.parseColor("#58CC02"));
            btnCheck.setBackgroundColor(Color.parseColor("#58CC02"));
            btnCheck.setText("CONTINUE");
        } else {
            if (!wrongExercises.contains(exerciseList.get(currentIndex))) {
                wrongExercises.add(exerciseList.get(currentIndex));
            }
            tvFeedback.setText("Chưa đúng. Thử lại nhé!");
            tvFeedback.setTextColor(Color.RED);
            Toast.makeText(this, "Đáp án đúng: " + correctAnswer, Toast.LENGTH_SHORT).show();
        }
    }

    private void showResult() {
        // Mark as completed if no wrong answers (or adjust logic as needed)
        if (wrongExercises.isEmpty()) {
            SharedPreferences sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            String username = sharedPref.getString("current_username", "");
            if (!username.isEmpty()) {
                dbHelper.markLessonCompleted(username, lessonTitle);
            }
        }

        Intent intent = new Intent(this, ResultActivity.class);
        String wrongJson = new Gson().toJson(wrongExercises);
        intent.putExtra("WRONG_EXERCISES", wrongJson);
        intent.putExtra("TOTAL_QUESTIONS", exerciseList.size());
        startActivity(intent);
        finish();
    }
}

package com.example.btn_duolingo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LessonActivity extends AppCompatActivity {

    private static final String TAG = "LessonActivity";
    private TextView tvQuestion, tvFeedback, tvProgress;
    private ChipGroup chipGroupOptions, chipGroupAnswer;
    private Button btnCheck;
    private LinearProgressIndicator lessonProgress;
    private List<Exercise> exerciseList;
    private int currentIndex = 0;
    private List<Exercise> wrongExercises = new ArrayList<>();
    private boolean isAnswerCorrect = false;
    private String lessonTitle;
    private String currentLanguage = "English";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        tvQuestion = findViewById(R.id.tvSentenceToTranslate);
        tvFeedback = findViewById(R.id.tvFeedback);
        tvProgress = findViewById(R.id.tvProgress);
        lessonProgress = findViewById(R.id.lessonProgress);
        chipGroupOptions = findViewById(R.id.chipGroupOptions);
        chipGroupAnswer = findViewById(R.id.chipGroupAnswer);
        btnCheck = findViewById(R.id.btnCheckAnswer);

        lessonTitle = getIntent().getStringExtra("EX_TITLE");
        currentLanguage = getIntent().getStringExtra("LANGUAGE");
        if (currentLanguage == null) currentLanguage = "English";
        
        Log.d(TAG, "Loading lesson: " + lessonTitle + " for language: " + currentLanguage);
        
        loadExercisesFromFirebase();

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

    private void loadExercisesFromFirebase() {
        String nodeName = currentLanguage.equals("Chinese") ? "exercises_cn" : "exercises";
        
        mDatabase.child(nodeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isFinishing() || isDestroyed()) return;

                exerciseList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Exercise ex = data.getValue(Exercise.class);
                    if (ex != null && lessonTitle != null && lessonTitle.equals(ex.getTitle())) {
                        exerciseList.add(ex);
                    }
                }

                Log.d(TAG, "Exercises loaded for " + lessonTitle + ": " + exerciseList.size());

                if (!exerciseList.isEmpty()) {
                    if (lessonProgress != null) {
                        lessonProgress.setMax(exerciseList.size());
                    }
                    displayExercise();
                } else {
                    Toast.makeText(LessonActivity.this, "Không có câu hỏi nào cho bài học này!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isFinishing() || isDestroyed()) return;
                Log.e(TAG, "Error loading exercises: " + error.getMessage());
                Toast.makeText(LessonActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayExercise() {
        if (isFinishing() || isDestroyed()) return;
        
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

        if (currentExercise.getOptions() != null) {
            String[] options = currentExercise.getOptions().split("\\|");
            List<String> optionList = new ArrayList<>();
            Collections.addAll(optionList, options);
            Collections.shuffle(optionList);

            for (String word : optionList) {
                addOptionChip(word);
            }
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
        
        if (correctAnswer != null && userAsnwer.toString().trim().equals(correctAnswer.trim())) {
            isAnswerCorrect = true;
            tvFeedback.setText("Chính xác! Làm tốt lắm.");
            tvFeedback.setTextColor(Color.parseColor("#58CC02"));
            btnCheck.setBackgroundColor(Color.parseColor("#58CC02"));
            btnCheck.setText("CONTINUE");

            addXPFirebase(5);
        } else {
            if (!wrongExercises.contains(exerciseList.get(currentIndex))) {
                wrongExercises.add(exerciseList.get(currentIndex));
            }
            tvFeedback.setText("Chưa đúng. Thử lại nhé!");
            tvFeedback.setTextColor(Color.RED);
            Toast.makeText(this, "Đáp án đúng: " + correctAnswer, Toast.LENGTH_SHORT).show();
        }
    }

    private void addXPFirebase(int amount) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            mDatabase.child("users").child(userId).child("xp").runTransaction(new com.google.firebase.database.Transaction.Handler() {
                @NonNull
                @Override
                public com.google.firebase.database.Transaction.Result doTransaction(@NonNull com.google.firebase.database.MutableData currentData) {
                    Integer currentXP = currentData.getValue(Integer.class);
                    if (currentXP == null) {
                        currentData.setValue(amount);
                    } else {
                        currentData.setValue(currentXP + amount);
                    }
                    return com.google.firebase.database.Transaction.success(currentData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {}
            });
        }
    }

    private void showResult() {
        if (isFinishing() || isDestroyed()) return;

        if (wrongExercises.isEmpty() && mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            mDatabase.child("user_progress").child(userId).child(lessonTitle).setValue(true);
        }

        Intent intent = new Intent(this, ResultActivity.class);
        String wrongJson = new Gson().toJson(wrongExercises);
        intent.putExtra("WRONG_EXERCISES", wrongJson);
        intent.putExtra("TOTAL_QUESTIONS", exerciseList.size());
        startActivity(intent);
        finish();
    }
}

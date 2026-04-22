
package com.example.btn_duolingo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import java.util.Locale;

public class LessonActivity extends AppCompatActivity {

    private static final String TAG = "LessonActivity";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    
    private TextView tvQuestion, tvFeedback, tvProgress;
    private ChipGroup chipGroupOptions, chipGroupAnswer;
    private Button btnCheck, btnRecord, btnPlayRecord;
    private LinearProgressIndicator lessonProgress;
    private ProgressBar loadingBar;
    private List<Exercise> exerciseList;
    private int currentIndex = 0;
    private List<Exercise> wrongExercises = new ArrayList<>();
    private String lessonTitle;
    private String currentLanguage = "English";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private GeminiService geminiService;

    private ActivityResultLauncher<Intent> speechResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        geminiService = new GeminiService();

        tvQuestion = findViewById(R.id.tvSentenceToTranslate);
        tvFeedback = findViewById(R.id.tvFeedback);
        tvProgress = findViewById(R.id.tvProgress);
        lessonProgress = findViewById(R.id.lessonProgress);
        chipGroupOptions = findViewById(R.id.chipGroupOptions);
        chipGroupAnswer = findViewById(R.id.chipGroupAnswer);
        btnCheck = findViewById(R.id.btnCheckAnswer);
        loadingBar = findViewById(R.id.loadingBar);
        btnRecord = findViewById(R.id.btnRecord);
        btnPlayRecord = findViewById(R.id.btnPlayRecord);
        
        btnRecord.setText("Speak");
        btnPlayRecord.setVisibility(View.GONE);

        lessonTitle = getIntent().getStringExtra("EX_TITLE");
        currentLanguage = getIntent().getStringExtra("LANGUAGE");
        if (currentLanguage == null) currentLanguage = "English";
        
        setupSpeechLauncher();
        checkAndLoadExercises();

        btnRecord.setOnClickListener(v -> startSpeechToText());

        btnCheck.setOnClickListener(v -> {
            if (btnCheck.getText().toString().equals("CONTINUE")) {
                currentIndex++;
                if (currentIndex < exerciseList.size()) {
                    displayExercise();
                } else {
                    showResult();
                }
            } else if (btnCheck.getText().toString().equals("TRY AGAIN")) {
                checkAndLoadExercises();
            } else {
                checkAnswer();
            }
        });

        findViewById(R.id.btnCloseLesson).setOnClickListener(v -> finish());
    }

    private void setupSpeechLauncher() {
        speechResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (matches != null && !matches.isEmpty()) {
                            String recognizedText = matches.get(0);
                            Log.d(TAG, "Speech result: " + recognizedText);
                            
                            tvFeedback.setVisibility(View.INVISIBLE);
                            
                            compareSpeechLocally(recognizedText);
                        }
                    }
                }
        );
    }

    private void compareSpeechLocally(String spokenText) {
        if (exerciseList == null || currentIndex >= exerciseList.size()) return;
        
        String questionText = exerciseList.get(currentIndex).getQuestion().toLowerCase().trim();
        String spokenTextLower = spokenText.toLowerCase().trim();
        
        double similarity = calculateSimilarity(questionText, spokenTextLower);
        String message = (similarity >= 0.3) ? "Bạn nói hay! ★★★★★" : "Cần nói lại.";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private double calculateSimilarity(String s1, String s2) {
        String[] words1 = s1.split("\\s+");
        String[] words2 = s2.split("\\s+");
        int matchCount = 0;
        for (String w1 : words1) {
            for (String w2 : words2) {
                if (w1.equals(w2)) {
                    matchCount++;
                    break; 
                }
            }
        }
        return (double) matchCount / Math.max(words1.length, words2.length);
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentLanguage.equals("Chinese") ? "zh-CN" : "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Đang nghe...");
        try {
            speechResultLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi động Voice", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAndLoadExercises() {
        if (loadingBar != null) loadingBar.setVisibility(View.VISIBLE);
        btnCheck.setEnabled(false);
        String nodeName = currentLanguage.equals("Chinese") ? "exercises_cn" : "exercises";
        mDatabase.child(nodeName).orderByChild("title").equalTo(lessonTitle)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        loadExercisesFromFirebase(snapshot);
                    } else {
                        loadExercisesFromGemini();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loadExercisesFromGemini();
                }
            });
    }

    private void loadExercisesFromFirebase(DataSnapshot snapshot) {
        exerciseList = new ArrayList<>();
        for (DataSnapshot data : snapshot.getChildren()) {
            Exercise ex = data.getValue(Exercise.class);
            if (ex != null) exerciseList.add(ex);
        }
        runOnUiThread(() -> {
            if (loadingBar != null) loadingBar.setVisibility(View.GONE);
            btnCheck.setEnabled(true);
            if (!exerciseList.isEmpty()) {
                lessonProgress.setMax(exerciseList.size());
                displayExercise();
            } else {
                loadExercisesFromGemini();
            }
        });
    }

    private void loadExercisesFromGemini() {
        if (loadingBar != null) loadingBar.setVisibility(View.VISIBLE);
        btnCheck.setEnabled(false);
        tvQuestion.setText("Generating fresh exercises with AI...");
        geminiService.generateExercises(currentLanguage, lessonTitle, new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(List<Exercise> exercises) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    if (loadingBar != null) loadingBar.setVisibility(View.GONE);
                    btnCheck.setEnabled(true);
                    exerciseList = exercises;
                    if (exerciseList != null && !exerciseList.isEmpty()) {
                        lessonProgress.setMax(exerciseList.size());
                        displayExercise();
                    }
                });
            }
            @Override
            public void onError(Throwable t) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    if (loadingBar != null) loadingBar.setVisibility(View.GONE);
                    btnCheck.setEnabled(true);
                    tvQuestion.setText("Failed to generate exercises. Check your API Key.");
                });
            }
        });
    }

    private void displayExercise() {
        if (isFinishing() || isDestroyed() || exerciseList == null || exerciseList.isEmpty()) return;
        Exercise currentExercise = exerciseList.get(currentIndex);
        tvProgress.setText((currentIndex + 1) + " / " + exerciseList.size());
        lessonProgress.setProgress(currentIndex + 1, true);
        tvQuestion.setText(currentExercise.getQuestion());
        tvFeedback.setVisibility(View.INVISIBLE);
        btnCheck.setText("CHECK");
        btnCheck.setBackgroundColor(ContextCompat.getColor(this, R.color.duolingo_green));
        btnCheck.setEnabled(true);
        chipGroupAnswer.removeAllViews();
        chipGroupOptions.removeAllViews();
        if (currentExercise.getOptions() != null) {
            String[] options = currentExercise.getOptions().split("\\|");
            List<String> optionList = new ArrayList<>();
            for (String s : options) optionList.add(s.trim());
            Collections.shuffle(optionList);
            for (String word : optionList) addOptionChip(word);
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
        if (exerciseList == null || currentIndex >= exerciseList.size()) return;
        StringBuilder userAsnwer = new StringBuilder();
        for (int i = 0; i < chipGroupAnswer.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupAnswer.getChildAt(i);
            userAsnwer.append(chip.getText().toString());
            if (i < chipGroupAnswer.getChildCount() - 1) userAsnwer.append(" ");
        }
        String correctAnswer = exerciseList.get(currentIndex).getAnswer().trim();
        tvFeedback.setVisibility(View.VISIBLE);
        if (userAsnwer.toString().trim().equalsIgnoreCase(correctAnswer)) {
            tvFeedback.setText("Chính xác! Làm tốt lắm.");
            tvFeedback.setTextColor(ContextCompat.getColor(this, R.color.duolingo_green));
            btnCheck.setText("CONTINUE");
            addXPFirebase(5);
        } else {
            if (!wrongExercises.contains(exerciseList.get(currentIndex))) wrongExercises.add(exerciseList.get(currentIndex));
            tvFeedback.setText("Chưa đúng. Đáp án: " + correctAnswer);
            tvFeedback.setTextColor(ContextCompat.getColor(this, R.color.duolingo_red));
        }
    }

    private void addXPFirebase(int amount) {
        String userId = mAuth.getUid();
        if (userId != null) {
            mDatabase.child("users").child(userId).child("xp").runTransaction(new com.google.firebase.database.Transaction.Handler() {
                @NonNull
                @Override
                public com.google.firebase.database.Transaction.Result doTransaction(@NonNull com.google.firebase.database.MutableData currentData) {
                    Integer currentXP = currentData.getValue(Integer.class);
                    currentData.setValue((currentXP == null ? 0 : currentXP) + amount);
                    return com.google.firebase.database.Transaction.success(currentData);
                }
                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {}
            });
        }
    }

    private void showResult() {
        if (isFinishing() || isDestroyed()) return;
        
        // Luôn lưu tiến trình khi hoàn thành bài học, không cần phải đúng hết 100%
        if (mAuth.getCurrentUser() != null) {
            mDatabase.child("user_progress").child(mAuth.getUid()).child(lessonTitle).setValue(true);
        }
        
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("WRONG_EXERCISES", new Gson().toJson(wrongExercises));
        intent.putExtra("TOTAL_QUESTIONS", exerciseList.size());
        startActivity(intent);
        finish();
    }
}

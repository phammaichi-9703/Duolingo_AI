package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    private Lesson lesson;
    private int currentQuestionIndex = 0;
    private int hearts = 5;
    private int earnedXp = 0;

    private TextView tvQuestion, tvHearts;
    private RadioGroup rgOptions;
    private RadioButton rb1, rb2, rb3, rb4;
    private MaterialButton btnCheck;
    private LinearProgressIndicator progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        lesson = (Lesson) getIntent().getSerializableExtra("lesson");
        if (lesson == null || lesson.questions == null || lesson.questions.isEmpty()) {
            finish();
            return;
        }

        tvQuestion = findViewById(R.id.tvQuestion);
        tvHearts = findViewById(R.id.tvHearts);
        rgOptions = findViewById(R.id.rgOptions);
        rb1 = findViewById(R.id.rbOption1);
        rb2 = findViewById(R.id.rbOption2);
        rb3 = findViewById(R.id.rbOption3);
        rb4 = findViewById(R.id.rbOption4);
        btnCheck = findViewById(R.id.btnCheck);
        progressBar = findViewById(R.id.quizProgress);

        displayQuestion();

        btnCheck.setOnClickListener(v -> {
            int selectedId = rgOptions.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }

            checkAnswer(selectedId);
        });
    }

    private void displayQuestion() {
        Question q = lesson.questions.get(currentQuestionIndex);
        tvQuestion.setText(q.questionText);
        rb1.setText(q.options.get(0));
        rb2.setText(q.options.get(1));
        rb3.setText(q.options.get(2));
        rb4.setText(q.options.get(3));
        rgOptions.clearCheck();

        tvHearts.setText(String.valueOf(hearts));
        int progress = (int) (((float) (currentQuestionIndex) / lesson.questions.size()) * 100);
        progressBar.setProgress(progress);
    }

    private void checkAnswer(int selectedId) {
        Question q = lesson.questions.get(currentQuestionIndex);
        int selectedIndex = -1;
        if (selectedId == R.id.rbOption1) selectedIndex = 0;
        else if (selectedId == R.id.rbOption2) selectedIndex = 1;
        else if (selectedId == R.id.rbOption3) selectedIndex = 2;
        else if (selectedId == R.id.rbOption4) selectedIndex = 3;

        if (selectedIndex == q.correctAnswerIndex) {
            earnedXp += 10;
            Toast.makeText(this, "Correct! +10 XP", Toast.LENGTH_SHORT).show();
        } else {
            hearts--;
            Toast.makeText(this, "Wrong! Correct answer: " + q.options.get(q.correctAnswerIndex), Toast.LENGTH_SHORT).show();
        }

        if (hearts <= 0) {
            Toast.makeText(this, "Game Over! You ran out of hearts.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        currentQuestionIndex++;
        if (currentQuestionIndex < lesson.questions.size()) {
            displayQuestion();
        } else {
            // Finished lesson
            Intent resultIntent = new Intent();
            resultIntent.putExtra("xpEarned", earnedXp);
            resultIntent.putExtra("lessonId", lesson.id);
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, "Lesson Complete! Total XP: " + earnedXp, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}

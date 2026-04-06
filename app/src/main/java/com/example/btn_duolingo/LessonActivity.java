package com.example.btn_duolingo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LessonActivity extends AppCompatActivity {

    private TextView tvQuestion, tvFeedback;
    private ChipGroup chipGroupOptions, chipGroupAnswer;
    private Button btnCheck;
    private String correctAnswer;
    private List<String> selectedWords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        tvQuestion = findViewById(R.id.tvSentenceToTranslate);
        tvFeedback = findViewById(R.id.tvFeedback);
        chipGroupOptions = findViewById(R.id.chipGroupOptions);
        chipGroupAnswer = findViewById(R.id.chipGroupAnswer);
        btnCheck = findViewById(R.id.btnCheckAnswer);

        // Nhận dữ liệu từ Intent
        String question = getIntent().getStringExtra("EX_QUESTION");
        String optionsStr = getIntent().getStringExtra("EX_OPTIONS");
        correctAnswer = getIntent().getStringExtra("EX_ANSWER");

        tvQuestion.setText(question);

        // Hiển thị các từ lựa chọn
        if (optionsStr != null) {
            String[] options = optionsStr.split("\\|");
            List<String> optionList = new ArrayList<>();
            for (String s : options) optionList.add(s);
            Collections.shuffle(optionList); // Trộn ngẫu nhiên các từ

            for (String word : optionList) {
                addOptionChip(word);
            }
        }

        btnCheck.setOnClickListener(v -> {
            if (btnCheck.getText().toString().equals("CONTINUE")) {
                finish(); // Quay lại danh sách bài tập (Hoặc bạn có thể code chuyển bài tiếp theo ở đây)
                return;
            }
            checkAnswer();
        });

        findViewById(R.id.btnCloseLesson).setOnClickListener(v -> finish());
    }

    private void addOptionChip(String word) {
        Chip chip = new Chip(this);
        chip.setText(word);
        chip.setClickable(true);
        chip.setOnClickListener(v -> {
            chipGroupOptions.removeView(chip);
            addAnswerChip(word);
        });
        chipGroupOptions.addView(chip);
    }

    private void addAnswerChip(String word) {
        Chip chip = new Chip(this);
        chip.setText(word);
        chip.setClickable(true);
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

        tvFeedback.setVisibility(View.VISIBLE);
        if (userAsnwer.toString().equals(correctAnswer)) {
            tvFeedback.setText("Correct! Well done.");
            tvFeedback.setTextColor(Color.parseColor("#58CC02"));
            btnCheck.setBackgroundColor(Color.parseColor("#58CC02"));
            btnCheck.setText("CONTINUE");
        } else {
            tvFeedback.setText("Incorrect. Try again!");
            tvFeedback.setTextColor(Color.RED);
            Toast.makeText(this, "Correct answer: " + correctAnswer, Toast.LENGTH_LONG).show();
        }
    }
}
package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LessonAdapter.OnLessonClickListener {

    private static final int REQUEST_CODE_QUIZ = 101;
    private int totalXp = 0;
    private TextView tvTotalXp;
    private List<Lesson> lessons;
    private LessonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTotalXp = findViewById(R.id.tvTotalXp);
        RecyclerView rv = findViewById(R.id.rvLessons);
        rv.setLayoutManager(new LinearLayoutManager(this));

        lessons = generateDummyData();
        adapter = new LessonAdapter(lessons, this);
        rv.setAdapter(adapter);

        updateXpDisplay();
    }

    private void updateXpDisplay() {
        tvTotalXp.setText(totalXp + " XP");
    }

    @Override
    public void onLessonRangeClick(Lesson lesson) {
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra("lesson", lesson);
        startActivityForResult(intent, REQUEST_CODE_QUIZ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_QUIZ && resultCode == RESULT_OK && data != null) {
            int xpEarned = data.getIntExtra("xpEarned", 0);
            int lessonId = data.getIntExtra("lessonId", -1);

            totalXp += xpEarned;
            updateXpDisplay();

            // Cập nhật progress giả định cho bài học
            for (Lesson l : lessons) {
                if (l.id == lessonId) {
                    l.progress = 100;
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    private List<Lesson> generateDummyData() {
        List<Lesson> list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            List<Question> questions = Arrays.asList(
                    new Question("How do you say 'Hello'?", Arrays.asList("Xin chào", "Tạm biệt", "Cảm ơn", "Xin lỗi"), 0),
                    new Question("How do you say 'Thank you'?", Arrays.asList("Xin chào", "Tạm biệt", "Cảm ơn", "Xin lỗi"), 2),
                    new Question("How do you say 'Goodbye'?", Arrays.asList("Xin chào", "Tạm biệt", "Cảm ơn", "Xin lỗi"), 1),
                    new Question("How do you say 'Sorry'?", Arrays.asList("Xin chào", "Tạm biệt", "Cảm ơn", "Xin lỗi"), 3),
                    new Question("What is 'Water'?", Arrays.asList("Nước", "Cơm", "Thịt", "Cá"), 0)
            );
            list.add(new Lesson(i, "Lesson " + i, "Description for lesson " + i, 50, 0, questions));
        }
        return list;
    }
}

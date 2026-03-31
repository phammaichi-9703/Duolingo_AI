package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements LessonAdapter.OnLessonClickListener {

    private static final int REQUEST_CODE_QUIZ = 101;
    private TextView tvTotalXp, tvMainStreak, tvTotalLessons;
    private List<Lesson> lessons;
    private LessonAdapter adapter;
    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        preferenceManager = new PreferenceManager(requireContext());

        tvTotalXp = view.findViewById(R.id.tvTotalXp);
        tvMainStreak = view.findViewById(R.id.tvMainStreak);
        tvTotalLessons = view.findViewById(R.id.tvTotalLessons);
        
        RecyclerView rv = view.findViewById(R.id.rvLessons);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        lessons = generateDummyData();
        adapter = new LessonAdapter(lessons, this);
        rv.setAdapter(adapter);

        updateUI();

        return view;
    }

    private void updateUI() {
        if (tvTotalXp != null) tvTotalXp.setText(preferenceManager.getTotalXp() + " XP");
        if (tvMainStreak != null) tvMainStreak.setText(String.valueOf(preferenceManager.getStreak()));
        if (tvTotalLessons != null) tvTotalLessons.setText(String.valueOf(preferenceManager.getLessonsDone()));
    }

    @Override
    public void onLessonRangeClick(Lesson lesson) {
        Intent intent = new Intent(getActivity(), QuestionActivity.class);
        intent.putExtra("lesson", lesson);
        startActivityForResult(intent, REQUEST_CODE_QUIZ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_QUIZ && resultCode == RESULT_OK && data != null) {
            int xpEarned = data.getIntExtra("xpEarned", 0);
            int lessonId = data.getIntExtra("lessonId", -1);

            preferenceManager.addXp(xpEarned);
            preferenceManager.addLessonDone();
            updateUI();

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
        String[] titles = {"Basics 1", "Common Phrases", "Family", "Food", "Animals", "Adjectives", "Plurals", "Possessives", "Objective Case", "Clothing"};
        String[] descs = {"Say hello and start learning", "Everyday expressions", "Talk about your relatives", "Eat and drink vocabulary", "Domestic and wild animals", "Describe things", "More than one", "Mine, yours, hers", "Me, you, him", "What are you wearing?"};

        for (int i = 0; i < 10; i++) {
            List<Question> questions = new ArrayList<>();
            questions.add(new Question("How do you say 'Hello'?", Arrays.asList("Xin chào", "Tạm biệt", "Cảm ơn", "Xin lỗi"), 0));
            questions.add(new Question("What is 'Water'?", Arrays.asList("Nước", "Cơm", "Thịt", "Cá"), 0));
            questions.add(new Question("Translate: 'Good morning'", Arrays.asList("Chào buổi sáng", "Chào buổi chiều", "Chào buổi tối", "Chúc ngủ ngon"), 0));
            questions.add(new Question("Translate: 'Apple'", Arrays.asList("Quả táo", "Quả chuối", "Quả cam", "Quả xoài"), 0));
            questions.add(new Question("How do you say 'Thank you'?", Arrays.asList("Xin chào", "Cảm ơn", "Tạm biệt", "Không có gì"), 1));
            
            list.add(new Lesson(i + 1, titles[i], descs[i], 50, 0, questions));
        }
        return list;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}

package com.example.btl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.model.AppDatabase;
import com.example.btl.model.User;
import com.example.btl.model.UserDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment implements LessonAdapter.OnLessonClickListener {

    private TextView tvTotalXp, tvMainStreak, tvTotalLessons;
    private List<Lesson> lessons;
    private LessonAdapter adapter;
    private PreferenceManager preferenceManager;
    private UserDao userDao;
    
    private final ActivityResultLauncher<Intent> quizLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    int xpEarned = result.getData().getIntExtra("xpEarned", 0);
                    int lessonId = result.getData().getIntExtra("lessonId", -1);
                    handleQuizResult(xpEarned, lessonId);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        preferenceManager = new PreferenceManager(requireContext());
        userDao = AppDatabase.getDatabase(requireContext()).userDao();

        tvTotalXp = view.findViewById(R.id.tvTotalXp);
        tvMainStreak = view.findViewById(R.id.tvMainStreak);
        tvTotalLessons = view.findViewById(R.id.tvTotalLessons);
        
        RecyclerView rv = view.findViewById(R.id.rvLessons);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        lessons = generateDummyData();
        adapter = new LessonAdapter(lessons, this);
        rv.setAdapter(adapter);

        loadUserData();

        return view;
    }

    private void loadUserData() {
        String username = preferenceManager.getUsername();
        if (username == null) return;

        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = userDao.getUserByUsername(username);
            if (user != null) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> updateUI(user));
                }
            }
        });
    }

    private void updateUI(User user) {
        if (tvTotalXp != null) tvTotalXp.setText(user.totalXp + " XP");
        if (tvMainStreak != null) tvMainStreak.setText(String.valueOf(user.streak));
        if (tvTotalLessons != null) tvTotalLessons.setText(String.valueOf(user.lessonsDone));
    }

    private void handleQuizResult(int xpEarned, int lessonId) {
        String username = preferenceManager.getUsername();
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.updateProgress(username, xpEarned);
            User updatedUser = userDao.getUserByUsername(username);
            requireActivity().runOnUiThread(() -> {
                if (updatedUser != null) updateUI(updatedUser);
                for (Lesson l : lessons) {
                    if (l.id == lessonId) {
                        l.progress = 100;
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onLessonRangeClick(Lesson lesson) {
        Intent intent = new Intent(getActivity(), QuestionActivity.class);
        intent.putExtra("lesson", lesson);
        quizLauncher.launch(intent);
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
        loadUserData();
    }
}

package com.example.btn_duolingo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ExerciseAdapter.OnExerciseClickListener {

    private RecyclerView rvExercises;
    private DatabaseHelper dbHelper;
    private ExerciseAdapter adapter;
    private List<Exercise> lessonList;
    private List<Boolean> lockStatus;
    private String currentLanguage = "English";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvExercises = view.findViewById(R.id.rvExercises);
        dbHelper = new DatabaseHelper(getContext());

        if (getArguments() != null) {
            currentLanguage = getArguments().getString("LANGUAGE", "English");
        }

        updateLessonList();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLessonList();
    }

    public void setLanguage(String language) {
        this.currentLanguage = language;
        updateLessonList();
    }

    private void updateLessonList() {
        if (getContext() == null) return;
        
        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPref.getString("current_username", "");

        List<Exercise> allLessons = dbHelper.getUniqueLessons();
        lessonList = new ArrayList<>();
        
        // Lọc bài học theo ngôn ngữ
        for (Exercise ex : allLessons) {
            if (currentLanguage.equals("Chinese")) {
                if (ex.getTitle().startsWith("Chinese")) {
                    lessonList.add(ex);
                }
            } else {
                if (!ex.getTitle().startsWith("Chinese")) {
                    lessonList.add(ex);
                }
            }
        }

        lockStatus = new ArrayList<>();
        for (int i = 0; i < lessonList.size(); i++) {
            if (i == 0) {
                lockStatus.add(false);
            } else {
                String previousLessonTitle = lessonList.get(i - 1).getTitle();
                boolean isPreviousCompleted = dbHelper.isLessonCompleted(username, previousLessonTitle);
                lockStatus.add(!isPreviousCompleted);
            }
        }

        adapter = new ExerciseAdapter(lessonList, lockStatus, this);
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExercises.setAdapter(adapter);
    }

    @Override
    public void onExerciseClick(Exercise exercise, boolean isLocked) {
        if (isLocked) {
            Toast.makeText(getContext(), "Bạn cần hoàn thành bài học trước đó!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getActivity(), LessonActivity.class);
            intent.putExtra("EX_TITLE", exercise.getTitle());
            startActivity(intent);
        }
    }
}

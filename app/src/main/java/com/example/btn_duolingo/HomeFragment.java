package com.example.btn_duolingo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HomeFragment extends Fragment implements ExerciseAdapter.OnExerciseClickListener {

    private RecyclerView rvExercises;
    private DatabaseHelper dbHelper;
    private ExerciseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvExercises = view.findViewById(R.id.rvExercises);
        dbHelper = new DatabaseHelper(getContext());

        // Lấy danh sách bài tập từ database
        List<Exercise> exerciseList = dbHelper.getAllExercises();

        // Thiết lập RecyclerView
        adapter = new ExerciseAdapter(exerciseList, this);
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExercises.setAdapter(adapter);

        return view;
    }

    @Override
    public void onExerciseClick(Exercise exercise) {
        // Khi nhấn vào một bài tập, chuyển sang LessonActivity và gửi dữ liệu bài tập
        Intent intent = new Intent(getActivity(), LessonActivity.class);
        intent.putExtra("EX_TITLE", exercise.getTitle());
        intent.putExtra("EX_QUESTION", exercise.getQuestion());
        intent.putExtra("EX_OPTIONS", exercise.getOptions());
        intent.putExtra("EX_ANSWER", exercise.getAnswer());
        startActivity(intent);
    }
}
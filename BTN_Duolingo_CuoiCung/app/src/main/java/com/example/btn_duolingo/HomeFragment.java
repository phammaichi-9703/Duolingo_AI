package com.example.btn_duolingo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment implements ExerciseAdapter.OnExerciseClickListener {

    private static final String TAG = "HomeFragment";
    private RecyclerView rvExercises;
    private ExerciseAdapter adapter;
    private List<Exercise> lessonList;
    private List<Boolean> lockStatus;
    private String currentLanguage = "English";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvExercises = view.findViewById(R.id.rvExercises);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

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

    private void updateLessonList() {
        if (!isAdded() || getContext() == null || mAuth.getCurrentUser() == null) return;
        
        String userId = mAuth.getCurrentUser().getUid();
        String nodeName = currentLanguage.equals("Chinese") ? "exercises_cn" : "exercises";

        Log.d(TAG, "Fetching exercises from Firebase node: " + nodeName);
        mDatabase.child(nodeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || getContext() == null) return;
                
                if (!snapshot.exists()) {
                    Log.e(TAG, "No data found at '" + nodeName + "' node.");
                    return;
                }

                List<Exercise> allExercises = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Exercise ex = data.getValue(Exercise.class);
                    if (ex != null) {
                        allExercises.add(ex);
                    }
                }

                Log.d(TAG, "Total exercises fetched: " + allExercises.size());

                lessonList = new ArrayList<>();
                Set<String> titles = new HashSet<>();
                for (Exercise ex : allExercises) {
                    if (ex.getTitle() != null && !titles.contains(ex.getTitle())) {
                        lessonList.add(ex);
                        titles.add(ex.getTitle());
                    }
                }

                Log.d(TAG, "Unique lessons for " + currentLanguage + ": " + lessonList.size());

                // Fetch user progress
                mDatabase.child("user_progress").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot progressSnapshot) {
                        if (!isAdded() || getContext() == null) return;
                        
                        lockStatus = new ArrayList<>();
                        for (int i = 0; i < lessonList.size(); i++) {
                            if (i == 0) {
                                lockStatus.add(false);
                            } else {
                                String previousLessonTitle = lessonList.get(i - 1).getTitle();
                                boolean isPreviousCompleted = progressSnapshot.hasChild(previousLessonTitle);
                                lockStatus.add(!isPreviousCompleted);
                            }
                        }
                        adapter = new ExerciseAdapter(lessonList, lockStatus, HomeFragment.this);
                        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
                        rvExercises.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load exercises: " + error.getMessage());
            }
        });
    }

    @Override
    public void onExerciseClick(Exercise exercise, boolean isLocked) {
        if (!isAdded() || getContext() == null) return;

        if (isLocked) {
            Toast.makeText(getContext(), "Bạn cần hoàn thành bài học trước đó!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getActivity(), LessonActivity.class);
            intent.putExtra("EX_TITLE", exercise.getTitle());
            intent.putExtra("LANGUAGE", currentLanguage);
            startActivity(intent);
        }
    }
}

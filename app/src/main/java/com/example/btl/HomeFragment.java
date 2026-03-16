package com.example.btl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView rv = view.findViewById(R.id.rvLessons);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayList<Lesson> lessons = new ArrayList<>();
        lessons.add(new Lesson("Basic Greetings", "Learn how to say hello and goodbye", "+50 XP", 100));
        lessons.add(new Lesson("Common Phrases", "Essential everyday expressions", "+30 XP", 60));
        lessons.add(new Lesson("Numbers & Counting", "Count from 1 to 100", "+30 XP", 0));
        lessons.add(new Lesson("Food & Drinks", "Order at restaurants and cafes", "+30 XP", 0));
        lessons.add(new Lesson("Family & Friends", "Talk about people close to you", "+40 XP", 0));
        lessons.add(new Lesson("Travel Basics", "Navigate airports and hotels", "+50 XP", 0));

        rv.setAdapter(new LessonAdapter(lessons));

        return view;
    }
}

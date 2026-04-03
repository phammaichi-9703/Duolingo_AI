package com.example.btl.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface LessonDao {
    @Insert
    void insert(LessonEntity lesson);

    @Query("SELECT * FROM lessons")
    List<LessonEntity> getAllLessons();

    @Query("SELECT * FROM lessons WHERE id = :id")
    LessonEntity getLessonById(int id);

    @Update
    void update(LessonEntity lesson);

    @Query("UPDATE lessons SET isCompleted = 1 WHERE id = :id")
    void completeLesson(int id);

    @Query("UPDATE lessons SET isUnlocked = 1 WHERE id = :id")
    void unlockLesson(int id);
}

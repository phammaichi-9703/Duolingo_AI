package com.example.btl.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String username;
    public String password;
    public int totalXp;
    public int streak;
    public int lessonsDone;
    public String avatarUri;
    public int level;
    public String lastLessonDate; // Format: yyyy-MM-dd
    public String wrongAnswers; // JSON string of wrong questions for review

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.totalXp = 0;
        this.streak = 0;
        this.lessonsDone = 0;
        this.level = 1;
        this.lastLessonDate = "";
        this.wrongAnswers = "[]";
    }
}

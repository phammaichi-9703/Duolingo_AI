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
    public String avatarUri; // Changed to Uri for simplicity with image picker
    public int level;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.totalXp = 0;
        this.streak = 0;
        this.lessonsDone = 0;
        this.level = 1;
    }

    public int calculateLevel() {
        // Simple level logic: every 100 XP is a level
        return (totalXp / 100) + 1;
    }
}

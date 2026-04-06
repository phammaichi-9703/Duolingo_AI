package com.example.btl.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lessons")
public class LessonEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String desc;
    public int xpValue;
    public int isUnlocked; // 0 for locked, 1 for unlocked
    public int isCompleted; // 0 or 1
    public String type; // VOCABULARY, GRAMMAR, LISTENING, etc.
    public String questionsJson; // Store questions as JSON string for simplicity

    public LessonEntity(String title, String desc, int xpValue, String type, String questionsJson) {
        this.title = title;
        this.desc = desc;
        this.xpValue = xpValue;
        this.type = type;
        this.questionsJson = questionsJson;
        this.isUnlocked = 0;
        this.isCompleted = 0;
    }
}

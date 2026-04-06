package com.example.btl;

import java.io.Serializable;
import java.util.List;

public class Lesson implements Serializable {
    public int id;
    public String title;
    public String desc;
    public int xpValue;
    public int progress; // 0..100
    public List<Question> questions;

    public Lesson(int id, String title, String desc, int xpValue, int progress, List<Question> questions) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.xpValue = xpValue;
        this.progress = progress;
        this.questions = questions;
    }
}

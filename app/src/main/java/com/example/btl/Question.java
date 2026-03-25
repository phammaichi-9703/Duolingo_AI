package com.example.btl;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    public String questionText;
    public List<String> options;
    public int correctAnswerIndex;

    public Question(String questionText, List<String> options, int correctAnswerIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }
}

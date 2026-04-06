package com.example.btn_duolingo;

public class Exercise {
    private int id;
    private String title;
    private String description;
    private String question;
    private String options;
    private String answer;

    public Exercise(int id, String title, String description, String question, String options, String answer) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.question = question;
        this.options = options;
        this.answer = answer;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getQuestion() { return question; }
    public String getOptions() { return options; }
    public String getAnswer() { return answer; }
}
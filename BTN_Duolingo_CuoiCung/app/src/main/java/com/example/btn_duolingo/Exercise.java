package com.example.btn_duolingo;

public class Exercise {
    private int id;
    private String title;
    private String description;
    private String question;
    private String options;
    private String answer;

    // Required for Firebase
    public Exercise() {}

    public Exercise(int id, String title, String description, String question, String options, String answer) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.question = question;
        this.options = options;
        this.answer = answer;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}

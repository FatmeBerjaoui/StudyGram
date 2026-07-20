package com.example.studygram.models;

public class QuizQuestion {

    private String frage;
    private String antwort;

    public QuizQuestion(String frage, String antwort) {
        this.frage = frage;
        this.antwort = antwort;
    }

    public String getFrage() {
        return frage;
    }

    public String getAntwort() {
        return antwort;
    }
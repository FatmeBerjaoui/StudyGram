package com.example.studygram.models;

public class QuizQuestion {

    private String frage;
    private String antwort;

    // Leerer Konstruktor für Firestore
    public QuizQuestion() {
    }

    public QuizQuestion(String frage, String antwort) {
        this.frage = frage;
        this.antwort = antwort;
    }

    public String getFrage() {
        return frage;
    }

    public void setFrage(String frage) {
        this.frage = frage;
    }

    public String getAntwort() {
        return antwort;
    }

    public void setAntwort(String antwort) {
        this.antwort = antwort;
    }
}
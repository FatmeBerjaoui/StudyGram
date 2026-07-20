package com.example.studygram.models;

public class QuizQuestion {

    private String frage;
    private String typ;

    private String antwortA;
    private String antwortB;
    private String antwortC;
    private String antwortD;

    private String richtigeAntwort;

    public QuizQuestion() {
    }

    public QuizQuestion(String frage, String typ,
                        String antwortA, String antwortB,
                        String antwortC, String antwortD,
                        String richtigeAntwort) {

        this.frage = frage;
        this.typ = typ;
        this.antwortA = antwortA;
        this.antwortB = antwortB;
        this.antwortC = antwortC;
        this.antwortD = antwortD;
        this.richtigeAntwort = richtigeAntwort;
    }

    public String getFrage() {
        return frage;
    }

    public String getTyp() {
        return typ;
    }

    public String getAntwortA() {
        return antwortA;
    }

    public String getAntwortB() {
        return antwortB;
    }

    public String getAntwortC() {
        return antwortC;
    }

    public String getAntwortD() {
        return antwortD;
    }

    public String getRichtigeAntwort() {
        return richtigeAntwort;
    }
}

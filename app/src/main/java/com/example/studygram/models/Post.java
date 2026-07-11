package com.example.studygram.models;

public class Post {
    private String title;
    private String subject;
    private String description;

    public Post() { //leerer Konstruktor für Firebase

    }

        public Post(String title, String subject , String description) {
         this.title= title;  //Attribute der Klasse Post werden überschrieben
         this.subject= subject;
         this.description= description;
    }
    // Getter
    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    // Setter
    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


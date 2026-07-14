package com.example.studygram;

public class Post {

    private String title;
    private String subject;
    private String imageUrl;
    private String userId;

    // Firestore braucht einen leeren Konstruktor, um Objekte automatisch zu erstellen
    public Post() {
    }

    public Post(String title, String subject, String imageUrl, String userId) {
        this.title = title;
        this.subject = subject;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }


}
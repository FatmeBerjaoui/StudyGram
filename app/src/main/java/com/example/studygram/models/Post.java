package com.example.studygram.models;

public class Post {
    private String title;
    private String subject;
    private String description;
    private String username;
    private int likes;
    private boolean liked = false;

    public Post() { //leerer Konstruktor für Firebase

    }

    public Post(String username, String title,String subject,String description, int likes) {

        this.username = username;
        this.title = title;
        this.subject = subject;
        this.description = description;
        this.likes = likes;
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

    public String getUsername() {
        return username;
    }

    public int getLikes() {
        return likes;
    }
    public boolean isLiked() {
        return liked;
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
    public void setUsername(String username) {
        this.username = username;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}


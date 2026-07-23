package com.example.studygram.models;

public class Post {
    private String title;
    private String subject;
    private String description;
    private String username;
    private String imageUrl;
    private String userId;
    private int likes;
    private boolean liked = false;
    private boolean saved = false;



    public Post() { //leerer Konstruktor für Firebase

    }

    public Post(String userId,String username, String title,String subject,String description,String imageUrl, int likes) {
        this.userId = userId;
        this.username = username;
        this.title = title;
        this.subject = subject;
        this.description = description;
        this.imageUrl = imageUrl;
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
    public String getImageUrl() { return imageUrl; }
    public String getUserId() { return userId; }
    public String getUsername() {
        return username;
    }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setUserId(String userId) { this.userId = userId; }
    public int getLikes() {
        return likes;
    }
    public boolean isLiked() {
        return liked;
    }
    public boolean isSaved() {
        return saved;
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
    public void setSaved(boolean saved) {
        this.saved = saved;
    }
}


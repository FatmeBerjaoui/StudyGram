package com.example.studygram;

public class Post {
    private String postId;
    private String userId;
    private String title;
    private String subject;
    private String description;
    private String username;
    private int likes;
    private boolean liked = false;
    private boolean saved = false;
    private String imageUrl;

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
    public String getPostId() {return postId;}
    public String getUserId() {
        return userId;
    }
    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }
    public String getImageUrl() {
        return imageUrl;
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
    public boolean isSaved() {
        return saved;
    }

    // Setter
    public void setPostId(String postId) {this.postId = postId;}
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}
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



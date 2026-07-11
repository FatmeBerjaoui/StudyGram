package com.example.studygram;

public class post{
    private String title;
    private String subject;
    private String description;

    public post () {}

        public post(String title, String subject , String description) {
         this.title= title;  //Attribute der Klasse Post werden überschrieben
         this.subject= subject;
         this.description= description;
    }
}

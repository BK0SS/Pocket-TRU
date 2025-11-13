package com.example.pockettru;

public class StudyGroupModel
{
    private String author;
    private String date;
    private String time;
    private String description;

    public StudyGroupModel(String author, String date, String time, String description){
        this.author = author;
        this.date = date;
        this.time = time;
        this.description = description;
    }
    public StudyGroupModel()
    {
        this.author = "";
        this.date = "";
        this.time = "";
        this.description = "";

    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }


    public String getTime() {
        return time;

    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}

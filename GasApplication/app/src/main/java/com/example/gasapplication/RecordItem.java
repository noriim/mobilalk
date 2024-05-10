package com.example.gasapplication;

public class RecordItem {
    private String id;
    private User user;
    private String value;
    private String dateAndTime;
    private boolean wrong;

    public RecordItem() {
    }

    public RecordItem(User user, String value, String dateAndTime) {
        this.user = user;
        this.value = value;
        this.dateAndTime = dateAndTime;
        this.wrong = false;
    }

    public User getUser() {return user;}
    public String getValue() {return value;}

    public String getDateAndTime() {return dateAndTime;}


    public boolean isWrong() {return wrong;}

    public String _getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}



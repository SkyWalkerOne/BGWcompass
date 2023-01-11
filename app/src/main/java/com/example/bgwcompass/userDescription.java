package com.example.bgwcompass;

public class userDescription {
    private String id;
    private final String place, myTime, myDesc;

    public userDescription(String place, String myTime, String myDesc, String id) {
        this.place = place;
        this.myTime = myTime;
        this.myDesc = myDesc;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getPlace() {
        return place;
    }

    public String getMyTime() {
        return myTime;
    }

    public String getMyDesc() {
        return myDesc;
    }
}

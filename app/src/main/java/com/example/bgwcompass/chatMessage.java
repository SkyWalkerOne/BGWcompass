package com.example.bgwcompass;

public class chatMessage {
    private final String userName, message, time, id, avatarURL;

    public chatMessage(String userName, String message, String time, String id, String avatarURL) {
        this.userName = userName;
        this.message = message;
        this.time = time;
        this.id = id;
        this.avatarURL = avatarURL;
    }

    public String getId() {
        return id;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }
}

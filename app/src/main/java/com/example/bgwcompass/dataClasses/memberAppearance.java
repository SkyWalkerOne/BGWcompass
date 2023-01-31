package com.example.bgwcompass.dataClasses;

public class memberAppearance {
    private final String memberID, avatarURL, nickName;

    public memberAppearance(String memberID, String avatarURL, String nickName) {
        this.memberID = memberID;
        this.avatarURL = avatarURL;
        this.nickName = nickName;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public String getNickName() {
        return nickName;
    }
}

package com.example.bgwcompass;

import java.util.ArrayList;

public class opengame {
    final private String creatorName, creatorID;
    final private String gameName, gameDime, gameDesc, gamePlace;
    private final ArrayList<String> members;

    public opengame(String creatorName, String creatorID, String gameName, String gameDime, String gameDesc, String gamePlace) {
        this.creatorName = creatorName;
        this.creatorID = creatorID;
        this.gameName = gameName;
        this.gameDime = gameDime;
        this.gameDesc = gameDesc;
        this.gamePlace = gamePlace;
        this.members = new ArrayList<>();
    }

    public opengame(String creatorName, String creatorID, String gameName, String gameDime, String gameDesc, ArrayList<String> members, String gamePlace) {
        this.creatorName = creatorName;
        this.creatorID = creatorID;
        this.gameName = gameName;
        this.gameDime = gameDime;
        this.gameDesc = gameDesc;
        this.gamePlace = gamePlace;
        this.members = members;
    }

    public void addMember (String userName) { this.members.add(userName); }

    public String getCreatorName() { return creatorName; }
    public String getCreatorID() { return creatorID; }
    public String getGameName() { return gameName; }
    public String getGameDime() { return gameDime; }
    public String getGameDesc() { return gameDesc; }
    public ArrayList<String> getMembers() { return members; }
    public String getGamePlace() { return gamePlace; }
}

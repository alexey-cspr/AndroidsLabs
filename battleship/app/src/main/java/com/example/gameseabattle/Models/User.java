package com.example.gameseabattle.Models;

public class User {
    private String id;
    private String imagePath;
    private String username;
    private Integer gamesAmount;
    private Integer wins;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Integer getGamesAmount() {
        return gamesAmount;
    }

    public Integer getWins() {
        return wins;
    }

}

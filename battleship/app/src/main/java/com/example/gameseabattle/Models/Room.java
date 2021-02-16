package com.example.gameseabattle.Models;

import com.example.gameseabattle.GameState;

public class Room {
    private String id;
    private String field1;
    private String field2;
    private String hostUser;
    private String user;
    private GameState gameState;

    public Room() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getField1() {
        return field1;
    }


    public String getField2() {
        return field2;
    }


    public String getHostUser() {
        return hostUser;
    }


    public String getUser() {
        return user;
    }


    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}

package com.example.umyhpuscdi.snapthat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by umyhpuscdi on 2016-05-09.
 */

public class PlayerData implements Serializable {

    private ArrayList<ThingToPhotograph> thingsToPhotograph;
    private String username;
    private boolean ready;
    private int score, numberOfPhotos;
    private String playerID;

    //For serialization
    private static final long serialVersionUID =-3016869652413998727L;

    public PlayerData(String playerID, String username) {
        this.playerID = playerID;
        this.username = username;
    }

    public ArrayList<ThingToPhotograph> getThingsToPhotograph() {
        return thingsToPhotograph;
    }

    public void setThingsToPhotograph(ArrayList<ThingToPhotograph> thingsToPhotograph) {
        this.thingsToPhotograph = thingsToPhotograph;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getNumberOfPhotos() {
        return numberOfPhotos;
    }

    public void setNumberOfPhotos(int numberOfPhotos) {
        this.numberOfPhotos = numberOfPhotos;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }
}

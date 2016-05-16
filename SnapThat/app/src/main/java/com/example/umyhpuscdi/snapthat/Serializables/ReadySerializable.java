package com.example.umyhpuscdi.snapthat.Serializables;

import com.example.umyhpuscdi.snapthat.PlayerData;

import java.io.Serializable;

/**
 * Created by umyhpuscdi on 2016-05-16.
 */
public class ReadySerializable implements Serializable {

    private String playerID;
    private boolean ready;

    //For serialization
    private static final long serialVersionUID =-3016869652413998727L;

    public ReadySerializable(PlayerData playerData) {
        this.playerID = playerData.getPlayerID();
        this.ready = playerData.isReady();
    }

    public String getPlayerID() {
        return playerID;
    }

    public boolean isReady() {
        return ready;
    }
}

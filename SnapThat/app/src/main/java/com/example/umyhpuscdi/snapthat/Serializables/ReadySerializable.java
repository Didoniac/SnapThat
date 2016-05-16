package com.example.umyhpuscdi.snapthat.Serializables;

import java.io.Serializable;

/**
 * Created by umyhpuscdi on 2016-05-16.
 */
public class ReadySerializable implements Serializable {

    private String playerID;
    private boolean ready;

    //For serialization
    private static final long serialVersionUID =-3016869652413998727L;

    public ReadySerializable(String playerID, boolean ready) {
        this.playerID = playerID;
        this.ready = ready;
    }

    public String getPlayerID() {
        return playerID;
    }

    public boolean isReady() {
        return ready;
    }
}

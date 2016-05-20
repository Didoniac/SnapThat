package com.example.umyhpuscdi.snapthat.Serializables;

import com.example.umyhpuscdi.snapthat.PlayerData;

import java.io.Serializable;

/**
 * Created by umyhpuscdi on 2016-05-16.
 */
public class ReadySerializable {

    private String playerID;
    private boolean ready;

    public ReadySerializable(String participantId, boolean isReady) {
        this.playerID = participantId;
        this.ready = isReady;
    }

    public String getPlayerID() {
        return playerID;
    }

    public boolean isReady() {
        return ready;
    }
}

package com.example.umyhpuscdi.snapthat.Serializables;

import com.example.umyhpuscdi.snapthat.ThingToPhotograph;

import java.util.ArrayList;

/**
 * Created by umyhblomti on 2016-05-23.
 */
public class PlayerMetaDataSerializable {

    private String mParticipantId;
    private String mPlayerName;
    private ArrayList<ThingToPhotograph> mThingToPhotographs;

    public PlayerMetaDataSerializable(String participantId, ArrayList<ThingToPhotograph> thingToPhotographs, String playerName){
        this.mParticipantId = participantId;
        this.mThingToPhotographs = thingToPhotographs;
        this.mPlayerName = playerName;
    }

    public ArrayList<ThingToPhotograph> getThingToPhotographs() {
        return mThingToPhotographs;
    }

    public String getParticipantId() {
        return mParticipantId;
    }

    public String getmPlayerName(){
        return mPlayerName;
    }
}

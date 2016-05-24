package com.example.umyhpuscdi.snapthat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.umyhpuscdi.snapthat.Serializables.PlayerMetaDataSerializable;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;

import java.util.ArrayList;

/**
 * Created by umyhblomti on 2016-05-11.
 */
public class ResultsListAdapter extends ArrayAdapter<PlayerData> {

    private int mThingIndex;
    private MainActivity mainActivity;

    public ResultsListAdapter(Context context, int resource, ArrayList<PlayerData> playerDatas, int thingIndex) {
        super(context, resource, playerDatas);
        mThingIndex = thingIndex;
        mainActivity = (MainActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.listitem_results, parent, false);
        }

        //Find child-views
        TextView usernameTextView = (TextView) v.findViewById(R.id.results_listitem_username_textview);
        TextView guessTextView = (TextView) v.findViewById(R.id.results_listitem_bestGuess_textview);
        ImageView photoImageView = (ImageView) v.findViewById(R.id.results_listitem_photo_imageview);
        ImageView correctImageView = (ImageView) v.findViewById(R.id.results_listitem_correctness_imageview);

        //Set standard values
        String userName = "user not found";
        String guessText = "guess not found";
        Bitmap photoBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.no_photo);
        Bitmap correctBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.red_cross);

        //Get variables from playerData and playerMetaData
        PlayerData playerData = getItem(position);
        PlayerMetaDataSerializable playerMetaData = getPlayerMetaData(playerData);
        if(playerMetaData != null){
            //Username
            userName = playerMetaData.getmPlayerName();

            ThingToPhotograph metaDataThing =
                    playerMetaData.getThingToPhotographs().get(mThingIndex);
            if(metaDataThing.isPhotographed()){
                photoBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.loading);
                correctBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.loading_circle);
            }
        }
        if(playerData != null){

            //Get username
            if(playerData.getUsername() != null){
                userName = playerData.getUsername();
            }

            /*
            Try to get the current thingToPhotograph from a player. If the player has not taken any photos
            this try will not work and thingToPhotograph will be null.
             */
            ThingToPhotograph thingToPhotograph = null;
            try {
                thingToPhotograph = playerData.getThingsToPhotograph().get(mThingIndex);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(thingToPhotograph != null){

                //Get best guess
                guessText = thingToPhotograph.getBestGuess();

                //Get photo
                //TODO find perfect value to size bitmaps here
                Bitmap getBitmapResult = thingToPhotograph.getBitmap(10);
                if(getBitmapResult != null){
                    photoBitmap = getBitmapResult;
                }

                //Get correctness
                if(thingToPhotograph.isPhotographed()){
                    if(thingToPhotograph.isAccepted()){
                        correctBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.green_check);
                    }else if(thingToPhotograph.isUploading()){
                        correctBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.loading_circle);
                    }
                }
            }
        }

        //Set values to views
        usernameTextView.setText(userName);
        guessTextView.setText(guessText);
        photoImageView.setImageBitmap(photoBitmap);
        photoBitmap = null;
        correctImageView.setImageBitmap(correctBitmap);

        return v;
    }

    private PlayerMetaDataSerializable getPlayerMetaData(PlayerData playerData) {
        String participantId = playerData.getPlayerID();
        PlayerMetaDataSerializable playerMetaData;
        if(mainActivity.playerMetaDatas != null) {
            for (int i = 0; i < mainActivity.playerMetaDatas.size(); i++) {
                playerMetaData = mainActivity.playerMetaDatas.get(i);
                if (playerMetaData.getParticipantId().equals(participantId)) {
                    return playerMetaData;
                }
            }
        }
        return null;
    }

    public void setmThingIndex(int mThingIndex) {
        this.mThingIndex = mThingIndex;
    }
}

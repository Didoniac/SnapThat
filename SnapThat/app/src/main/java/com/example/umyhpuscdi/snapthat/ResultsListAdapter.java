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

import java.util.ArrayList;

/**
 * Created by umyhblomti on 2016-05-11.
 */
public class ResultsListAdapter extends ArrayAdapter<PlayerData> {

    private int mThingIndex;

    public ResultsListAdapter(Context context, int resource, ArrayList<PlayerData> playerDatas, int thingIndex) {
        super(context, resource, playerDatas);
        mThingIndex = thingIndex;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.listitem_results, parent, false);
        }

        PlayerData playerData = getItem(position);

        if (playerData != null) {

            TextView usernameTextView = (TextView) v.findViewById(R.id.results_listitem_username_textview);
            if (usernameTextView != null) { //TODO why is this check needed?
                usernameTextView.setText(playerData.getUsername());
            }

            ThingToPhotograph thing = null;
            if(playerData.getThingsToPhotograph() == null){
                Toast.makeText(parent.getContext(), "getThingsToPhotograph returns null position:" + position, Toast.LENGTH_SHORT).show();
            }else {
                thing = playerData.getThingsToPhotograph().get(mThingIndex);

                TextView guessTextView = (TextView) v.findViewById(R.id.results_listitem_bestGuess_textview);
                guessTextView.setText(thing.getBestGuess());


            /*TODO
                When a player takes a photo, it should be sent as a small compressed bitmap to
                the others (in mainactivity). If this listitem shows a different player, this should
                be able to find that bitmap and show it here.
             */
                Bitmap bitmap = null;
                if(true) {// TODO if this listitem is current player
                    bitmap = thing.getBitmap(10);
                }else {//if it's another player

                }
                if(bitmap != null) {
                    ImageView photoImageView = (ImageView) v.findViewById(R.id.results_listitem_photo_imageview);
                    photoImageView.setImageBitmap(bitmap);
                }

                Bitmap correctBitmap = null;
                if(thing.isUploadedAndChecked()){
                    if(thing.isAccepted()){
                        correctBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.green_check);
                    }else {
                        correctBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.red_cross);
                    }
                }else{
                    //This should run if the photo is uploading or has just been uploaded while this if was run
                    correctBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.loading_circle);
                }
                if(correctBitmap != null){
                    ImageView correctImageView = (ImageView) v.findViewById(R.id.results_listitem_correctness_imageview);
                    correctImageView.setImageBitmap(bitmap);
                }
            }
        }
        return v;
    }
}

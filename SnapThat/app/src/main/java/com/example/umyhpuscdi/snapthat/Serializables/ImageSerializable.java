package com.example.umyhpuscdi.snapthat.Serializables;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by umyhpuscdi on 2016-05-18.
 */
public class ImageSerializable {
    private String playerId;
    private Bitmap bitmap;
    private int index;
    private boolean accepted;
    private String bestGuess;

    public ImageSerializable(String playerId,
                             Bitmap bitmap,
                             int index,
                             boolean accepted,
                             String bestGuess) {

        this.playerId = playerId;
        this.index = index;
        this.accepted = accepted;
        this.bestGuess = bestGuess;
        setBitmap(bitmap);
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream)) {
            byte[] byteArray = stream.toByteArray();
            this.bitmap = BitmapFactory.decodeByteArray(byteArray,0,0);
        } else {
            this.bitmap = bitmap;
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getBestGuess() {
        return bestGuess;
    }

    public void setBestGuess(String bestGuess) {
        this.bestGuess = bestGuess;
    }


}
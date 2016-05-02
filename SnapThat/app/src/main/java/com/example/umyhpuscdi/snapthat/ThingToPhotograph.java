package com.example.umyhpuscdi.snapthat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

/**
 * Created by umyhblomti on 2016-05-02.
 */
public class ThingToPhotograph {


    private String mName;
    private boolean isPhotographed = false;
    private boolean accepted = false;
    private boolean hasBeenUploadedAndChecked = false;
    private Uri mFilePath;

    public ThingToPhotograph(String name){
        mName = name;
    }

    public void setFilePath(Uri filePath){
        mFilePath = filePath;
        isPhotographed = true;
    }

    public boolean isPhotographed(){
        return isPhotographed;
    }

    public boolean hasBeenUploadedAndChecked(){
        return hasBeenUploadedAndChecked;
    }

    public boolean isAccepted(){
        return accepted;
    }

    /*
    Runs an Asynctask to upload the file to ImageRecognition webbservice.
    When done:
    hasBeenUploadedAndChecked = true;
    accepted is updated to a correct value;
     */
    public String checkIfPhotoIsAccepted(){

        return "photo uploaded and checked successfully!";
    }
}

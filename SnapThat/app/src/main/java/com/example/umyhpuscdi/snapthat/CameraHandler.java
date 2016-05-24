package com.example.umyhpuscdi.snapthat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by umyhblomti on 2016-05-02.
 *
 * This class creates an intent that you can start in your Activity.
 * This intent starts the camera and the user can take a picture.
 * The picture is saved in the pictures folder in a given subfolder taken as the variable directoryInPictures.
 *
 * In your Activity you can override onActivityResult which is run after the camera has returned a
 * picture.
 * There you can send in the old intent and you get a bitmap scaled to some size.
 */
public class CameraHandler {

    /*
    Creates an intent you can run in an Activity with the method startActivityForResult().
    startActivityResult() takes this intent and a requestcode that should be 100.
    That is the CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE for some reason.
     */
    public static Intent getPictureFileIntent(Context context, String directoryInPictures) {

        Intent pictureFileIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        checkExternalState(context);
        Uri pictureFileUri = getOutputPictureFileUri(directoryInPictures, context);
        pictureFileIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureFileUri);

        return pictureFileIntent;
    }

    private static void checkExternalState(Context context) {
        //TODO change "mounted" to some static variable somewhere like xxx.MEDIAMOUNTED
        if(!Environment.getExternalStorageState().equals("mounted")){
            Toast.makeText(context, "External storage not found," + Environment.getExternalStorageState(), Toast.LENGTH_LONG).show();
        }
    }

    protected static Uri getOutputPictureFileUri(String directoryInPictures, Context context) {
        return Uri.fromFile(getOutputPictureFile(directoryInPictures, context));
    }

    private static File getOutputPictureFile(String directoryInPictures, Context context){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), directoryInPictures);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Toast.makeText(context, "Save directory could not be created", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        // Create a media pictureFile name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_CAMERAHANDLER_"+ timeStamp + ".jpg");

        return mediaFile;
    }

    /*
    Use the intent that was created by this Class with getPictureFileIntent.
    Set inSampleSize to 1 if you don't want the picture scaled down. Set it to >1 otherwise.
     */
    public static Bitmap getBitmap(Intent pictureFileIntent, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Uri pictureFileUri = (Uri) getFilePathFromIntent(pictureFileIntent);
        Bitmap bitmap = BitmapFactory.decodeFile(pictureFileUri.getPath(), options);
        return bitmap;
    }

    public static Uri getFilePathFromIntent(Intent pictureFileIntent){
        Bundle bundle = pictureFileIntent.getExtras();
        return (Uri) bundle.get(MediaStore.EXTRA_OUTPUT);
    }
}


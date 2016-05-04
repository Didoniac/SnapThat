package com.example.umyhpuscdi.snapthat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by umyhblomti on 2016-05-02.
 */
public class ThingToPhotograph{

    private static final String X_MASHAPE_KEY = "J5ykIANsTimshEs1KFCqdavnWsDPp1vY2ILjsn26bsA9ElIZJw";

    private String mName;

    private boolean isPhotographed = false;
    private boolean uploadedAndChecked = false;
    private boolean accepted = false;
    private Uri mFilePath;

    public String getmName() {
        return mName;
    }

    public ThingToPhotograph(String name){
        mName = name;
    }

    public boolean isPhotographed() {
        return isPhotographed;
    }

    public boolean isUploadedAndChecked() {
        return uploadedAndChecked;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public Uri getmFilePath() {
        return mFilePath;
    }

    public void setmFilePath(Uri filePath) {
        this.mFilePath = filePath;
        isPhotographed = true;
    }

    /*
    Creates an Asynctask to upload the picture.
    When the asyntask is done, postExecutePicToWordPOST is run which
    updates:
    uploadedAndChecked
    accepted
     */
    public void uploadAndCheck(){
        if(isPhotographed) {
            PicToWordAsyncTask asyncTask = new PicToWordAsyncTask();
            asyncTask.execute(getBitmap());
        }else {
            Log.i("ThingToPhotograph", "picture hasn't been taken yet, supply a filepath with setmFilePath");
        }
    }

    public File getFile(){
        return new File(mFilePath.getPath());
    }

    public Bitmap getBitmap(){

        //TODO set inSamplesize to be dynamic
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(mFilePath.getPath(), options);
        return bitmap;
    }

    private void onPostExecuteUploadAndCheck(String jsonString){
        Log.i("HTTP SHIT", jsonString);
        if(doesJsonContainWord(jsonString)){
            accepted = true;
        }else {
            accepted = false;
        }
        uploadedAndChecked = true;
    }

    private boolean doesJsonContainWord(String jsonString){
        return true;
    }

    private class PicToWordAsyncTask extends AsyncTask<Bitmap, Void, String>{

        private static final String API_URL = "https://quasiris-image-recognition-automatic-picture-labeling-v1.p.mashape.com/classify_upload?plain=1";
        URL url;
        HttpURLConnection connection;

        @Override
        protected String doInBackground(Bitmap... params) {

            //Create URL
            try{
                url = new URL(API_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            //Create Connection
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Setup request
            try {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);
                connection.setDoInput(true);
                //change to send a picture-file
                //connection.setRequestProperty("Content-type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("X-Mashape-Key", "J5ykIANsTimshEs1KFCqdavnWsDPp1vY2ILjsn26bsA9ElIZJw");

            } catch (ProtocolException e) {
                e.printStackTrace();
            }

            //Upload
            try {
                connection.connect();

                /*
                OutputStream outStream = connection.getOutputStream();
                params[0].compress(Bitmap.CompressFormat.JPEG, 50, outStream);
                outStream.close();
                */

                OutputStream outputStream = connection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                bufferedWriter.write();

                /*
                Scanner result = new Scanner(connection.getInputStream());
                String response = result.nextLine();
                Log.i("ImageUploader", "Error uploading image: " +response);
                result.close();
                */

                InputStreamReader input = new InputStreamReader(connection.getInputStream());
                BufferedReader buffr = new BufferedReader(input);

                String inputLine;
                StringBuffer strBuffr = new StringBuffer();
                while((inputLine = buffr.readLine()) != null){
                    strBuffr.append(inputLine);
                }
                buffr.close();
                String response = strBuffr.toString();

                return response;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            onPostExecuteUploadAndCheck(jsonString);
        }
    }
}

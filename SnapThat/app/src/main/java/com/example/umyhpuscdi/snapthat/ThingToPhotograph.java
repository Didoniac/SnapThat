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

/**
 * Created by umyhblomti on 2016-05-02.
 */
public class ThingToPhotograph{

    private String mName;

    private boolean isPhotographed = false;
    private boolean uploadedAndChecked = false;
    private boolean accepted = false;
    private String mFilePath;

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

    public String getmFilePath() {
        return mFilePath;
    }

    public void setmFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
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
            asyncTask.execute(getFile());
        }else {
            Log.i("ThingToPhotograph", "picture hasn't been taken yet, supply a filepath with setmFilePath");
        }
    }

    public File getFile(){
        return new File(mFilePath);
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

    private class PicToWordAsyncTask extends AsyncTask<File, Void, String>{

        private static final String API_URL = "https://quasiris-image-recognition-automatic-picture-labeling-v1.p.mashape.com/classify_upload?plain=1";
        URL url;
        HttpURLConnection connection;

        @Override
        protected String doInBackground(File... params) {
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
                connection.setDoInput(true);
                //change to send a picture-file
                //connection.setRequestProperty("Content-type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }

            //Upload
            try {
                connection.connect();

                OutputStream outStream = connection.getOutputStream();
                OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
                BufferedWriter buffWriter = new BufferedWriter(outStreamWriter);

                buffWriter.write(params[2]);
                buffWriter.flush();
                buffWriter.close();

                outStream.close();

                InputStreamReader input = new InputStreamReader(connection.getInputStream());
                BufferedReader buffR = new BufferedReader(input);
                String inputLine;
                StringBuffer strBuff = new StringBuffer();

                while((inputLine = buffR.readLine()) != null){
                    strBuff.append(inputLine);
                }
                buffR.close();

                jsonReturnString = strBuff.toString();

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

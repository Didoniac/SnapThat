package com.example.umyhpuscdi.snapthat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;


/**
 * Created by umyhblomti on 2016-05-02.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThingToPhotograph{

    public static final String UNCHECKED_VALUE = "unchecked";

    private static final String X_MASHAPE_KEY = "J5ykIANsTimshEs1KFCqdavnWsDPp1vY2ILjsn26bsA9ElIZJw";

    private String mSearchTerm;
    private String mTitle;
    private boolean photographed = false;
    private boolean uploadedAndChecked = false;
    private String bestGuess = UNCHECKED_VALUE;
    private boolean uploading = false;
    private boolean accepted = false;
    private PostDownloadAPIGuessExecuteListener mListener;
    private int index;

    @JsonIgnore
    private Uri mFilePath;

    public String getBestGuess() {
        return bestGuess;
    }

    public int getIndex() {
        return index;
    }

    public interface PostDownloadAPIGuessExecuteListener{
        void postAPIGuess(ThingToPhotograph self, boolean accepted, String bestGuess);
    }

    public String getmSearchTerm() {
        return mSearchTerm;
    }

    public String getmTitle() {
        return mTitle;
    }

    public ThingToPhotograph(String title, String searchTerm, PostDownloadAPIGuessExecuteListener listener){
        mTitle = title;
        mSearchTerm = searchTerm;
        mListener = listener;
    }

    //Required empty constructor for Gson
    public ThingToPhotograph() {

    }

    public ThingToPhotograph(String bitmapByteArrayString, int index, boolean accepted, String bestGuess) {
        this.accepted = accepted;
        this.bestGuess = bestGuess;
        this.index = index;

        Bitmap bitmap = getBitmapFromString(bitmapByteArrayString);

        String path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                + "/SnapThat/";
        OutputStream fOut;
        File file = new File(path, bestGuess + "_" + index + "_" + Calendar.getInstance().getTimeInMillis()); // the File to save to (unique name)
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            setmFilePath(Uri.fromFile(file));
            try {
                fOut.flush();
                fOut.close(); // close the stream
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap = null;
    }

    public boolean isPhotographed() {
        return photographed;
    }

    public boolean isUploadedAndChecked() {
        return uploadedAndChecked;
    }

    public boolean isUploading() {
        return uploading;
    }

    public boolean isAccepted() {
        return accepted;
    }

    @JsonIgnore
    public Uri getmFilePath() {
        return mFilePath;
    }

    @JsonIgnore
    public void setmFilePath(Uri filePath) {
        this.mFilePath = filePath;
        photographed = true;
    }

    public void setmListener(PostDownloadAPIGuessExecuteListener listener) {
        this.mListener = listener;
    }

    /*
    Creates an Asynctask to upload the picture.
    When the asyntask is done, postExecutePicToWordPOST is run which
    updates:
    uploadedAndChecked
    accepted

    After that mListeners method "postAPIGuess" is run
     */
    public void uploadAndCheck(){
        if(photographed) {
            uploading = true;
            PicToWordAsyncTask asyncTask = new PicToWordAsyncTask();
            asyncTask.execute(getFile());
        }else {
            Log.i("ThingToPhotograph", "picture hasn't been taken yet, supply a filepath with setmFilePath");
        }
    }

    @JsonIgnore
    public File getFile(){
        return new File(mFilePath.getPath());
    }

    public Bitmap getBitmap(int inSampleSize){
        if(photographed) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;

            Bitmap bitmap = BitmapFactory.decodeFile(mFilePath.getPath(), options);
            return bitmap;
        }else {
            return null;
        }
    }

    private void onPostExecuteUploadAndCheck(String jsonString){
        if(jsonString == null) {
            return;
        }else{
            int indexScore = jsonString.indexOf("score");
            String jsonStringWithoutImage =  jsonString.substring(indexScore);
            jsonString = jsonStringWithoutImage;

            if(doesJsonContainWord(jsonString)){
                accepted = true;
            }else {
                accepted = false;
            }
        }
        uploadedAndChecked = true;
        uploading = false;
        setBestGuessFromJson(jsonString);
        mListener.postAPIGuess(this, accepted, bestGuess);
    }

    private boolean doesJsonContainWord(String jsonString){
        if(jsonString.contains(this.mSearchTerm)){
            return true;
        }else {
            return false;
        }
    }

    @JsonIgnore
    private void setBestGuessFromJson(String jsonStringWithoutImage){
        int firstTextIndex = jsonStringWithoutImage.indexOf("text\":");
        int firstWordIndex = firstTextIndex + 7;
        String firstWordPlusJunk = jsonStringWithoutImage.substring(firstWordIndex);
        int firstWordEndIndex = firstWordPlusJunk.indexOf('"');
        bestGuess = firstWordPlusJunk.substring(0, firstWordEndIndex);
    }

    private class PicToWordAsyncTask extends AsyncTask<File, Void, String>{

        private static final String API_URL = "https://quasiris-image-recognition-automatic-picture-labeling-v1.p.mashape.com/classify_upload?plain=1";
        URL url;

        @Override
        protected String doInBackground(File... params) {

            //Create URL
            try{
                url = new URL(API_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            String charset = "UTF_8";
            MultipartUtility multipart = null;
            try {
                multipart = new MultipartUtility(API_URL, charset);

                //add your file here.
                /*This is to add file content*/
                multipart.addFilePart("imagefile", params[0]);

                String response = multipart.finish().toString();

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

    private Bitmap getBitmapFromString(String stringPicture) {
        /*
        * This Function converts the String back to Bitmap
        * */
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public void removeSavedImageFile() {
        if (mFilePath != null) {
            File file = new File(mFilePath.getPath());
            boolean deleted = file.delete();
            if (deleted) {
                Log.i("TAG", "Removed file successfully: " + mFilePath.getPath());
            } else {
                Log.e("TAG", "Failed to remove file: " + mFilePath.getPath());
            }
        }
    }
}

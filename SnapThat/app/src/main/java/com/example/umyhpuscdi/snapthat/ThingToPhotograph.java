package com.example.umyhpuscdi.snapthat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

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
public class ThingToPhotograph{

    private static final String X_MASHAPE_KEY = "J5ykIANsTimshEs1KFCqdavnWsDPp1vY2ILjsn26bsA9ElIZJw";

    private String mSearchTerm;
    private String mTitle;
    private boolean isPhotographed = false;
    private boolean uploadedAndChecked = false;
    private String mBestGuess = "unchecked";
    private boolean isUploading = false;
    private boolean accepted = false;
    private Uri mFilePath;
    private PostDownloadAPIGuessExecuteListener mListener;
    private int index;

    public String getBestGuess() {
        return mBestGuess;
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

    public ThingToPhotograph(Bitmap bitmap, int index, boolean accepted, String bestGuess) {
        this.accepted = accepted;
        this.mBestGuess = bestGuess;
        this.index = index;

        String path = Environment.getExternalStorageDirectory().toString();
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
    }

    public boolean isPhotographed() {
        return isPhotographed;
    }

    public boolean isUploadedAndChecked() {
        return uploadedAndChecked;
    }

    public boolean isUploading() {
        return isUploading;
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

    After that mListeners method "postAPIGuess" is run
     */
    public void uploadAndCheck(){
        if(isPhotographed) {
            isUploading = true;
            PicToWordAsyncTask asyncTask = new PicToWordAsyncTask();
            asyncTask.execute(getFile());
        }else {
            Log.i("ThingToPhotograph", "picture hasn't been taken yet, supply a filepath with setmFilePath");
        }
    }

    public File getFile(){
        return new File(mFilePath.getPath());
    }

    public Bitmap getBitmap(int inSampleSize){
        if(isPhotographed) {
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
        isUploading = false;
        setBestGuessFromJson(jsonString);
        mListener.postAPIGuess(this, accepted, mBestGuess);
    }

    private boolean doesJsonContainWord(String jsonString){
        if(jsonString.contains(this.mSearchTerm)){
            return true;
        }else {
            return false;
        }
    }

    private void setBestGuessFromJson(String jsonStringWithoutImage){
        int firstTextIndex = jsonStringWithoutImage.indexOf("text\":");
        int firstWordIndex = firstTextIndex + 7;
        String firstWordPlusJunk = jsonStringWithoutImage.substring(firstWordIndex);
        int firstWordEndIndex = firstWordPlusJunk.indexOf('"');
        mBestGuess = firstWordPlusJunk.substring(0, firstWordEndIndex);
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
}

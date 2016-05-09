package com.example.umyhpuscdi.snapthat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
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
            asyncTask.execute(getFile());
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

            String charset = "UTF_8";
            MultipartUtility multipart = null;
            try {
                multipart = new MultipartUtility(API_URL, charset);

                //add your file here.
                /*This is to add file content*/
                multipart.addFilePart("imagefile", params[0]);

                List<String> response = multipart.finish();

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
            File file = new File("path/to/your/file.txt");
            try {
                HttpClient client = new DefaultHttpClient();
                String postURL = "http://someposturl.com";
                HttpPost post = new HttpPost(postURL);
                FileBody bin = new FileBody(file);
                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                reqEntity.addPart("myFile", bin);
                post.setEntity(reqEntity);
                HttpResponse response = client.execute(post);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    Log.i("RESPONSE", EntityUtils.toString(resEntity));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/

/*
            MultipartBody response = Unirest.post("https://quasiris-image-recognition-automatic-picture-labeling-v1.p.mashape.com/classify_upload?plain=1")
                    .header("X-Mashape-Key", "aTaq4JgPedmshFlJznk83KutM6hWp1mPGnejsn8lEIHLCFHR6q")
                    .field("imagefile", params[0]);
                    //.asJson();

            return response.toString();



            /*
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
                //connection.setRequestProperty("Content-type", "imagefile");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("X-Mashape-Key", "J5ykIANsTimshEs1KFCqdavnWsDPp1vY2ILjsn26bsA9ElIZJw");

            } catch (ProtocolException e) {
                e.printStackTrace();
            }

/*
            try
            {
                final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

                RequestBody requestBody = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addFormDataPart("fullname", args.getString("fullname"))
                        .addFormDataPart("email", args.getString("email"))
                        .addFormDataPart("password", args.getString("password"))
                        .addFormDataPart("confpassword",  args.getString("confpassword"))
                        .addFormDataPart("pic", "profile.png", RequestBody.create(MEDIA_TYPE_PNG, (File) args.get("pic")))
                        .build();

                Request request = new Request.Builder()
                        .url(Utils.host_api + args.getString("action"))
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                return new JSONObject(response.body().string());
            }
            catch (Exception e)
            {
                Utils.logStackTrace(e);
                return null;
            }


            //Upload
            try {

                connection.connect();

                //skicka bild som hela grej men inte multipart
                //OutputStream outStream = connection.getOutputStream();
                //params[0].compress(Bitmap.CompressFormat.JPEG, 50, outStream);
                //outStream.close();

/*
                OutputStream outputStream = connection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(params[0].getWidth() * params[0].getHeight());
                params[0].compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);

                //byte[] message = stream.toByteArray();



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
            }*/

            return null;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            onPostExecuteUploadAndCheck(jsonString);
        }
    }
}

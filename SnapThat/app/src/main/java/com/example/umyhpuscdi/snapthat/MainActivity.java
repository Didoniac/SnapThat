package com.example.umyhpuscdi.snapthat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static final int PICTURE_REQUEST_CODE = 100;
    Intent pictureIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takeAPicture();

    }

    private void takeAPicture() {
        pictureIntent = CameraHandler.getPictureFileIntent(this, "SnapThat");
        startActivityForResult(pictureIntent, PICTURE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICTURE_REQUEST_CODE){
            APIMagicToTextView();
        }
    }

    private void APIMagicToTextView() {
        ThingToPhotograph photo = new ThingToPhotograph("chair");
        photo.setmFilePath(CameraHandler.getFilePathFromIntent(pictureIntent));
        photo.uploadAndCheck();
    }
}

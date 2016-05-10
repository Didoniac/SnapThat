package com.example.umyhpuscdi.snapthat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ThingToPhotograph.PostDownloadAPIGuessExecuteListener{

    private static final int PICTURE_REQUEST_CODE = 100;
    Intent pictureIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button picButt = (Button)findViewById(R.id.TESTBUTTON);
        picButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAPicture();
            }
        });

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
        ThingToPhotograph photo = new ThingToPhotograph("computer mouse", "mouse", this);
        photo.setmFilePath(CameraHandler.getFilePathFromIntent(pictureIntent));
        photo.uploadAndCheck();
    }

    @Override
    public void postAPIGuess(boolean accepted, String crappyJsonGuesses) {
        TextView tv = (TextView)findViewById(R.id.TESTTEXTVIEW);

        String text;
        if(accepted){
            text = "CORRECT!\n";
        }else {
            text = "THATS NO COMPUTER MOUSE! ITS SOMETHING MORE LIKE : \n";
        }
        text = text + crappyJsonGuesses;
        tv.setText(text);
    }
}

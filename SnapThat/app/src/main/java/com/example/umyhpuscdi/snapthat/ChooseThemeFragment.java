package com.example.umyhpuscdi.snapthat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by umyhpuscdi on 2016-05-03.
 */
public class ChooseThemeFragment extends Fragment {

    private Button addValueButton;
    ImageView imgV;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle) {
        View rootView =
                layoutInflater.inflate(R.layout.choosethemefragment_layout, container, false);

        final MainActivity mainActivity = (MainActivity) getActivity();

        addValueButton = (Button) rootView.findViewById(R.id.addValueButton);
        imgV = (ImageView)rootView.findViewById(R.id.TEST_IMAGEVIEW);

        addValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.photoAndSend();
            }
        });

        return rootView;
    }

    public void setImageTest(Bitmap bitmap) {
        imgV.setImageBitmap(bitmap);
    }

    public Button getAddValueButton() {
        return addValueButton;
    }
}

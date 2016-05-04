package com.example.umyhpuscdi.snapthat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by umyhpuscdi on 2016-05-03.
 */
public class ChooseThemeFragment extends Fragment {

    private Button defaultThemeButton;
    ImageView imgV;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle) {
        View rootView =
                layoutInflater.inflate(R.layout.choosethemefragment_layout, container, false);

        final MainActivity mainActivity = (MainActivity) getActivity();

        defaultThemeButton = (Button) rootView.findViewById(R.id.defaultThemeButton);
        imgV = (ImageView)rootView.findViewById(R.id.TEST_IMAGEVIEW);

        defaultThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChooseThemeFragment chooseThemeFragment = new ChooseThemeFragment();
                FragmentTransaction fragmentTransaction =
                        mainActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.mainLayout, chooseThemeFragment).commit();

                // Gammal testning
                // mainActivity.photoAndSend();
            }
        });

        return rootView;
    }

    public void setImageTest(Bitmap bitmap) {
        imgV.setImageBitmap(bitmap);
    }

    public Button getDefaultThemeButton() {
        return defaultThemeButton;
    }
}

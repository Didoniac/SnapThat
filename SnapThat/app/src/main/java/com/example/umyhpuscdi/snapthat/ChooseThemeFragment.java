package com.example.umyhpuscdi.snapthat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by umyhpuscdi on 2016-05-03.
 */
public class ChooseThemeFragment extends Fragment {

    private Button addValueButton;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle) {
        View rootView =
                layoutInflater.inflate(R.layout.choosethemefragment_layout, container, false);

        final MainActivity mainActivity = (MainActivity) getActivity();

        addValueButton = (Button) rootView.findViewById(R.id.addValueButton);

        addValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.addValueAndSendToOthers();
            }
        });

        return rootView;
    }

    public Button getAddValueButton() {
        return addValueButton;
    }
}

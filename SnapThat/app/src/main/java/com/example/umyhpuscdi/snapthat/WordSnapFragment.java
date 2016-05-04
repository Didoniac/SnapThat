package com.example.umyhpuscdi.snapthat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by umyhpuscdi on 2016-05-04.
 */
public class WordSnapFragment extends Fragment {

    private TextView timeLeftTextView, wordTextView;
    private Button skipButton, snapButton;
    private ArrayList<ThingToPhotograph> thingsToPhotograph = new ArrayList<>();

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle) {
        View rootView = layoutInflater.inflate(R.layout.wordsnapfragment_layout, container, false);
        final MainActivity mainActivity = (MainActivity) getActivity();

        timeLeftTextView = (TextView) rootView.findViewById(R.id.timeLeftTextView);
        wordTextView = (TextView) rootView.findViewById(R.id.wordTextView);
        skipButton = (Button) rootView.findViewById(R.id.skipButton);
        snapButton = (Button) rootView.findViewById(R.id.snapButton);

        String[] thingsToPhotographStrings = mainActivity.getResources().getStringArray(R.array.office);

        for (String thingToPhotographString : thingsToPhotographStrings) {
            thingsToPhotograph.add(new ThingToPhotograph(thingToPhotographString));
        }
        Collections.shuffle(thingsToPhotograph);

    //    wordTextView.setText(thingsToPhotograph.get(0).); //TODO get the name of the thing to photograph

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        snapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }

    public String showNextWord() { //TODO set text of wordTextView to next word.
        return "";
    }
}

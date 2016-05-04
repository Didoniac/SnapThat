package com.example.umyhpuscdi.snapthat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private MainActivity mainActivity;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle) {
        View rootView = layoutInflater.inflate(R.layout.wordsnapfragment_layout, container, false);
        mainActivity = (MainActivity) getActivity();

        timeLeftTextView = (TextView) rootView.findViewById(R.id.timeLeftTextView);
        wordTextView = (TextView) rootView.findViewById(R.id.wordTextView);
        skipButton = (Button) rootView.findViewById(R.id.skipButton);
        snapButton = (Button) rootView.findViewById(R.id.snapButton);

        String[] thingsToPhotographStrings = mainActivity.getResources().getStringArray(R.array.office);

        for (String thingToPhotographString : thingsToPhotographStrings) {
            thingsToPhotograph.add(new ThingToPhotograph(thingToPhotographString));
        }
        Collections.shuffle(thingsToPhotograph);

        //get the name of the thing to photograph
        wordTextView.setText(thingsToPhotograph.get(0).getmName());

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextWord();
            }
        });

        snapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.photoAndSend();
            }
        });

        return rootView;
    }

    /**
     * set text of wordTextView to next word.
     */
    public String showNextWord() {
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < thingsToPhotograph.size(); i++) {
            temp.add(thingsToPhotograph.get(i).getmName());
        }

        int nextIndex = temp.indexOf(wordTextView.getText().toString()) + 1;

        if (thingsToPhotograph.size() > nextIndex) {
            String newString = thingsToPhotograph.get(nextIndex).getmName();
            wordTextView.setText(newString);
            return newString;
        } else {
            Toast.makeText(mainActivity, "There are no items left to photograph.",
                    Toast.LENGTH_SHORT).show();

            return thingsToPhotograph.get(thingsToPhotograph.size() - 1).getmName();
        }

    }
}

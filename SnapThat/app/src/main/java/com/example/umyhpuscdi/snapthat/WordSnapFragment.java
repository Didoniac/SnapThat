package com.example.umyhpuscdi.snapthat;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Created by umyhpuscdi on 2016-05-04.
 */
public class WordSnapFragment extends Fragment {

    private TextView timeLeftTextView, wordTextView;
    private Button skipButton, snapButton;
    private ArrayList<ThingToPhotograph> thingsToPhotograph = new ArrayList<>();
    private MainActivity mainActivity;
    private Toast toast;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle) {
        View rootView = layoutInflater.inflate(R.layout.wordsnapfragment_layout, container, false);
        mainActivity = (MainActivity) getActivity();

        timeLeftTextView = (TextView) rootView.findViewById(R.id.timeLeftTextView);
        wordTextView = (TextView) rootView.findViewById(R.id.wordTextView);
        skipButton = (Button) rootView.findViewById(R.id.skipButton);
        snapButton = (Button) rootView.findViewById(R.id.snapButton);

        timeLeftTextView.setText(R.string.time_left);

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //120000 = 2 minuters timer. Andra parametern (1000) gör så att det dröjer 1 sekund mellan varje onTick.
        CountDownTimer timer = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String tempTimeLeftString = getString(R.string.time_left);
                final String timeLeftString = String.format(getResources().getConfiguration().locale,
                        tempTimeLeftString
                                + "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        timeLeftTextView.setText(timeLeftString);
                    }
                });

                if (millisUntilFinished <= 10000) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(getContext(), timeLeftString, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, (int) -(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) * 20));
                    toast.show();
                }
            }

            @Override
            public void onFinish() {
                //TODO Show results view from here
                Toast.makeText(getContext(), "Time's up!", Toast.LENGTH_LONG).show();
                String tempTimeLeftString = getString(R.string.time_left);
                tempTimeLeftString += "00:00";
                timeLeftTextView.setText(tempTimeLeftString);
            }
        };

        timer.start();
    }

    public void onResume() {
        super.onResume();
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

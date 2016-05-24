package com.example.umyhpuscdi.snapthat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultFragment extends Fragment {

    MainActivity mainActivity;
    TextView themeTextView;
    TextView wordTextView;
    ListView listView;
    Button prevButton;
    Button nextButton;
    Button scoreButton;

    int mCurrentWordIndex = 0;

    public ResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle) {
        View rootView = layoutInflater.inflate(R.layout.fragment_results, container, false);
        mainActivity = (MainActivity) getActivity();

        themeTextView = (TextView) rootView.findViewById(R.id.results_theme_textview);
        wordTextView = (TextView) rootView.findViewById(R.id.results_word_textview);
        listView = (ListView) rootView.findViewById(R.id.results_listview);
        prevButton = (Button) rootView.findViewById(R.id.results_prev_button);
        nextButton = (Button) rootView.findViewById(R.id.results_next_button);
        scoreButton = (Button) rootView.findViewById(R.id.results_score_button);

        themeTextView.setText("Bodyparts?");
        String word = mainActivity.playerData.getThingsToPhotograph().get(mCurrentWordIndex).getmTitle();
        wordTextView.setText(word);

        mainActivity.resultsListViewAdapter = new ResultsListAdapter(mainActivity, R.layout.listitem_results, mainActivity.getPlayerDatas(), mCurrentWordIndex);
        listView.setAdapter(mainActivity.resultsListViewAdapter);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentWordIndex > 0){
                    mCurrentWordIndex--;
                    wordTextView.setText(mainActivity.playerData.getThingsToPhotograph().get(mCurrentWordIndex).getmTitle());
                    mainActivity.resultsListViewAdapter.setmThingIndex(mCurrentWordIndex);
                    mainActivity.resultsListViewAdapter.notifyDataSetChanged();
                }
                if(mCurrentWordIndex == 0){
                    prevButton.setEnabled(false);
                }
                if(mCurrentWordIndex < mainActivity.playerData.getThingsToPhotograph().size()){
                    nextButton.setEnabled(true);
                }
            }
        });
        prevButton.setEnabled(false);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentWordIndex < mainActivity.playerData.getThingsToPhotograph().size()-1){
                    mCurrentWordIndex++;
                    wordTextView.setText(mainActivity.playerData.getThingsToPhotograph().get(mCurrentWordIndex).getmTitle());
                    mainActivity.resultsListViewAdapter.setmThingIndex(mCurrentWordIndex);
                    mainActivity.resultsListViewAdapter.notifyDataSetChanged();
                }
                if(mCurrentWordIndex == mainActivity.playerData.getThingsToPhotograph().size()-1){
                    nextButton.setEnabled(false);
                }
                if(mCurrentWordIndex > 0){
                    prevButton.setEnabled(true);
                }
            }
        });

        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to score-fragment with fragment manager
                VictoryFragment victoryFragment = new VictoryFragment();
                mainActivity.setVictoryFragment(victoryFragment);

                FragmentTransaction fragmentTransaction =
                        mainActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainLayout, victoryFragment).commit();

            }
        });

        if(mainActivity.haveIReceivedAllPhotoDataFromEveryone()){
            scoreButton.setEnabled(true);
        }

        return rootView;
    }

    public void enableScoreButton() {
        scoreButton.setEnabled(true);
    }
}

package com.example.umyhpuscdi.snapthat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

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
        View rootView = layoutInflater.inflate(R.layout.newgamemenufragment_layout, container, false);
        mainActivity = (MainActivity) getActivity();

        themeTextView = (TextView) rootView.findViewById(R.id.results_theme_textview);
        wordTextView = (TextView) rootView.findViewById(R.id.results_word_textview);
        listView = (ListView) rootView.findViewById(R.id.results_listview);

        prevButton = (Button) rootView.findViewById(R.id.results_prev_button);
        nextButton = (Button) rootView.findViewById(R.id.results_next_button);
        scoreButton = (Button) rootView.findViewById(R.id.results_score_button);

        mainActivity.resultsListViewAdapter = new ResultsListAdapter(mainActivity, R.layout.listitem_results, mainActivity.getPlayerDatas(), mCurrentWordIndex);
        listView.setAdapter(mainActivity.resultsListViewAdapter);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change listview content to previous word for all players
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change listview content to next word for all players
            }
        });

        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to score-fragment with fragment manager
            }
        });

        return rootView;
    }

}

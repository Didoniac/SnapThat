package com.example.umyhpuscdi.snapthat;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

/**
 * Created by umyhlarsvi on 2016-05-17.
 */
public class VictoryFragment extends Fragment {

    MainActivity mainActivity;
    Button prevButton;
    Button mainmenuButton;
    Button rematchButton;
    ListView finalscoreList;

    public VictoryFragment (){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle) {
        View rootView = layoutInflater.inflate(R.layout.victoryfragment_layout, container, false);
        mainActivity = (MainActivity) getActivity();

        prevButton = (Button)rootView.findViewById(R.id.victory_prev_button);
        mainmenuButton = (Button)rootView.findViewById(R.id.victory_backtomain_button);
        rematchButton = (Button)rootView.findViewById(R.id.victory_rematch_button);
        finalscoreList = (ListView)rootView.findViewById(R.id.finalscore_list);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Return to ResultFragment

                ResultFragment resultFragment = new ResultFragment();
                mainActivity.setResultFragment(resultFragment);

                FragmentTransaction fragmentTransaction =
                        mainActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.mainLayout, resultFragment).commit();
            }
        });

        mainmenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Disconnect from room and go to main menu
            }
        });

        finalscoreList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //For rematch, go to NewGameMenuFragment while in the same room
            }
        });


        return rootView;
    }
}

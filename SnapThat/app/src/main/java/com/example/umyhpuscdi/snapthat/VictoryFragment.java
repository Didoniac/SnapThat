package com.example.umyhpuscdi.snapthat;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;

import java.util.ArrayList;
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
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.mainLayout, resultFragment).commit();
            }
        });

        mainmenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Disconnect from room and go to main menu
                mainActivity.leave(mainActivity.googleApiClient, mainActivity, mainActivity.room.getRoomId());

                mainActivity.getPlayerDatas().clear();
                mainActivity.getPlayerDatas().add(mainActivity.playerData);
                if (mainActivity.newGameMenuFragment != null) {
                    mainActivity.getSupportFragmentManager().beginTransaction().remove(mainActivity.newGameMenuFragment).commit();
                    mainActivity.newGameMenuFragment = null;
                }
                mainActivity.setReturningToMainMenu(true);
                mainActivity.onBackPressed();
            }
        });

        rematchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //For rematch, go to NewGameMenuFragment while in the same room

                FragmentTransaction fragmentTransaction = mainActivity.getSupportFragmentManager().beginTransaction();
                mainActivity.newGameMenuFragment = new NewGameMenuFragment();

                fragmentTransaction.replace(R.id.mainLayout, mainActivity.newGameMenuFragment).commit();
                mainActivity.clearThingsToPhotographFiles();

                if (mainActivity.getPlayerDatas().size() == 0) {
                    ArrayList<Participant> participants = mainActivity.room.getParticipants();
                    PlayerData playerData;
                    for (int i = 0; i < participants.size(); i++) {
                        //If current player
                        if (participants.get(i).getParticipantId().equals(
                                mainActivity.room.getParticipantId(Games.Players.getCurrentPlayerId(mainActivity.googleApiClient)))) {
                            mainActivity.getPlayerDatas().add(mainActivity.playerData);
                            playerData = mainActivity.playerData;

                        //If someone else
                        } else {
                            playerData = new PlayerData(participants.get(i).getParticipantId(),participants.get(i).getDisplayName());
                            mainActivity.getPlayerDatas().add(playerData);
                        }
                        playerData.getThingsToPhotograph().clear();
                        playerData.setScore(0);
                        playerData.setNumberOfPhotos(0);
                        playerData.setReady(false);
                    }
                }

                mainActivity.readyUpListViewAdapter.notifyDataSetChanged();
            }
        });


        return rootView;
    }
}

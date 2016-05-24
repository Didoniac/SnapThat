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
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by umyhpuscdi on 2016-05-09.
 */
public class NewGameMenuFragment extends Fragment {

    private MainActivity mainActivity;
    private Button chooseThemeButton;
    protected Button goButton;
    private ListView readyUpListView;
    private boolean beingDestroyed = false;
    protected TextView infoMessageTextView;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle) {
        View rootView = layoutInflater.inflate(R.layout.newgamemenufragment_layout, container, false);
        mainActivity = (MainActivity) getActivity();

        chooseThemeButton = (Button) rootView.findViewById(R.id.chooseThemeButton);
        goButton = (Button) rootView.findViewById(R.id.goButton);
        readyUpListView = (ListView) rootView.findViewById(R.id.readyUpListView);
        infoMessageTextView = (TextView) rootView.findViewById(R.id.infoMessageTextView);

        mainActivity.readyUpListViewAdapter
                = new ReadyUpListAdapter(mainActivity, R.layout.readyup_list_item, mainActivity.getPlayerDatas(), mainActivity);
        readyUpListView.setAdapter(mainActivity.readyUpListViewAdapter);

        chooseThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.inThemeView = true;
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.mainLayout, new ChooseThemeFragment()).commit();
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get the words
                String[] thingsToPhotographStrings = mainActivity.getResources().getStringArray(R.array.office);

                for (String thingToPhotographString : thingsToPhotographStrings) {
                    mainActivity.playerData.getThingsToPhotograph().add(
                            new ThingToPhotograph(thingToPhotographString, thingToPhotographString, mainActivity));
                }
                //Shuffle the list
                Collections.shuffle(mainActivity.playerData.getThingsToPhotograph());

                //Convert list to JSON
                ArrayList<String> stringArrayList = new ArrayList<>();
                ObjectMapper objectMapper = new ObjectMapper();
                for (int i=0; i < mainActivity.playerData.getThingsToPhotograph().size(); i++) {
                    try {
                        stringArrayList.add(objectMapper.writeValueAsString(mainActivity.playerData.getThingsToPhotograph().get(i)));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
                JSONArray jsonThingsToPhotographArray = new JSONArray(stringArrayList);
                JSONObject jsonMessage = new JSONObject();
                try {
                    jsonMessage.put("contentType",MainActivity.startGameMessage);
                    jsonMessage.put("contents",jsonThingsToPhotographArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Send the list to the others as a byte[]
                mainActivity.sendReliableMessage(
                        mainActivity.googleApiClient,mainActivity,jsonMessage.toString().getBytes(),mainActivity.room.getRoomId(),null);

                //Start wordsnapfragment
                WordSnapFragment wordSnapFragment = new WordSnapFragment();
                mainActivity.setWordSnapFragment(wordSnapFragment);
                FragmentTransaction fragmentTransaction =
                mainActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainLayout, wordSnapFragment).commit();
            }
        });

        return rootView;
    }


    @Override
    public void onDestroy() {
        beingDestroyed = true;
        mainActivity.readyUpListViewAdapter = null;
        mainActivity.getPlayerDatas().clear();
        super.onDestroy();
    }


    public boolean isBeingDestroyed() {
        return beingDestroyed;
    }

    public void setBeingDestroyed(boolean beingDestroyed) {
        this.beingDestroyed = beingDestroyed;
    }
}

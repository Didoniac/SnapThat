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

/**
 * Created by umyhpuscdi on 2016-05-09.
 */
public class NewGameMenuFragment extends Fragment {

    private MainActivity mainActivity;
    private Button chooseThemeButton, goButton;
    private ListView readyUpListView;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle) {
        View rootView = layoutInflater.inflate(R.layout.newgamemenufragment_layout, container, false);
        mainActivity = (MainActivity) getActivity();

        chooseThemeButton = (Button) rootView.findViewById(R.id.chooseThemeButton);
        goButton = (Button) rootView.findViewById(R.id.goButton);
        readyUpListView = (ListView) rootView.findViewById(R.id.readyUpListView);

        //initiate ListView with adapter containing readyup_list_items representing players.
        readyUpListView.setAdapter(new ReadyUpListAdapter(mainActivity, R.layout.readyup_list_item, mainActivity.getPlayers()));

        chooseThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.mainLayout, new ChooseThemeFragment()).commit();
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordSnapFragment wordSnapFragment = new WordSnapFragment();
                mainActivity.setWordSnapFragment(wordSnapFragment);
                FragmentTransaction fragmentTransaction =
                        mainActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.mainLayout, wordSnapFragment).commit();
            }
        });

        return rootView;
    }
}

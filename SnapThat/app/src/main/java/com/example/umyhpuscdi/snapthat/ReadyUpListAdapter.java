package com.example.umyhpuscdi.snapthat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by umyhpuscdi on 2016-05-09.
 */
public class ReadyUpListAdapter extends ArrayAdapter<PlayerData> {

    private MainActivity mainActivity;
    private boolean checkboxWasJustClicked = false;
    private PlayerData currentPlayerData;

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            currentPlayerData.setReady(isChecked);

            if (checkboxWasJustClicked) {
                //send message to the other players that the player has readied up!
                mainActivity.sendPlayerDataToOthers();
            }

            checkboxWasJustClicked = false;
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkboxWasJustClicked = true;
        }
    };

    public ReadyUpListAdapter(Context context, int resource, List<PlayerData> playerDatas, MainActivity mainActivity) {
        super(context, resource, playerDatas);
        this.mainActivity = mainActivity;
        currentPlayerData = mainActivity.playerData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.readyup_list_item, parent, false);
        }

        currentPlayerData = getItem(position);

        if (currentPlayerData != null) {
            TextView participantUsernameTextView = (TextView) v.findViewById(R.id.participantUsernameTextView);

            if (participantUsernameTextView != null) {
                participantUsernameTextView.setText(currentPlayerData.getUsername());
            }

            CheckBox readyCheckBox = (CheckBox) v.findViewById(R.id.readyCheckBox);

            if (readyCheckBox != null) {
                readyCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
                readyCheckBox.setOnClickListener(onClickListener);
                readyCheckBox.setChecked(currentPlayerData.isReady());

                if (currentPlayerData.getPlayerID().equals(mainActivity.playerData.getPlayerID())) {
                    readyCheckBox.setEnabled(true);
                } else {
                    readyCheckBox.setEnabled(false);
                }
            }
        }

        return v;
    }
}

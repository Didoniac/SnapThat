package com.example.umyhpuscdi.snapthat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by umyhpuscdi on 2016-05-09.
 */
public class ReadyUpListAdapter extends ArrayAdapter<PlayerData> {

    private MainActivity mainActivity;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isChecked = ((CheckBox)v).isChecked();
            mainActivity.playerData.setReady(isChecked);

            //Enable go button if everyone is ready.
            if (mainActivity.shouldStartGame()) {
                mainActivity.newGameMenuFragment.goButton.setEnabled(true);
            } else {
                mainActivity.newGameMenuFragment.goButton.setEnabled(false);
            }

            //send message to the other players that the player has readied up!
            mainActivity.sendReadyDataToOthers();
        }
    };

    public ReadyUpListAdapter(Context context, int resource, List<PlayerData> playerDatas, MainActivity mainActivity) {
        super(context, resource, playerDatas);
        this.mainActivity = mainActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.readyup_list_item, parent, false);
        }

        PlayerData currentPlayerData = getItem(position);

        if (currentPlayerData != null) {
            TextView participantUsernameTextView = (TextView) v.findViewById(R.id.participantUsernameTextView);

            if (participantUsernameTextView != null) {
                participantUsernameTextView.setText(currentPlayerData.getUsername());
            }

            CheckBox readyCheckBox = (CheckBox) v.findViewById(R.id.readyCheckBox);

            if (readyCheckBox != null) {
                readyCheckBox.setOnClickListener(onClickListener);
                readyCheckBox.setChecked(currentPlayerData.isReady());

                if (currentPlayerData.getPlayerID().equals(mainActivity.playerData.getPlayerID())
                        && mainActivity.newGameMenuFragment.infoMessageTextView.getText().equals(getContext().getString(R.string.connected_to_room))) {
                    readyCheckBox.setEnabled(true);
                } else {
                    readyCheckBox.setEnabled(false);
                }
            }
        }

        return v;
    }
}

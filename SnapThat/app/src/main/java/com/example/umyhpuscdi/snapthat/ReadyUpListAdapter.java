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
public class ReadyUpListAdapter extends ArrayAdapter<Player> {

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //TODO
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO send message to the other players that the player has readied up!
        }
    };

    public ReadyUpListAdapter(Context context, int resource, List<Player> players) {
        super(context, resource, players);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.readyup_list_item, parent, false);
        }

        Player player = getItem(position);

        if (player != null) {
            TextView participantUsernameTextView = (TextView) v.findViewById(R.id.participantUsernameTextView);

            if (participantUsernameTextView != null) {
                participantUsernameTextView.setText(player.getUsername());
            }

            CheckBox readyCheckBox = (CheckBox) v.findViewById(R.id.readyCheckBox);

            if (readyCheckBox != null) {
                readyCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
                readyCheckBox.setOnClickListener(onClickListener);
                readyCheckBox.setChecked(player.isReady());
            }

            TextView statusTextView = (TextView) v.findViewById(R.id.statusTextView);
            if (player.hasJoined()) {
                statusTextView.setText(R.string.joined);
            } else {
                statusTextView.setText(R.string.invited);
            }
        }

        return v;
    }
}

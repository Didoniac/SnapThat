package com.example.umyhpuscdi.snapthat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by umyhpuscdi on 2016-05-02.
 */
public class MainMenuFragment extends Fragment {

    private TextView signedInOrOutTextView;
    private Button quickGameButton, invitePlayersButton, showInvitationsButton, quitButton;
    private MainActivity mainActivity;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle) {
        View rootView = layoutInflater.inflate(R.layout.mainmenufragment_layout,container,false);
        mainActivity = (MainActivity) getActivity();

        signedInOrOutTextView = (TextView) rootView.findViewById(R.id.signedInOrOutTextView);
        quickGameButton = (Button) rootView.findViewById(R.id.quickGameButton);
        invitePlayersButton = (Button) rootView.findViewById(R.id.invitePlayersButton);
        showInvitationsButton = (Button) rootView.findViewById(R.id.showInvitationsButton);
        quitButton = (Button) rootView.findViewById(R.id.quitButton);

        quickGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.startQuickGame();
            }
        });

        invitePlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.invitePlayers();
            }
        });

        showInvitationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showInvitations();
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.finish();
            }
        });

        return rootView;
    }

    public void setGreeting(String string) {
        signedInOrOutTextView.setText(string);
    }

    public void setNewGameButtonsClickable(boolean clickable) {
        try {
            quickGameButton.setClickable(clickable);
            invitePlayersButton.setClickable(clickable);
            showInvitationsButton.setClickable(clickable);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mainActivity.isSignedIn()) {
            signedInOrOutTextView.setText(getString(R.string.signed_in));
            setNewGameButtonsClickable(mainActivity.isSignedIn());
        } else {
            signedInOrOutTextView.setText(getString(R.string.signed_out));
            setNewGameButtonsClickable(mainActivity.isSignedIn());
        }
    }
}

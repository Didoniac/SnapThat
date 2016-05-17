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
    private boolean googlePlayConnected = false;
    private boolean cameraPermissionGranted = false;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle) {
        View rootView = layoutInflater.inflate(R.layout.mainmenufragment_layout,container,false);
        final MainActivity mainActivity = (MainActivity) getActivity();

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

        updateNewGameButtonsClickableState();

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

    public void updateNewGameButtonsClickableState() {
        if(this.googlePlayConnected && this.cameraPermissionGranted) {
            try {
                enableButtons();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }else {
            try {
                disableButtons();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void disableButtons() {
        quickGameButton.setClickable(false);
        invitePlayersButton.setClickable(false);
        showInvitationsButton.setClickable(false);

        quickGameButton.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark_disabled));
        invitePlayersButton.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark_disabled));
        showInvitationsButton.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark_disabled));
    }

    private void enableButtons() {
        quickGameButton.setClickable(true);
        invitePlayersButton.setClickable(true);
        showInvitationsButton.setClickable(true);

        quickGameButton.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
        invitePlayersButton.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
        showInvitationsButton.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
    }



    public void setGooglePlayConnected(boolean isConnected){
        this.googlePlayConnected = isConnected;
        updateNewGameButtonsClickableState();
    }

    public void setCameraPermissionGranted(boolean permissionGranted){
        this.cameraPermissionGranted = permissionGranted;
        updateNewGameButtonsClickableState();
    }

}

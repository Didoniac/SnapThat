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
    private Button newGameButton;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle) {
        View rootView = layoutInflater.inflate(R.layout.mainmenufragment_layout,container,false);

        signedInOrOutTextView = (TextView) rootView.findViewById(R.id.signedInOrOutTextView);
        newGameButton = (Button) rootView.findViewById(R.id.newGameButton);

        return rootView;
    }

    public void setGreeting(String string) {
        signedInOrOutTextView.setText(string);
    }

    public void setNewGameButtonClickable(boolean clickable) {
        try {
            newGameButton.setClickable(clickable);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}

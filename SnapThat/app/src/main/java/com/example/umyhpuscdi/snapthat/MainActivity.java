package com.example.umyhpuscdi.snapthat;

import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;

public class MainActivity
        extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private MainMenuFragment mainMenuFragment;
    private GoogleApiClient googleApiClient;

    // request codes we use when invoking an external activity
    private static final int RC_RESOLVE = 5000;
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    // Are we currently resolving a connection failure?
    private boolean resolvingConnectionFailure = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the Google API Client with access to Games
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        mainMenuFragment = new MainMenuFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout,
                mainMenuFragment).commit();
        mainMenuFragment.setNewGameButtonClickable(false);
    }

    private boolean isSignedIn() {
        return (googleApiClient != null && googleApiClient.isConnected());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TAG", "onStart(): connecting");
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TAG", "onStop(): disconnecting");
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN || requestCode == RC_RESOLVE) {
            resolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                googleApiClient.connect();
            } else {
                Log.e("TAG","Error!\nrequestCode: " + requestCode + "\nresultCode: " + resultCode);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("TAG", "onConnected(): connected to Google APIs");

        // Set the greeting appropriately on main menu
        Player p = Games.Players.getCurrentPlayer(googleApiClient);
        String displayName;
        if (p == null) {
            Log.w("TAG", "Games.Players.getCurrentPlayer() is NULL!");
            displayName = "???";
        } else {
            displayName = p.getDisplayName();
        }
        Toast.makeText(MainActivity.this, "Welcome " + displayName + "!", Toast.LENGTH_SHORT).show();
        mainMenuFragment.setGreeting(getString(R.string.signed_in));
        mainMenuFragment.setNewGameButtonClickable(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("TAG", "onConnectionSuspended(): attempting to connect");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mainMenuFragment.setNewGameButtonClickable(false);
        Log.d("TAG", "onConnectionFailed(): attempting to resolve");
        if (resolvingConnectionFailure) {
            Log.d("TAG", "onConnectionFailed(): already resolving");
            return;
        } else {

            resolvingConnectionFailure = true;
            if (connectionResult.hasResolution()) {
                try {
                    // !!!
                    connectionResult.startResolutionForResult(this, RC_RESOLVE);

                } catch (IntentSender.SendIntentException e) {
                    googleApiClient.connect();
                }
            } else {
                Log.e("TAG", "Connection failure not resolvable.");
            }
        }

        // Sign-in failed, so show sign-in button on main menu
        mainMenuFragment.setGreeting(getString(R.string.signed_out));
    }
}

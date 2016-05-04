package com.example.umyhpuscdi.snapthat;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        RoomUpdateListener,
        RealTimeMultiplayer,
        RealTimeMessageReceivedListener,
        RoomStatusUpdateListener {

    private MainMenuFragment mainMenuFragment;
    private ChooseThemeFragment chooseThemeFragment;
    private GoogleApiClient googleApiClient;

    //Photo
    private static final int IMG_TAKEN_CODE = 100;

    // request codes we use when invoking an external activity
    private static final int RC_RESOLVE = 5000;
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    // request code for the "select players" UI
    // can be any number as long as it's unique
    private final static int RC_SELECT_PLAYERS = 10000;

    // request code (can be any number, as long as it's unique)
    private final static int RC_INVITATION_INBOX = 10001;

    // at least 2 players required for our game
    private final static int MIN_PLAYERS = 2;
    private final static int MAX_PLAYERS = 4;

    // Are we currently resolving a connection failure?
    private boolean resolvingConnectionFailure = false;

    // are we already playing?
    private boolean playing = false;
    private Player player;
    private ArrayList<String> otherPlayerIds;
    private Room room;

    private int value = 0;

    //Photo
    private Intent latestPicIntent;

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
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,
                mainMenuFragment).commit();
        mainMenuFragment.setNewGameButtonsClickable(false);
    }

    private boolean isSignedIn() {
        return (googleApiClient != null && googleApiClient.isConnected());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!googleApiClient.isConnected()) {
            Log.d("TAG", "onStart(): connecting");
        googleApiClient.connect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "onDestroy(): disconnecting");
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN || requestCode == RC_RESOLVE) {
            resolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                googleApiClient.connect();
            } else {
                Log.e("TAG","Error!\nrequestCode: " + requestCode + "\nresultCode: " + resultCode);
            }
        } else if (requestCode == RC_SELECT_PLAYERS) {
            if (resultCode != RESULT_OK) {
                // user canceled
                return;
            }

            // get the invitee list
            Bundle extras = data.getExtras();
            final ArrayList<String> invitees =
                    data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get auto-match criteria
            Bundle autoMatchCriteria = null;
            int minAutoMatchPlayers =
                    data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers =
                    data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            // create the room and specify a variant if appropriate
            RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
            roomConfigBuilder.addPlayersToInvite(invitees);
            if (autoMatchCriteria != null) {
                roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
            }
            RoomConfig roomConfig = roomConfigBuilder.build();
            Games.RealTimeMultiplayer.create(googleApiClient, roomConfig);

            // prevent screen from sleeping during handshake
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            Toast.makeText(MainActivity.this, "Invited " + invitees.size() + " player(s).",
                    Toast.LENGTH_SHORT).show();

            otherPlayerIds = invitees;

            // go to game screen
            chooseThemeFragment = new ChooseThemeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,
                    chooseThemeFragment).commit();

        } else if (requestCode == RC_INVITATION_INBOX) {
            if (resultCode != Activity.RESULT_OK) {
                // canceled
                return;
            }

            // get the selected invitation
            Bundle extras = data.getExtras();
            Invitation invitation =
                    extras.getParcelable(Multiplayer.EXTRA_INVITATION);

            // accept it!
            RoomConfig roomConfig = null;
            if (invitation != null) {
                roomConfig = makeBasicRoomConfigBuilder()
                        .setInvitationIdToAccept(invitation.getInvitationId())
                        .build();
            } else {
                //Invitation doesn't exist
                return;
            }
            Games.RealTimeMultiplayer.join(googleApiClient, roomConfig);

            // prevent screen from sleeping during handshake
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // go to game screen
            chooseThemeFragment = new ChooseThemeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,
                    chooseThemeFragment).commit();
        }else if(requestCode == IMG_TAKEN_CODE){
            if (resultCode != Activity.RESULT_OK || !isSignedIn()) {
                // canceled
                return;
            }
            Bitmap bitmap = CameraHandler.getBitmap(latestPicIntent, 4);
            ByteArrayOutputStream stream =
                    new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
            byte[] message = stream.toByteArray();
            sendReliableMessage(googleApiClient, null, message, null, null);
            chooseThemeFragment.setImageTest(bitmap);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("TAG", "onConnected(): connected to Google APIs");

        // Set the greeting appropriately on main menu
        player = Games.Players.getCurrentPlayer(googleApiClient);
        String displayName;
        if (player == null) {
            Log.w("TAG", "Games.Players.getCurrentPlayer() is NULL!");
            displayName = "???";
        } else {
            displayName = player.getDisplayName();
        }
        Toast.makeText(MainActivity.this, "Welcome " + displayName + "!", Toast.LENGTH_SHORT).show();
        mainMenuFragment.setGreeting(getString(R.string.signed_in));
        mainMenuFragment.setNewGameButtonsClickable(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("TAG", "onConnectionSuspended(): attempting to connect");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mainMenuFragment.setNewGameButtonsClickable(false);
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

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        if (statusCode == GamesStatusCodes.STATUS_OK) {

            this.room = room;

        } else {
            // let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            //TODO show error message, return to main screen.

        }
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        if (statusCode == GamesStatusCodes.STATUS_OK) {

            this.room = room;

        } else {
            // let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            //TODO show error message, return to main screen.
        }
    }

    @Override
    public void onLeftRoom(int i, String s) {
    //    room = null;
    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        if (statusCode == GamesStatusCodes.STATUS_OK) {
            this.room = room;
        } else {
            // let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            //TODO show error message, return to main screen.
        }
    }

    @Override
    public Intent getWaitingRoomIntent(GoogleApiClient googleApiClient, Room room, int i) {
        return null;
    }

    @Override
    public Intent getSelectOpponentsIntent(GoogleApiClient googleApiClient, int i, int i1) {
        return null;
    }

    @Override
    public Intent getSelectOpponentsIntent(GoogleApiClient googleApiClient, int i, int i1, boolean b) {
        return null;
    }

    @Override
    public void create(GoogleApiClient googleApiClient, RoomConfig roomConfig) {

    }

    @Override
    public void join(GoogleApiClient googleApiClient, RoomConfig roomConfig) {

    }

    @Override
    public void leave(GoogleApiClient googleApiClient, RoomUpdateListener roomUpdateListener, String s) {

    }

    @Override
    public int sendReliableMessage(GoogleApiClient googleApiClient,
                                   ReliableMessageSentCallback reliableMessageSentCallback,
                                   byte[] message, String roomId, String participantId) {

        for (Participant p : room.getParticipants()) {
            //Send the byte[] message to everyone except yourself.
            if (!p.getParticipantId().equals(player.getPlayerId())) {
                Games.RealTimeMultiplayer.sendReliableMessage(googleApiClient, null, message,
                        room.getRoomId(), p.getParticipantId());
            }
        }

        return 0;
    }

    @Override
    public int sendUnreliableMessage(GoogleApiClient googleApiClient, byte[] bytes, String s, String s1) {
        return 0;
    }

    @Override
    public int sendUnreliableMessage(GoogleApiClient googleApiClient, byte[] bytes, String s, List<String> list) {
        return 0;
    }

    @Override
    public int sendUnreliableMessageToOthers(GoogleApiClient googleApiClient, byte[] bytes, String s) {
        return 0;
    }

    @Override
    public void declineInvitation(GoogleApiClient googleApiClient, String s) {

    }

    @Override
    public void dismissInvitation(GoogleApiClient googleApiClient, String s) {

    }

    public void startQuickGame() {
        // auto-match criteria to invite one random automatch opponent.
        // You can also specify more opponents (up to 3).
        Bundle am = RoomConfig.createAutoMatchCriteria(MIN_PLAYERS - 1, MAX_PLAYERS - 1, 0);

        // build the room config:
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setAutoMatchCriteria(am);
        RoomConfig roomConfig = roomConfigBuilder.build();

        // create room:
        Games.RealTimeMultiplayer.create(googleApiClient, roomConfig);

        // prevent screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // go to game screen
        chooseThemeFragment = new ChooseThemeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,
                chooseThemeFragment).commit();
    }

    public void invitePlayers() {
        // launch the player selection screen
        // minimum: 1 other player; maximum: 3 other players
        Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(googleApiClient, MIN_PLAYERS - 1, MAX_PLAYERS - 1);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    public void showInvitations() {
        // launch the intent to show the invitation inbox screen
        Intent intent = Games.Invitations.getInvitationInboxIntent(googleApiClient);
        startActivityForResult(intent, RC_INVITATION_INBOX);
    }

    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        // get real-time message
        byte[] b = realTimeMessage.getMessageData();

        // process message
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        chooseThemeFragment.setImageTest(bitmap);

        /*Old testing of sending integers
        value = byteArrayToInt(b);
        String s = "" + value;
        chooseThemeFragment.getAddValueButton().setText(s);
        */
    }

    public static int byteArrayToInt(byte[] b) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }

    @Override
    public void onRoomConnecting(Room room) {

    }

    @Override
    public void onRoomAutoMatching(Room room) {

    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) {

    }

    @Override
    public void onPeerDeclined(Room room, List<String> list) {

    }

    @Override
    public void onPeerJoined(Room room, List<String> list) {

    }

    @Override
    public void onPeerLeft(Room room, List<String> list) {

    }

    @Override
    public void onConnectedToRoom(Room room) {

    }

    @Override
    public void onDisconnectedFromRoom(Room room) {

    }

    @Override
    public void onPeersConnected(Room room, List<String> list) {

    }

    @Override
    public void onPeersDisconnected(Room room, List<String> list) {

    }

    @Override
    public void onP2PConnected(String s) {

    }

    @Override
    public void onP2PDisconnected(String s) {

    }

    public void addValueAndSendToOthers() {
        value++;
        String s = "" + value;
        chooseThemeFragment.getAddValueButton().setText(s);

        //Make a byte[] out of the int to be sent.
        byte[] message = ByteBuffer.allocate(4).putInt(value).array();

        // broadcast the new value to the other players.
        sendReliableMessage(googleApiClient, null, message, null, null);
    }

    public int getValue() {
        return value;
    }

    public void photoAndSend() {
        latestPicIntent = CameraHandler.getPictureFileIntent(this, "SnapThat");
        startActivityForResult(latestPicIntent, IMG_TAKEN_CODE);
    }
}

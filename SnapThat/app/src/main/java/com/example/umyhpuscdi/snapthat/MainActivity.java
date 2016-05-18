package com.example.umyhpuscdi.snapthat;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.umyhpuscdi.snapthat.Comparators.ThingToPhotographIndexComparator;
import com.example.umyhpuscdi.snapthat.Serializables.ImageSerializable;
import com.example.umyhpuscdi.snapthat.Serializables.ReadySerializable;
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
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        RoomUpdateListener,
        RealTimeMultiplayer,
        RealTimeMessageReceivedListener,
        RoomStatusUpdateListener,
        RealTimeMultiplayer.ReliableMessageSentCallback,
        ThingToPhotograph.PostDownloadAPIGuessExecuteListener{

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private MainMenuFragment mainMenuFragment;
    private ChooseThemeFragment chooseThemeFragment;
    private WordSnapFragment wordSnapFragment;
    protected NewGameMenuFragment newGameMenuFragment;
    private ResultFragment resultFragment;
    private VictoryFragment victoryFragment;

    protected GoogleApiClient googleApiClient;

    //Photo
    private static final int IMG_TAKEN_CODE = 100;

    // request codes we use when invoking an external activity
    private static final int RC_RESOLVE = 5000;
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    protected static final String startGameMessage = "Start game";

    // request code for the "select players" UI
    // can be any number as long as it's unique
    private final static int RC_SELECT_PLAYERS = 10000;

    // request code (can be any number, as long as it's unique)
    private final static int RC_INVITATION_INBOX = 10001;

    // at least 2 players required for our game
    protected final static int MIN_PLAYERS = 2;
    private final static int MAX_PLAYERS = 4;

    // Are we currently resolving a connection failure?
    private boolean resolvingConnectionFailure = false;

    // are we already playing?
    private boolean playing = false;

    //The user
    protected PlayerData playerData;

    //All players in the room
    private ArrayList<PlayerData> playerDatas = new ArrayList<>();

    protected Room room;
    private RoomConfig roomConfig;

    private int value = 0;

    //Photo
    private Intent latestPicIntent;

    private static final int PICTURE_REQUEST_CODE = 100;
    private Intent pictureIntent;

    protected ArrayAdapter readyUpListViewAdapter;
    protected ArrayAdapter resultsListViewAdapter;
    private boolean timeIsUp = false;
    protected boolean inThemeView = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createMainMenu();
        createGoogleAPIClient();
    }

    private void cameraPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            //ask for permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }else {
            mainMenuFragment.setCameraPermissionGranted(true);
        }
    }

    public boolean isSignedIn() {
        return (googleApiClient != null && googleApiClient.isConnected());
    }

    @Override
    protected void onStart() {
        super.onStart();

        cameraPermission();

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
            roomConfig = roomConfigBuilder.build();
            Games.RealTimeMultiplayer.create(googleApiClient, roomConfig);

            // prevent screen from sleeping during handshake
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            Toast.makeText(MainActivity.this, "Invited " + invitees.size() + " player(s).",
                    Toast.LENGTH_SHORT).show();

            // go to game screen
            newGameMenuFragment = new NewGameMenuFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack("MainMenuFragment");
            fragmentTransaction.replace(R.id.mainLayout,
                    newGameMenuFragment);
            fragmentTransaction.commit();

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
            if (invitation != null) {
                roomConfig = makeBasicRoomConfigBuilder()
                        .setInvitationIdToAccept(invitation.getInvitationId())
                        .build();
            } else {
                //Invitation doesn't exist
                return;
            }
            join(googleApiClient, roomConfig);

            // go to game screen
            newGameMenuFragment = new NewGameMenuFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack("MainMenuFragment");
            fragmentTransaction.replace(R.id.mainLayout, newGameMenuFragment).commit();

        } else if(requestCode == IMG_TAKEN_CODE){
            if(!timeIsUp) {
                if (resultCode != Activity.RESULT_OK || !isSignedIn()) {
                    // canceled
                    return;
                }
                Uri latestPictureUri = CameraHandler.getFilePathFromIntent(latestPicIntent);
                int latestWordIndex = wordSnapFragment.getIndexOfCurrentWord();
                playerData.getThingsToPhotograph().get(latestWordIndex).setmFilePath(latestPictureUri);
                playerData.getThingsToPhotograph().get(latestWordIndex).uploadAndCheck();

                wordSnapFragment.showNextWord();
            }else {
                endCurrentGame();
            }
        }
    }

    public void endCurrentGame() {
        goToResultViewFragment();
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        // Set the greeting appropriately on main menu
        Player player = Games.Players.getCurrentPlayer(googleApiClient);
        this.playerData = new PlayerData(player.getPlayerId(),player.getDisplayName());
        String displayName;

        displayName = player.getDisplayName();

        Toast.makeText(MainActivity.this, "Welcome " + displayName + "!", Toast.LENGTH_SHORT).show();
        mainMenuFragment.setGreeting(getString(R.string.signed_in));
        mainMenuFragment.setGooglePlayConnected(true);

        //If player already accepted invite
        if (connectionHint != null) {
            Invitation inv =
                    connectionHint.getParcelable(Multiplayer.EXTRA_INVITATION);

            if (inv != null) {
                // accept invitation
                RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
                roomConfigBuilder.setInvitationIdToAccept(inv.getInvitationId());
                Games.RealTimeMultiplayer.join(googleApiClient, roomConfigBuilder.build());

                // prevent screen from sleeping during handshake
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                // go to game screen
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack("MainMenuFragment");
                newGameMenuFragment = new NewGameMenuFragment();
                fragmentTransaction.replace(R.id.mainLayout, newGameMenuFragment).commit();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // attempt to connect
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mainMenuFragment.setGooglePlayConnected(false);
        Log.d("TAG", "onConnectionFailed(): attempting to resolve");
        if (resolvingConnectionFailure) {
            Log.d("TAG", "onConnectionFailed(): already resolving");
            return;
        } else {

            resolvingConnectionFailure = true;
            if (connectionResult.hasResolution()) {
                try {
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
            if (!playerDatas.contains(playerData)) {
                playerDatas.add(0,playerData);
            }

        } else {
            // let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            //show error message, return to main screen.
            Toast.makeText(MainActivity.this, "Error in onRoomCreated!", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().popBackStack("MainMenuFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        if (statusCode == GamesStatusCodes.STATUS_OK) {
            this.room = room;
            if (!playerDatas.contains(playerData)) {
                playerDatas.add(0,playerData);
            }

        } else {
            // let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            //show error message, return to main screen.
            Toast.makeText(MainActivity.this, "Error in onJoinedRoom!", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().popBackStack("MainMenuFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

            //show error message, return to main screen.
            Toast.makeText(MainActivity.this, "Error in onRoomConnected!", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().popBackStack("MainMenuFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
        // create room:
        Games.RealTimeMultiplayer.create(googleApiClient, roomConfig);

        // prevent screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void join(GoogleApiClient googleApiClient, RoomConfig roomConfig) {
        Games.RealTimeMultiplayer.join(googleApiClient, roomConfig);

        // prevent screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void leave(GoogleApiClient googleApiClient, RoomUpdateListener roomUpdateListener, String roomId) {
        // leave room
        Games.RealTimeMultiplayer.leave(googleApiClient, roomUpdateListener, roomId);

        // remove the flag that keeps the screen on
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (getSupportFragmentManager().getFragments().contains(newGameMenuFragment)
                && !newGameMenuFragment.isBeingDestroyed()) {
            playerDatas.clear();
            getSupportFragmentManager().popBackStack();
            Toast.makeText(MainActivity.this, "Starting quick game.", Toast.LENGTH_SHORT).show();
            startQuickGame();
        }
    }

    @Override
    public int sendReliableMessage(GoogleApiClient googleApiClient,
                                   ReliableMessageSentCallback reliableMessageSentCallback,
                                   byte[] message, String roomId, String participantId) {

        for (Participant p : room.getParticipants()) {
            //Send the byte[] message to everyone
            Games.RealTimeMultiplayer.sendReliableMessage(googleApiClient, this, message,
                    room.getRoomId(), p.getParticipantId());
        }

        return 0;
    }

    @Override
    public int sendUnreliableMessage(GoogleApiClient googleApiClient, byte[] message, String roomId, String participantId) {
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
        roomConfig = roomConfigBuilder.build();

        // create room:
        create(googleApiClient, roomConfig);

        // go to game screen
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("MainMenuFragment");
        newGameMenuFragment = new NewGameMenuFragment();
        fragmentTransaction.replace(R.id.mainLayout, newGameMenuFragment).commit();
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

        Object receivedObject = null;

        try {
            receivedObject = Serializer.deserialize(b);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (receivedObject != null) {
            if (receivedObject instanceof ReadySerializable) {
                ReadySerializable receivedReadySerializable = (ReadySerializable) receivedObject;
                //Find the player and change it to the new object.
                for (int i = 0; i < playerDatas.size(); i++) {
                    if (receivedReadySerializable.getPlayerID().equals(playerDatas.get(i).getPlayerID())) {
                        playerDatas.get(i).setReady(receivedReadySerializable.isReady());
                    }
                }
                readyUpListViewAdapter.notifyDataSetChanged();
            }
        } else {
            String receivedString = new String(b);
            if (receivedString.equals(startGameMessage)) {
                WordSnapFragment wordSnapFragment = new WordSnapFragment();
                setWordSnapFragment(wordSnapFragment);
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.mainLayout, wordSnapFragment).commit();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(receivedString);
                    if (jsonObject.get("contentType").equals("ImageSerializable")) {
                        JSONObject imageSerializableJsonObject
                                = new JSONObject((String)jsonObject.get("contents"));
                        Gson gson = new Gson();
                        ImageSerializable imageSerializable
                                = gson.fromJson(imageSerializableJsonObject.toString(),ImageSerializable.class);
                        //Loop through the list of players to find who sent the object.
                        PlayerData playerWhoSentTheData = null;
                        for (int i=0; i<playerDatas.size(); i++) {
                            if (playerDatas.get(i).getPlayerID().equals(imageSerializable.getPlayerId())) {
                                playerWhoSentTheData = playerDatas.get(i);
                                break;
                            }
                        }
                        if (playerWhoSentTheData != null) {
                            //add it at the end, then sort by indexes.
                            playerWhoSentTheData.getThingsToPhotograph().add(
                                    new ThingToPhotograph(
                                            imageSerializable.getBitmap(),
                                            imageSerializable.getIndex(),
                                            imageSerializable.isAccepted(),
                                            imageSerializable.getBestGuess()));
                            Collections.sort(playerWhoSentTheData.getThingsToPhotograph(),new ThingToPhotographIndexComparator());
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // process message
    //        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
    //        chooseThemeFragment.setImageTest(bitmap);
        }

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

    // returns whether there are enough players to start the game
    public boolean shouldStartGame() {
        int connectedPlayers = 0;
        if (room != null) {
            for (Participant p : room.getParticipants()) {
                if (p.isConnectedToRoom()) ++connectedPlayers;
            }
        }
        return connectedPlayers >= MIN_PLAYERS;
    }

    /**
     * Returns whether the room is in a state where the game should be canceled.
     */
    public boolean shouldCancelGame(Room room) {
        // Your game-specific cancellation logic here. For example, you might decide to
        // cancel the game if enough people have declined the invitation or left the room.
        // You can check a participant's status with Participant.getStatus().
        // (Also, your UI should have a Cancel button that cancels the game too)
        if (room.getParticipants().size() < MIN_PLAYERS) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRoomConnecting(Room room) {
        newGameMenuFragment.infoMessageTextView.setText(R.string.connecting_to_room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {

    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> participantIds) {
        Participant participant;
        for (int i = 0; i < participantIds.size(); i++) {
            participant = room.getParticipant(participantIds.get(i));

            PlayerData playerData
                    = new PlayerData(participant.getParticipantId(), participant.getDisplayName());
            playerDatas.add(playerData);
            readyUpListViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPeerDeclined(Room room, List<String> participantIds) {
        Participant participant;
        String stringToDisplay = "Players have declined their invite:";
        for (int i = 0; i < participantIds.size(); i++) {
            participant = room.getParticipant(participantIds.get(i));
            stringToDisplay += "\n" + participant.getDisplayName();
            if (participantIds.get(i).equals(playerDatas.get(i).getPlayerID())) {
                playerDatas.remove(i);
                readyUpListViewAdapter.notifyDataSetChanged();
            }
        }
        Toast.makeText(MainActivity.this, stringToDisplay, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPeerJoined(Room room, List<String> participantIds) {
        Participant participant;
        int existsInListAlreadyAtPosition = -1;
        for (int i = 0; i < participantIds.size(); i++) {
            participant = room.getParticipant(participantIds.get(i));

            //Update player list
            for (int j = 0; j < playerDatas.size(); j++) {
                if (participant.getParticipantId().equals(playerDatas.get(j).getPlayerID())) {
                    existsInListAlreadyAtPosition = j;
                }
            }

            if (existsInListAlreadyAtPosition != -1) {
                PlayerData playerData = playerDatas.get(existsInListAlreadyAtPosition);
                readyUpListViewAdapter.notifyDataSetChanged();
            } else {
                PlayerData playerData
                        = new PlayerData(participant.getParticipantId(), participant.getDisplayName());
                playerDatas.add(playerData);
                if(readyUpListViewAdapter!=null) {
                    readyUpListViewAdapter.notifyDataSetChanged();
                }
            }

            //Reset value for next loop
            existsInListAlreadyAtPosition = -1;
        }
    }

    @Override
    public void onPeerLeft(Room room, List<String> participantIds) {
        Participant participant;
        String stringToDisplay = "Players have left the room:";
        for (int i = 0; i < participantIds.size(); i++) {
            participant = room.getParticipant(participantIds.get(i));
            stringToDisplay += "\n" + participant.getDisplayName();
            if (playerDatas.size() > 0) {
                if (participantIds.get(i).equals(playerDatas.get(i).getPlayerID())) {
                    playerDatas.remove(i);
                    readyUpListViewAdapter.notifyDataSetChanged();
                }
            }
        }
        Toast.makeText(MainActivity.this, stringToDisplay, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectedToRoom(Room room) {
        newGameMenuFragment.infoMessageTextView.setText(R.string.connected_to_room);
        if (playerDatas.size() == 0) {
            ArrayList<Participant> participants = room.getParticipants();
            for (int i = 0; i < participants.size(); i++) {
                playerDatas.add(new PlayerData(participants.get(i).getParticipantId(),participants.get(i).getDisplayName()));
            }
        }
        if (!playerDatas.contains(playerData)) {
            playerDatas.add(0, playerData);
        }
        newGameMenuFragment.goButton.setEnabled(true);
        readyUpListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        // leave the room
        leave(googleApiClient, this, room.getRoomId());

        // show error message and return to main screen
        Toast.makeText(MainActivity.this, "You got disconnected.", Toast.LENGTH_SHORT).show();
        getSupportFragmentManager().popBackStack("MainMenuFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if(newGameMenuFragment!=null){
            newGameMenuFragment.onDestroy();
        }
    }

    @Override
    public void onPeersConnected(Room room, List<String> participantIds) {
        Participant participant;
        String stringToDisplay = "Players have connected:";
        for (int i = 0; i < participantIds.size(); i++) {
            participant = room.getParticipant(participantIds.get(i));
            stringToDisplay += "\n" + participant.getDisplayName();
        }
        Toast.makeText(MainActivity.this, stringToDisplay, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> participantIds) {
        Participant participant;
        String stringToDisplay = "Players have disconnected:";
        for (int i = 0; i < participantIds.size(); i++) {
            participant = room.getParticipant(participantIds.get(i));
            stringToDisplay += "\n" + participant.getDisplayName();
        }
        Toast.makeText(MainActivity.this, stringToDisplay, Toast.LENGTH_LONG).show();

        if (shouldCancelGame(room)) {
            leave(googleApiClient, this, room.getRoomId());
        }
    }

    @Override
    public void onP2PConnected(String participantId) {

    }

    @Override
    public void onP2PDisconnected(String participantId) {

    }

    public void addValueAndSendToOthers() {
        value++;
        String s = "" + value;
        chooseThemeFragment.getDefaultThemeButton().setText(s);

        //Make a byte[] out of the int to be sent.
        byte[] message = ByteBuffer.allocate(4).putInt(value).array();

        // broadcast the new value to the other players.
        sendReliableMessage(googleApiClient, this, message, room.getRoomId(), null);
    }

    public int getValue() {
        return value;
    }

    public void sendReadyDataToOthers() {
        byte[] message;
        ReadySerializable readySerializable = new ReadySerializable(playerData);
        try {
            message = Serializer.serialize(readySerializable);
        } catch (IOException e) {
            Log.e("TAG","Error sending player data.");
            e.printStackTrace();
            return;
        }
        // broadcast the object to the other players.
        sendReliableMessage(googleApiClient, this, message, room.getRoomId(), null);
    }

    public void photoAndSend(int indexOfCurrentWord) {
        latestPicIntent = CameraHandler.getPictureFileIntent(this, "SnapThat");
        startActivityForResult(latestPicIntent, IMG_TAKEN_CODE);
    }

    public void setWordSnapFragment(WordSnapFragment wordSnapFragment) {
        this.wordSnapFragment = wordSnapFragment;
    }

    public void setResultFragment(ResultFragment resultFragment) {
        this.resultFragment = resultFragment;
    }

    public void setVictoryFragment(VictoryFragment victoryFragment) {
        this.victoryFragment = victoryFragment;
    }

    public ArrayList<PlayerData> getPlayerDatas() {
        return playerDatas;
    }

    @Override
    public void postAPIGuess(ThingToPhotograph theThing, boolean accepted, String bestGuess) {
        String toastText;
        if(accepted) {
            toastText = "YES!";
        }else {
            toastText = bestGuess;
        }
        Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
        makeAndSendImageSerializable(theThing);
    }

    private void makeAndSendImageSerializable(ThingToPhotograph thingToPhotograph) {
        ImageSerializable imageSerializable = new ImageSerializable(
                playerData.getPlayerID(),
                thingToPhotograph.getBitmap(3),
                playerData.getThingsToPhotograph().indexOf(thingToPhotograph),
                thingToPhotograph.isAccepted(),
                thingToPhotograph.getBestGuess());

        Gson gson = new Gson();
        String imageSerializableString = gson.toJson(imageSerializable);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("contentType","ImageSerializable");
            jsonObject.put("contents",imageSerializableString);
            byte[] bytes = jsonObject.toString().getBytes();
            sendReliableMessage(googleApiClient,this,bytes,room.getRoomId(),null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
        if (statusCode == GamesStatusCodes.STATUS_REAL_TIME_MESSAGE_SEND_FAILED) {
            Log.e("TAG","Error, STATUS_REAL_TIME_MESSAGE_SEND_FAILED (" + statusCode + ")");
        } else if (statusCode == GamesStatusCodes.STATUS_REAL_TIME_ROOM_NOT_JOINED) {
            Log.e("TAG","Error, STATUS_REAL_TIME_ROOM_NOT_JOINED (" + statusCode + ")");
        } else if (statusCode == GamesStatusCodes.STATUS_OK) {
            Log.i("TAG","Message delivered successfully. (" + statusCode + ")");
        }
    }

    private void goToResultViewFragment(){
        ResultFragment resultFragment = new ResultFragment();
        setResultFragment(resultFragment);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.mainLayout, resultFragment).commit();
    }

    public void timeIsUp() {
        this.timeIsUp = true;
    }

    public void timerStarted(){
        this.timeIsUp = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permission granted
                    mainMenuFragment.setCameraPermissionGranted(true);
                }else {
                    cameraPermission();
                }
            }
        }
    }

    private void createGoogleAPIClient() {
        // Create the Google API Client with access to Games
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }

    private void createMainMenu(){
        mainMenuFragment = new MainMenuFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,
                mainMenuFragment).commit();
    }

  public void onBackPressed() {

       if(inThemeView){
           inThemeView = false;
           super.onBackPressed();
       }
       else {
           if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
               if(wordSnapFragment!=null){
                   getSupportFragmentManager().beginTransaction().remove(wordSnapFragment).commit();

               }
               if(resultFragment!=null){
                   getSupportFragmentManager().beginTransaction().remove(resultFragment).commit();

               }
               if(victoryFragment!=null){
                   getSupportFragmentManager().beginTransaction().remove(victoryFragment).commit();

               }
               super.onBackPressed();

               if (room!=null) {
                   leave(googleApiClient, this, room.getRoomId());
               }
           } else {
               super.onBackPressed();
           }
       }
    }
}

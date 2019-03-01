package ca.sfu.pacmacro;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import ca.sfu.pacmacro.API.PacMacroClient;
import ca.sfu.pacmacro.Controller.CharacterDisplayCriteria;
import ca.sfu.pacmacro.Controller.CharacterManager;
import ca.sfu.pacmacro.Controller.GameController;
import ca.sfu.pacmacro.Model.Character;

public class PlayerActivity extends AppCompatActivity {
    private static final int PERMISSION_RESPONSE_CODE = 1;

    private PacMacroClient mApiClient;
    private CharacterManager mCharacterManager;
    private GameController mGameController;
    private CharacterDisplayCriteria mDisplayCriteria;

    private Character.CharacterType mSelectedCharacterType;
    private Character mSelectedCharacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mSelectedCharacterType = (Character.CharacterType) getIntent().getExtras().get("Character");

        mGameController = new GameController();
        mApiClient = new PacMacroClient();
        mDisplayCriteria = new CharacterDisplayCriteria(CharacterDisplayCriteria.CRITERIA_PLAYER);
        mCharacterManager = new CharacterManager(mApiClient, mGameController, mDisplayCriteria);

        mApiClient.selectCharacter(mSelectedCharacterType, 0, 0);
        //TODO: set mSelectedCharacter state

        mGameController.startLoop();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_RESPONSE_CODE);
        }
        else {
            startLocationService(mSelectedCharacterType);
        }

        Button stopButton = (Button) findViewById(R.id.player_stop_service);
        if (stopButton != null) {
            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showStopWarning();
                }
            });
        }

        Button tagButton = (Button) findViewById(R.id.player_tag);
        if (tagButton != null) {
            tagButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Character[] characters = mCharacterManager.getCharacters();

                    AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);
                    builder.setTitle(getString(R.string.player_tag_select));

                    ArrayAdapter<Character> characterArrayAdapter = new ArrayAdapter<Character>(getApplicationContext(),
                                    R.layout.select_dialog_singlechoice, characters);
                    builder.setAdapter(characterArrayAdapter, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            Character character = characters[item];
                            sendTagRequest(character);
                            dialog.dismiss();
                        }
                    });

                    Dialog characterSelectDialog = builder.create();
                    characterSelectDialog.show();
                }
            });
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showStopWarning();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showStopWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.player_activity_stop_warning_title)
                .setMessage(R.string.player_activity_stop_warning_body)
                .setPositiveButton(R.string.player_activity_stop_warning_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        stopPlaying();
                    }
                })
                .setNegativeButton(R.string.player_activity_stop_warning_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void stopPlaying() {
        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
        stopService(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_RESPONSE_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService(mSelectedCharacterType);
            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Really?");
                builder.create().show();
                // TODO: real shit here
            }
        }
    }

    private void startLocationService(Character.CharacterType characterType) {
        Intent intent = new Intent(getApplicationContext(), PlayerService.class);
        intent.putExtra("Character", characterType);
        startService(intent);
    }

    private void sendTagRequest(Character character) {
        Character.CharacterState characterState = Character.CharacterState.CAPTURED;
        mApiClient.updateCharacterState(character.getType(), characterState);
    }

    @Override
    protected void onDestroy() {
        mGameController.stopLoop();
        mApiClient.deselectCharacter(mSelectedCharacterType);
        Intent intent = new Intent(getApplicationContext(), PlayerService.class);
        stopService(intent);
        super.onDestroy();
    }
}

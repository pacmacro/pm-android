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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import ca.sfu.pacmacro.API.PacMacroClient;
import ca.sfu.pacmacro.Controller.CharacterManager;
import ca.sfu.pacmacro.Controller.GameController;
import ca.sfu.pacmacro.Model.Character;

public class PlayerActivity extends AppCompatActivity {
    private static final int PERMISSION_RESPONSE_CODE = 1;

    private PacMacroClient mApiClient;
    private CharacterManager mCharacterManager;
    private GameController mGameController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mGameController = new GameController();
        mApiClient = new PacMacroClient();
        mCharacterManager = new CharacterManager(mApiClient, mGameController);

        mGameController.startLoop();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_RESPONSE_CODE);
        }
        else {
            startLocationService();
        }

        Button stopButton = (Button) findViewById(R.id.player_stop_service);
        if (stopButton != null) {
            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                    stopService(intent);
                    finish();
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
                            Log.d("TagButton", "Character selected: " + character);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_RESPONSE_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
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

    private void startLocationService() {
        Intent intent = new Intent(getApplicationContext(), PlayerService.class);
        startService(intent);
    }

    private void sendTagRequest(int characterId) {
        Character.CharacterState characterState = Character.CharacterState.CAPTURED;
        mApiClient.updateCharacterState(characterId, characterState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGameController.stopLoop();
    }
}

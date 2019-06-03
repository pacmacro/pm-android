package ca.sfu.pacmacro;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

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
    private AppCompatDialog gpsAlertDialog;

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

        registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_RESPONSE_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService(mSelectedCharacterType);
            } else {
                if(!shouldShowRequestPermissionRationale(permissions[0])){
                    // When user clicked "Never ask again"
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getApplicationContext().getString(R.string.warning));
                    builder.setMessage(getApplicationContext().getString(R.string.location_permission_dialog_msg));
                    builder.setNegativeButton(getApplicationContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.create().show();

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_RESPONSE_CODE);
                }
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

    @RequiresApi(api = 28)
    @Override
    protected void onResume() {
        super.onResume();
        showGpsDialog();
    }

    @RequiresApi(api = 28)
    private void showGpsDialog() {
        if(!isLocationEnabled(getApplicationContext())) {
            if(gpsAlertDialog==null) {
                gpsAlertDialog = createGpsAlertDialog();
                gpsAlertDialog.show();
            } else if (!gpsAlertDialog.isShowing()){
                gpsAlertDialog.show();
            }
        } else {
            if(gpsAlertDialog!=null)
                gpsAlertDialog.dismiss();
        }
    }

    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @RequiresApi(api = 28)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                if(isLocationEnabled(getApplicationContext())) {
                    if(gpsAlertDialog!=null)
                        gpsAlertDialog.dismiss();
                } else {
                    showGpsDialog();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        mGameController.stopLoop();
        mApiClient.deselectCharacter(mSelectedCharacterType);
        Intent intent = new Intent(getApplicationContext(), PlayerService.class);
        stopService(intent);
        unregisterReceiver(gpsReceiver);
        super.onDestroy();
    }

    @RequiresApi(api = 28)
    public static Boolean isLocationEnabled(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            // This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }

    AppCompatDialog createGpsAlertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PlayerActivity.this, R.style.Theme_AppCompat_Dialog);
        dialogBuilder.setTitle(getApplicationContext().getString(R.string.warning));
        dialogBuilder.setMessage(getApplicationContext().getString(R.string.gps_alert_dialog_msg));
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(getApplicationContext().getString(R.string.go_to_settings), null)
                .create();

        AppCompatDialog alertDialog = dialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
            }
        });
        return alertDialog;
    }
}


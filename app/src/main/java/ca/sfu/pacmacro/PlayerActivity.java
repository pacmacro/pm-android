package ca.sfu.pacmacro;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Vibrator;

import ca.sfu.pacmacro.API.PacMacroClient;
import ca.sfu.pacmacro.Controller.CharacterDisplayCriteria;
import ca.sfu.pacmacro.Controller.CharacterManager;
import ca.sfu.pacmacro.Controller.GameController;
import ca.sfu.pacmacro.Controller.GameStateCallback;
import ca.sfu.pacmacro.Controller.GameStateManager;
import ca.sfu.pacmacro.Controller.ScoreCallBack;
import ca.sfu.pacmacro.Controller.ScoreManager;
import ca.sfu.pacmacro.Model.Character;

public class PlayerActivity extends BaseActivity {
    private static final int PERMISSION_RESPONSE_CODE = 1;

    private PacMacroClient mApiClient;
    private CharacterManager mCharacterManager;
    private GameController mGameController;
    private CharacterDisplayCriteria mDisplayCriteria;
    private Character.CharacterType mSelectedCharacterType;
    private AppCompatDialog gpsAlertDialog;
    private boolean isPacman = true;
    private TextView mGameState, mPacmanState, mScore;
    private int scoreRecord = 0;

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

        ImageView scoreIcon = findViewById(R.id.pScoreIcon);
        if(mSelectedCharacterType != Character.CharacterType.PACMAN){
            isPacman = false;
            scoreIcon.setImageResource(R.drawable.home_ghost);
        }
        mGameState = findViewById(R.id.gameState);
        mPacmanState = findViewById(R.id.pacmanState);
        mScore = findViewById(R.id.pScoreNum);
        ScoreCallBack scoreCallBack = new ScoreCallBack() {
            @Override
            public void ScoreCallBack(Integer score) {
                if(isPacman || score==0){
                    mScore.setText(score.toString());
                }else{
                    mScore.setText("- " + score.toString());
                }
                if(scoreRecord!=score){
                    scoreRecord = score;
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(400);
                }
            }
        };
        ScoreManager scoreManager = new ScoreManager(mApiClient, scoreCallBack, mGameController);

        GameStateCallback gameStateCallback = new GameStateCallback() {
            @Override
            public void GameStateCallback(int type, String GameState) {
                if (type==0){
                    mGameState.setText(GameState);
                }
                else{
                    mPacmanState.setText(GameState);
                    if(GameState.compareTo("POWERUP")==0){
                        mPacmanState.setTextColor(0xFFFF5555);
                    }else{
                        mPacmanState.setTextColor(0xFFFFFFFF);
                    }
                }
            }
        };
        GameStateManager gameStateManager = new GameStateManager(mApiClient, gameStateCallback, mGameController);

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
        try {
            if (requestCode == PERMISSION_RESPONSE_CODE) {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService(mSelectedCharacterType);
                } else {
                    if (!shouldShowRequestPermissionRationale(permissions[0])) {
                        // When user clicked "Never ask again"
                        showLocationPermissionDialog();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_RESPONSE_CODE);
                    }
                }
            }
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

    void showLocationPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);
        String message = getApplicationContext().getString(R.string.location_permission_dialog_msg);
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(message);
        ssBuilder.setSpan(new ForegroundColorSpan(Color.RED), message.indexOf("Settings"), message.indexOf("Settings") + String.valueOf("Settings").length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        builder.setTitle(getApplicationContext().getString(R.string.warning));
        builder.setMessage(ssBuilder);
        builder.setNegativeButton(getApplicationContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void startLocationService(Character.CharacterType characterType) {
        Intent intent = new Intent(getApplicationContext(), PlayerService.class);
        intent.putExtra("Character", characterType);
        startService(intent);
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
        dialogBuilder.setNegativeButton("Go Back",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        stopPlaying();
                        dialog.cancel();
                    }
                });

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


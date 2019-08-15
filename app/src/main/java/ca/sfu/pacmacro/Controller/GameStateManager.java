package ca.sfu.pacmacro.Controller;

import android.util.Log;

import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ca.sfu.pacmacro.API.PacMacroClient;
import ca.sfu.pacmacro.API.events.CharactersReceivedEvent;
import ca.sfu.pacmacro.API.events.GameStateReceivedEvent;
import ca.sfu.pacmacro.API.model.CharacterData;
import ca.sfu.pacmacro.Model.Character;

public class GameStateManager {
    private static final String TAG = "ScoreManager";
    private PacMacroClient mApiClient;
    private GameStateCallback mCallBack;
    private String mGameState;
    private String mPowerState;
    private GameController mGameController;

    public GameStateManager(PacMacroClient apiClient,GameStateCallback callback, GameController gameController) {
        this.mApiClient = apiClient;
        this.mGameController = gameController;
        this.mCallBack = callback;

        EventBus.getDefault().register(this);
        mGameController.registerAction(new GameLoopAction() {
            @Override
            public void execute() {
                mApiClient.getGameState();
                Log.v(TAG, "Get score request sent");
            }
        });
        mGameController.registerAction(new GameLoopAction() {
            @Override
            public void execute() {
                mApiClient.getCharacters();
                Log.v(TAG, "Get characters request sent");
            }
        });
    }

    @Subscribe
    public void OnGameStateRecieved(GameStateReceivedEvent event){
        mGameState = event.getGameState();
        Log.v(TAG, "Received gameState of " + mGameState);
        mCallBack.GameStateCallback(0, mGameState);
    }

    @Subscribe
    public void OnCharactorRecieved(CharactersReceivedEvent event){
        CharacterData pacman = event.getCharacterDataList().get(0);
        mPowerState = pacman.getState().toString();
        Log.v(TAG, "Received powerState of " + mPowerState);
        mCallBack.GameStateCallback(1, mPowerState);

    }
}

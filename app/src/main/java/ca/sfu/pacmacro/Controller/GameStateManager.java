package ca.sfu.pacmacro.Controller;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ca.sfu.pacmacro.API.PacMacroClient;
import ca.sfu.pacmacro.API.events.GameStateReceivedEvent;

public class GameStateManager {
    private static final String TAG = "ScoreManager";
    private PacMacroClient mApiClient;
    private GameStateCallback mCallBack;
    private String mGameState;
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
    }

    @Subscribe
    public void OnGameStateRecieved(GameStateReceivedEvent event){
        mGameState = event.getGameState();
        Log.v(TAG, "Received gameState of " + mGameState);
        mCallBack.GameStateCallback(mGameState);
    }
}

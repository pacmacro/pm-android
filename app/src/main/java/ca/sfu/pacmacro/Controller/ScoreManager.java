package ca.sfu.pacmacro.Controller;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ca.sfu.pacmacro.API.PacMacroClient;
import ca.sfu.pacmacro.API.events.ScoreReceivedEvent;

public class ScoreManager {
    private static final String TAG = "ScoreManager";
    private PacMacroClient mApiClient;
    private ScoreCallBack mCallBack;
    private int mScore;
    private GameController mGameController;

    public ScoreManager(PacMacroClient apiClient,ScoreCallBack scoreCallBack, GameController gameController){
        this.mApiClient = apiClient;
        this.mGameController = gameController;
        this.mCallBack = scoreCallBack;

        EventBus.getDefault().register(this);

        mGameController.registerAction(new GameLoopAction() {
            @Override
            public void execute() {
                mApiClient.getScore();
                Log.v(TAG, "Get score request sent");
            }
        });
    }

    @Subscribe
    public void onScoreRecieved(ScoreReceivedEvent event){
        int score = event.getScore();
        mCallBack.ScoreCallBack(score);
        Log.v(TAG, "Received score of " + score);
        mScore = score;
    }

}

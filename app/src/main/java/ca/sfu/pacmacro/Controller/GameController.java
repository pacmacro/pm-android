package ca.sfu.pacmacro.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controls main game loop, including pulling data from the API for characters and pellets
 */
public class GameController {
    private static final int TIMER_DELAY = 0;
    private static final int TIMER_PERIOD = 500;

    private Timer timer;
    private List<GameLoopAction> actions;

    public GameController() {
        this.actions = new ArrayList<>();
    }

    public void registerAction(GameLoopAction action) {
        this.actions.add(action);
    }

    public void startLoop() {
        this.timer = new Timer("GameLoop");
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (GameLoopAction action: actions) {
                    action.execute();
                }
            }
        }, TIMER_DELAY, TIMER_PERIOD);
    }
}

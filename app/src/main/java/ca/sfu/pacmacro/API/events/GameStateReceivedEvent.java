package ca.sfu.pacmacro.API.events;

import android.util.Log;

import com.google.gson.JsonObject;

public class GameStateReceivedEvent {
    JsonObject mGameState;

    public GameStateReceivedEvent(JsonObject GameState){
        mGameState = GameState;
        Log.v("GameStateRecievedEvent", "GameState responded");
    }

    public String getGameState() {
        JsonObject temp = mGameState.deepCopy();
        Log.v("GameStateRecievedEvent", mGameState.keySet().toString());
        return temp.remove("state").getAsString();
    }
}

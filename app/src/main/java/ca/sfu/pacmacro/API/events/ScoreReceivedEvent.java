package ca.sfu.pacmacro.API.events;

import com.google.gson.JsonObject;

public class ScoreReceivedEvent {
    JsonObject score;

    public ScoreReceivedEvent(JsonObject scoreList){
        this.score = scoreList;
    }

    public int getScore() {
        return score.remove("score").getAsInt();
    }
}

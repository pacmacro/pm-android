package ca.sfu.pacmacro.API.events;

import com.google.gson.JsonObject;

public class ScoreReceivedEvent {
    JsonObject score;

    public ScoreReceivedEvent(JsonObject scoreList){
        this.score = scoreList;
    }

    public int getScore() {
        JsonObject temp = score.deepCopy();
        return temp.remove("score").getAsInt();
    }
}

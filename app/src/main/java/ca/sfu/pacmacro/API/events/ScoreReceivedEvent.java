package ca.sfu.pacmacro.API.events;

import com.google.gson.JsonObject;

public class ScoreReceivedEvent {
    JsonObject scoreList;

    public ScoreReceivedEvent(JsonObject scoreList){
        this.scoreList = scoreList;
    }

    public String getScoreList() {
        return scoreList.remove("score").getAsString();
    }
}

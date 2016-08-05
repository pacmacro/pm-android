package ca.sfu.pacmacro.API.model;

import android.util.Log;

import ca.sfu.pacmacro.Model.Character;

/**
 * Created by AlexLand on 2016-08-04.
 */
public class CharacterStateData {
    int id;
    Character.CharacterState state;

    public CharacterStateData(int id, String stateString) {
        this.id = id;

        switch(stateString) {
            case "UNINITIALIZED":
                this.state = Character.CharacterState.UNINITIALIZED;
                break;
            case "READY":
                this.state = Character.CharacterState.READY;
                break;
            case "ACTIVE":
                this.state = Character.CharacterState.ACTIVE;
                break;
            case "CAPTURED":
                this.state = Character.CharacterState.CAPTURED;
                break;
            case "POWERUP":
                this.state = Character.CharacterState.POWERUP;
                break;
            default:
                Log.wtf("CharacterStateData", "Character state unknown: " + stateString);
        }
    }

    public int getId() {
        return id;
    }

    public Character.CharacterState getState() {
        return state;
    }
}

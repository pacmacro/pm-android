package ca.sfu.pacmacro.API.model;

import ca.sfu.pacmacro.Model.Character;

/**
 * Created by AlexLand on 2016-08-04.
 */
public class CharacterStateData {
    Character.CharacterType type;
    Character.CharacterState state;

    public CharacterStateData(String name, String stateString) {
        this.type = Character.CharacterType.valueOf(name.toUpperCase());
        this.state = Character.CharacterState.valueOf(stateString.toUpperCase());
    }

    public Character.CharacterType getType() {
        return type;
    }

    public Character.CharacterState getState() {
        return state;
    }
}

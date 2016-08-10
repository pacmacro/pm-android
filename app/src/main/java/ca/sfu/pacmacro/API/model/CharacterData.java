package ca.sfu.pacmacro.API.model;

import com.google.android.gms.maps.model.LatLng;

import ca.sfu.pacmacro.Model.Character;

/**
 * Data structure to store information about a Character fetched from the API
 */
public class CharacterData {
    private Character.CharacterType type;
    private Character.CharacterState state;
    private LatLng location;

    public CharacterData(String type, String state, float latitude, float longitude) {
        this.type = Character.CharacterType.valueOf(type.toUpperCase());
        this.state = Character.CharacterState.valueOf(state.toUpperCase());
        this.location = new LatLng(latitude, longitude);
    }

    public LatLng getLocation() {
        return location;
    }

    public Character.CharacterType getType() {
        return type;
    }

    public Character.CharacterState getState() {
        return state;
    }
}

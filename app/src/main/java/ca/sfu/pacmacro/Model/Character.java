package ca.sfu.pacmacro.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Data structure containing a character's information and position
 */
public class Character {
    private Marker marker;
    private CharacterType characterType;
    private CharacterState characterState;

    public enum CharacterType{
        PACMAN("Pacman"),
        INKY("Inky"),
        BLINKY("Blinky"),
        PINKY("Pinky"),
        CLYDE("Clyde");

        String name;

        CharacterType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum CharacterState{
        UNINITIALIZED,
        READY,
        ACTIVE,
        CAPTURED,
        POWERUP
    }

    public Character(CharacterType characterType, Marker marker) {
        this.characterType = characterType;
        this.marker = marker;
    }

    public boolean isPacman() {
        return characterType == CharacterType.PACMAN;
    }

    public void updateLocation(LatLng characterLocation) {
        if (marker != null) {
            marker.setPosition(characterLocation);
        }
    }

    public void updateState(CharacterState characterState) {
        this.characterState = characterState;
    }

    @Override
    public String toString() {
        return characterType.toString();
    }

    public CharacterState getState() {
        return characterState;
    }

    public CharacterType getType() {
        return characterType;
    }
}

package ca.sfu.pacmacro.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Data structure containing a character's information and position
 */
public class Character {
    private int id;
    private Marker marker;
    private CharacterType characterType;

    public enum CharacterType{
        PACMAN,
        INKY,
        BLINKY,
        PINKY,
        CLYDE
    }
    public Character(int id, CharacterType characterType, Marker marker) {
        this.id = id;
        this.characterType = characterType;
        this.marker = marker;
    }

    public int getId() {
        return id;
    }

    public boolean isPacman() {
        return characterType == CharacterType.PACMAN;
    }

    public void updateLocation(LatLng characterLocation) {
        marker.setPosition(characterLocation);
    }

}

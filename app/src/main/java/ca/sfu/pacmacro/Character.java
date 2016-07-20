package ca.sfu.pacmacro;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import ca.sfu.pacmacro.API.model.Id;

/**
 * Created by AlexLand on 2016-07-19.
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
        CLYDE;
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

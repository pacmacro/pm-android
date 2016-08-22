package ca.sfu.pacmacro.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import ca.sfu.pacmacro.R;

/**
 * Data structure containing a character's information and position
 */
public class Character {
    private Marker marker;
    private CharacterType characterType;
    private CharacterState characterState;

    public enum CharacterType{
        PACMAN("Pacman", R.drawable.pacman),
        INKY("Inky", R.drawable.inky),
        BLINKY("Blinky", R.drawable.blinky),
        PINKY("Pinky", R.drawable.pinky),
        CLYDE("Clyde", R.drawable.clyde);

        String name;
        int iconId;

        CharacterType(String name, int iconId) {
            this.name = name;
            this.iconId = iconId;
        }

        @Override
        public String toString() {
            return name;
        }

        public int getIconId() {
            return iconId;
        }
    }

    public enum CharacterState{
        UNINITIALIZED,
        READY,
        ACTIVE,
        CAPTURED,
        POWERUP
    }

    public Character(CharacterType characterType, CharacterState characterState, Marker marker) {
        this.characterType = characterType;
        this.characterState = characterState;
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

    public void updateMarkerVisibility() {
        if (characterState == CharacterState.UNINITIALIZED) {
            this.marker.setVisible(false);
        }
        else {
            this.marker.setVisible(true);
        }
    }
}

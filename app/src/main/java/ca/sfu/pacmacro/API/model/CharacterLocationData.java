package ca.sfu.pacmacro.API.model;

import com.google.android.gms.maps.model.LatLng;

import ca.sfu.pacmacro.Model.Character;

/**
 * Created by AlexLand on 2016-07-14.
 */
public class CharacterLocationData {
    private Character.CharacterType type;
    private LatLng location;

    public CharacterLocationData(String name, float latitude, float longitude) {
        this.type = Character.CharacterType.valueOf(name.toUpperCase());
        this.location = new LatLng(latitude, longitude);
    }

    public CharacterLocationData(LatLng location) {
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }

    public Character.CharacterType getType() {
        return type;
    }
}

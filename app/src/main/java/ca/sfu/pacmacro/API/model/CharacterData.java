package ca.sfu.pacmacro.API.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by AlexLand on 2016-07-14.
 */
public class CharacterData {
    private Id id;
    private LatLng location;

    public CharacterData(Id id, float latitude, float longitude) {
        this.id = id;
        this.location = new LatLng(latitude, longitude);
    }

    public CharacterData(LatLng location) {
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }

    public Id getId() {
        return id;
    }
}

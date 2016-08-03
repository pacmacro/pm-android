package ca.sfu.pacmacro.API.model;

import com.google.android.gms.maps.model.LatLng;

import ca.sfu.pacmacro.Model.Pellet;

/**
 * Created by AlexLand on 2016-08-02.
 */
public class PelletData {
    private LatLng location;
    private Pellet.PelletType type;

    public PelletData(String type, double latitude, double longitude) {
        if (type.equals("pill")) {
            this.type = Pellet.PelletType.PILL;
        }
        else {
            this.type = Pellet.PelletType.DOT;
        }

        this.location = new LatLng(latitude, longitude);
    }

    public LatLng getLocation() {
        return location;
    }

    public Pellet.PelletType getType() {
        return type;
    }
}

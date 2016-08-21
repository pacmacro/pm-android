package ca.sfu.pacmacro.API.model;

import com.google.android.gms.maps.model.LatLng;

import ca.sfu.pacmacro.Model.Pellet;

/**
 * Created by AlexLand on 2016-08-02.
 */
public class PelletData {
    private LatLng location;
    private Pellet.PelletType type;
    private boolean eaten;

    public PelletData(boolean powerPill, double latitude, double longitude, boolean eaten) {
        if (powerPill) {
            this.type = Pellet.PelletType.PILL;
        }
        else {
            this.type = Pellet.PelletType.DOT;
        }

        this.location = new LatLng(latitude, longitude);
        this.eaten = eaten;
    }

    public LatLng getLocation() {
        return location;
    }

    public Pellet.PelletType getType() {
        return type;
    }
}

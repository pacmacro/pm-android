package ca.sfu.pacmacro.Model;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by AlexLand on 2016-08-02.
 */
public class Pellet {
    Marker location;
    PelletType type;

    public enum PelletType{
        DOT,
        PILL
    }

    public Pellet(Marker location, PelletType type) {
        this.location = location;
        this.type = type;
    }
}

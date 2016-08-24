package ca.sfu.pacmacro.Model;

import com.google.android.gms.maps.model.Circle;

/**
 * Created by AlexLand on 2016-08-02.
 */
public class Pellet {
    private Circle circle;
    private PelletType type;

    public enum PelletType{
        DOT,
        PILL
    }

    public Pellet(Circle circle, PelletType type) {
        this.circle = circle;
        this.type = type;
    }

    public Circle getCircle() {
        return circle;
    }
}

package ca.sfu.pacmacro.API.events;

import android.location.Location;

/**
 * Created by AlexLand on 2016-07-16.
 */
public class LocationChangedEvent {
    private Location location;

    public LocationChangedEvent(Location location) {
        this.location = location;
    }
}

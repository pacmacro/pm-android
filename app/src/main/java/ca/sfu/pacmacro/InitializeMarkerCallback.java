package ca.sfu.pacmacro;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by AlexLand on 2016-07-19.
 */
public interface InitializeMarkerCallback {
    Marker initializeMarker(LatLng latLng, String name);
}

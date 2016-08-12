package ca.sfu.pacmacro.Controller;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Callback interface for creating markers on the map
 */
public interface InitializeMarkerCallback {
    Marker initializeMarker(LatLng latLng, String name, BitmapDescriptor icon);
}

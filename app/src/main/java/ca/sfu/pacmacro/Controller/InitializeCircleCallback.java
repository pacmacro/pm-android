package ca.sfu.pacmacro.Controller;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by AlexLand on 2016-08-23.
 */
public interface InitializeCircleCallback {
    Circle initializeCircle(LatLng latLng, boolean wide);
}

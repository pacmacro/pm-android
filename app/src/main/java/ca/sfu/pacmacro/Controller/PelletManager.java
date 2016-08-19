package ca.sfu.pacmacro.Controller;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import ca.sfu.pacmacro.API.PacMacroClient;
import ca.sfu.pacmacro.API.events.PelletReceivedEvent;
import ca.sfu.pacmacro.API.model.PelletData;
import ca.sfu.pacmacro.Model.Pellet;
import ca.sfu.pacmacro.R;

/**
 * Track dots and power pills locations
 */
public class PelletManager {
    private static final String TAG = "PelletManager";
    private PacMacroClient mApiClient;
    private List<Pellet> mPelletList;
    private InitializeMarkerCallback mMapCallback;
    private GameController mGameController;

    public PelletManager(PacMacroClient mApiClient, InitializeMarkerCallback mMapCallback, GameController mGameController) {
        this.mApiClient = mApiClient;
        this.mMapCallback = mMapCallback;

        EventBus.getDefault().register(this);

        mGameController.registerAction(new GameLoopAction() {
            @Override
            public void execute() {
                //TODO: fetch pellets
            }
        });
    }

    @Subscribe
    public void onPelletsReceived(PelletReceivedEvent event) {
        mPelletList.clear();

        for (PelletData pelletData: event.getPelletDataList()) {
            //TODO: initialize latlng from pelletData
            LatLng latLng = pelletData.getLocation();
            Pellet.PelletType type = pelletData.getType();
            Marker marker = mMapCallback.initializeMarker(latLng, "", R.drawable.pacman);
            //TODO: initialize pellet with location and type
            Pellet pellet = new Pellet(marker, type);
            Log.d(TAG, "onPelletsReceived: Received pellet of type" + "TYPE");
            mPelletList.add(pellet);
        }
    }
}

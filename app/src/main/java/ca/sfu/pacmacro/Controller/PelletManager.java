package ca.sfu.pacmacro.Controller;

import android.util.Log;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.pacmacro.API.PacMacroClient;
import ca.sfu.pacmacro.API.events.PelletReceivedEvent;
import ca.sfu.pacmacro.API.model.PelletData;
import ca.sfu.pacmacro.Model.Pellet;

/**
 * Track dots and power pills locations
 */
public class PelletManager {
    private static final String TAG = "PelletManager";
    private PacMacroClient mApiClient;
    private List<Pellet> mPelletList = new ArrayList<>();
    private InitializeCircleCallback mMapCallback;
    private GameController mGameController;

    public PelletManager(PacMacroClient apiClient, InitializeCircleCallback mapCallback, GameController gameController) {
        this.mApiClient = apiClient;
        this.mMapCallback = mapCallback;
        this.mGameController = gameController;

        EventBus.getDefault().register(this);

        mGameController.registerAction(new GameLoopAction() {
            @Override
            public void execute() {
                mApiClient.getPellets();
                Log.v(TAG, "Get pellets request sent");
            }
        });
    }

    @Subscribe
    public void onPelletsReceived(PelletReceivedEvent event) {
        clearPellets();

        for (PelletData pelletData: event.getPelletDataList()) {
            LatLng latLng = pelletData.getLocation();
            Pellet.PelletType type = pelletData.getType();
            boolean isVisible = pelletData.isVisible();

            boolean isPowerPill = type == Pellet.PelletType.PILL;
            Circle circle = mMapCallback.initializeCircle(latLng, isPowerPill);
            circle.setVisible(isVisible);

            Pellet pellet = new Pellet(circle, type);
            Log.v(TAG, "Received pellet of type " + type);
            mPelletList.add(pellet);
        }
    }

    private void clearPellets() {
        for (Pellet pellet : mPelletList) {
            pellet.getCircle().remove();
        }
    }
}

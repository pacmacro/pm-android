package ca.sfu.pacmacro;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ca.sfu.pacmacro.API.PacMacroClient;
import ca.sfu.pacmacro.Controller.CharacterManager;
import ca.sfu.pacmacro.Controller.GameController;
import ca.sfu.pacmacro.Controller.InitializeMarkerCallback;

public class SpectatorActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PacMacroClient mApiClient;
    private CharacterManager mCharacterManager;
    private GameController mGameController;

    private int PERMISSION_RESPONSE_CODE = 0;
    private String TAG = "SpectatorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mApiClient = new PacMacroClient();
        mGameController = new GameController();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        InitializeMarkerCallback markerCallback = new InitializeMarkerCallback() {
            @Override
            public Marker initializeMarker(LatLng latLng, String name, int drawableResourceId) {
                Bitmap mDotMarkerBitmap = getBitmapFromDrawable(drawableResourceId);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(name).icon(BitmapDescriptorFactory.fromBitmap(mDotMarkerBitmap));
                return mMap.addMarker(markerOptions);
            }
        };
        mCharacterManager = new CharacterManager(mApiClient, markerCallback, mGameController);

        mGameController.startLoop();
    }

    @NonNull
    private Bitmap getBitmapFromDrawable(int drawableResourceId) {
        int px = getResources().getDimensionPixelSize(R.dimen.character_map_icon_size);
        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = getResources().getDrawable(drawableResourceId);
        shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
        shape.draw(canvas);
        return mDotMarkerBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGameController.stopLoop();
    }
}

package ca.sfu.pacmacro;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ca.sfu.pacmacro.API.PacMacroClient;
import ca.sfu.pacmacro.Controller.CharacterDisplayCriteria;
import ca.sfu.pacmacro.Controller.CharacterManager;
import ca.sfu.pacmacro.Controller.GameController;
import ca.sfu.pacmacro.Controller.InitializeCircleCallback;
import ca.sfu.pacmacro.Controller.InitializeMarkerCallback;
import ca.sfu.pacmacro.Controller.PelletManager;

public class SpectatorActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PacMacroClient mApiClient;
    private CharacterManager mCharacterManager;
    private CharacterDisplayCriteria mDisplayCriteria;
    private PelletManager mPelletManager;
    private GameController mGameController;

    private int PERMISSION_RESPONSE_CODE = 0;
    private String TAG = "SpectatorActivity";

    private ImageView mTeamIcon;
    private TextView mTeamScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        int team = getIntent().getExtras().getInt(CharacterDisplayCriteria.EXTRA_KEY);
        mDisplayCriteria = new CharacterDisplayCriteria(team);

        mApiClient = new PacMacroClient();
        mGameController = new GameController();

        mTeamIcon = (ImageView) findViewById(R.id.scoreIcon);
        mTeamScore= (TextView) findViewById(R.id.scoreNum);

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

        centerMap();

        InitializeMarkerCallback characterMarkerCallback = new InitializeMarkerCallback() {
            @Override
            public Marker initializeMarker(LatLng latLng, String name, int drawableResourceId) {
                Bitmap mDotMarkerBitmap = getBitmapFromDrawable(drawableResourceId);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(name)
                        .icon(BitmapDescriptorFactory.fromBitmap(mDotMarkerBitmap));
                return mMap.addMarker(markerOptions);
            }
        };
        mCharacterManager = new CharacterManager(mApiClient, characterMarkerCallback, mGameController, mDisplayCriteria);

        InitializeCircleCallback pelletCircleCallback = new InitializeCircleCallback() {
            @Override
            public Circle initializeCircle(LatLng latLng, boolean isPowerPill) {
                int radius = 6;
                if (isPowerPill) {
                    radius = 18;
                }

                CircleOptions circleOptions = new CircleOptions()
                        .center(latLng)
                        .radius(radius)
                        .fillColor(Color.WHITE)
                        .strokeColor(Color.BLACK)
                        .strokeWidth(5.0f);
                return mMap.addCircle(circleOptions);
            }
        };
        mPelletManager = new PelletManager(mApiClient, pelletCircleCallback, mGameController);

        mGameController.startLoop();
    }

    private void centerMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49.283625, -123.116455), 15f));
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

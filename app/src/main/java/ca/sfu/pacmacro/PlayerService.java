package ca.sfu.pacmacro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import ca.sfu.pacmacro.API.PacMacroClient;
import ca.sfu.pacmacro.Model.Character;

public class PlayerService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public final static int NOTIFICATION_ID = 1;
    private static final long LOCATION_UPDATE_INTERVAL = 2000L;

    private Character.CharacterType mSelectedCharacterType;
    private PacMacroClient mApiClient = new PacMacroClient();
    private static final String TAG = "PLAYER_SERVICE";
    private NotificationManager mNotificationManager;
    private LocationListener mLocationListener;
    private PendingIntent mPendingIntent;
    private GoogleApiClient mGoogleApiClient;


    public PlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocationListener = createLocationListener();

//        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        registerLocationUpdateCallback();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = createNotification();

        startForeground(NOTIFICATION_ID, notification);
        Toast.makeText(PlayerService.this, "Service started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int returnValue = super.onStartCommand(intent, flags, startId);
        mSelectedCharacterType = (Character.CharacterType) intent.getExtras().get("Character");
        return returnValue;
    }

    @Override
    public void onDestroy() {
        try {
            Toast.makeText(PlayerService.this, "On Destroy called!!!!", Toast.LENGTH_SHORT).show();
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
            mPendingIntent.cancel();
            mNotificationManager.cancel(NOTIFICATION_ID);
            mGoogleApiClient.disconnect();
        } catch (SecurityException ignored) {

        }
    }

    private Notification createNotification() {
        Intent intent = new Intent(this, PlayerActivity.class);
        mPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.pacman)
                .setContentTitle(getString(R.string.player_service_notification_title))
                .setContentText(getString(R.string.player_service_notification_text))
                .setContentIntent(mPendingIntent)
                .setPriority(Notification.PRIORITY_MIN);

        return mBuilder.build();
    }

    private void registerLocationUpdateCallback() {
        try {
            LocationRequest locationRequest = LocationRequest.create().setInterval(LOCATION_UPDATE_INTERVAL).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,locationRequest ,mLocationListener);
        }
        catch (SecurityException ignored) {

        }
    }

    private void updateLocation(Location location) {
        mApiClient.setCharacterLocation(mSelectedCharacterType, location.getLatitude(), location.getLongitude());
        Toast.makeText(PlayerService.this, "Location Updated", Toast.LENGTH_SHORT).show();
        Log.v(TAG, "Location updated to" + location.toString());
    }

    private LocationListener createLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocation(location);
            }
        };
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        registerLocationUpdateCallback();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

package ca.sfu.pacmacro;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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
    private static final long LOCATION_UPDATE_INTERVAL = 5000L;

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
        registerListeners();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.getExtras() != null) {
            int returnValue = super.onStartCommand(intent, flags, startId);
            mSelectedCharacterType = (Character.CharacterType) intent.getExtras().get("Character");
            return returnValue;
        }
        return -1;
    }

    private void registerListeners() {
        mLocationListener = createLocationListener();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = createNotification();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(NOTIFICATION_ID, notification);

        Toast.makeText(PlayerService.this, "Service started", Toast.LENGTH_SHORT).show();
    }

    private void unregisterListeners() {
        try {
            if(mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
                mPendingIntent.cancel();

                mNotificationManager.cancel(NOTIFICATION_ID);
                mGoogleApiClient.disconnect();
                mLocationListener = null;
            }

        } catch (SecurityException ignored) {

        }
    }

    @TargetApi(26)
    private void startMyOwnForeground(){
        Intent intent = new Intent(this, PlayerActivity.class);
        mPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        String NOTIFICATION_CHANNEL_ID = "ca.sfu.pacmacro";
        String channelName = "PacMacro";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.pacman)
                .setContentTitle(getString(R.string.player_service_notification_title))
                .setContentText(getString(R.string.player_service_notification_text))
                .setContentIntent(mPendingIntent)
                .setPriority(Notification.PRIORITY_MIN)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        unregisterListeners();
        Log.i(TAG, "onDestroy()");
        super.onDestroy();
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
                Log.i(TAG, "onLocationChanged location: " + location.toString());
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

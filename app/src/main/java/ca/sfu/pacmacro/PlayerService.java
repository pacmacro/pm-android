package ca.sfu.pacmacro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import ca.sfu.pacmacro.API.PacMacroClient;

public class PlayerService extends Service {
    public final static int NOTIFICATION_ID = 1;

    private PacMacroClient mApiClient = new PacMacroClient();
    private static final String TAG = "PLAYER_SERVICE";
    NotificationManager mNotificationManager;
    LocationManager mLocationManager;
    private LocationListener mLocationListener;


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

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        registerLocationUpdateCallback();

        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = createNotification();

        startForeground(NOTIFICATION_ID, notification);
        Toast.makeText(PlayerService.this, "Service started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        try {
            Toast.makeText(PlayerService.this, "On Destroy called!!!!", Toast.LENGTH_SHORT).show();
            mLocationManager.removeUpdates(mLocationListener);
            mNotificationManager.cancel(NOTIFICATION_ID);
        } catch (SecurityException ignored) {

        }
    }

    private Notification createNotification() {
        NotificationCompat.BigTextStyle notificationStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle(getString(R.string.player_service_notification_title))
                .bigText(getString(R.string.player_service_notification_text));

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                .setContentTitle(getString(R.string.player_service_notification_title))
                .setContentText(getString(R.string.player_service_notification_text))
//                .setContentIntent(pi)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setStyle(notificationStyle);

        return mBuilder.build();
    }

    private void registerLocationUpdateCallback() {
        Log.d(TAG, "started registerLocationUpdateCallback");

        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 1.0f, mLocationListener);
        }
        catch (SecurityException ignored) {

        }
    }

    private void updateLocation(Location location) {
        //pacMacroClient.doSomething()
        Toast.makeText(PlayerService.this, "Location Updated", Toast.LENGTH_SHORT).show();
    }

    private LocationListener createLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }
}

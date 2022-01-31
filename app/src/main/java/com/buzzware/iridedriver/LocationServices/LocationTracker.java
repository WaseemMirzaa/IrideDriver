package com.buzzware.iridedriver.LocationServices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.StartUp;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LocationTracker extends Service {

    public final String TAG = "LocationTracker";

    public static final String ACTION_START_FOREGROUND_SERVICE =
            "ACTION_START_FOREGROUND_SERVICE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE =
            "ACTION_STOP_FOREGROUND_SERVICE";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS =  3 * 1000; // Every 5 Seconds

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    public static final String CHANNEL_ID = "my_channel_id";
    public static final CharSequence CHANNEL_NAME = "MY Channel";
    private LocationRequest mLocationRequest;
    public static Boolean serviceStarted = false;


    /**
     * The current location.
     */
    private Location mLocation;
    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    public static Location startingLatLng = null;
    public static Location lastKnownLocation = null;

    public LocationTracker() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "LocationTracker service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: ");

        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_START_FOREGROUND_SERVICE:

                        startingLatLng = null;

                        serviceStarted = true;

                        setLocationUpdateStarted();

                        startForegroundService();

                        break;
                    case ACTION_STOP_FOREGROUND_SERVICE:

                        serviceStarted = false;

                        setLocationUpdateStopped();

                        stopForegroundService();

                        break;
                }
            }
        }
        return START_STICKY;
    }

    private void setLocationUpdateStarted() {
//        DatabaseReference databaseReferenceFriendRequestsUser= FirebaseDatabase.getInstance().getReference().child(Constant.constant.getUserTable()).child(UserSessions.GetUserSession().getFirebaseUserID(getApplicationContext()))
//                .child("isWalking");
//        databaseReferenceFriendRequestsUser.setValue(true);
    }

    private void setLocationUpdateStopped() {
//        DatabaseReference databaseReferenceFriendRequestsUser= FirebaseDatabase.getInstance().getReference().child(Constant.constant.getUserTable()).child(UserSessions.GetUserSession().getFirebaseUserID(getApplicationContext()))
//                .child("isWalking");
//        databaseReferenceFriendRequestsUser.setValue(false);
    }

    private void startForegroundService() {

        Log.i(TAG, "Start foreground service");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Log.d(TAG, "onLocationResult: ");

                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();

        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
        getLastLocation();

        // Create Notification channel
        createNotificationChannel();

        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        Intent showTaskIntent = new Intent(getApplicationContext(), StartUp.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Location Service Started")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setOnlyAlertOnce(true)
                .setWhen(System.currentTimeMillis());
        //.setContentIntent(contentIntent);
        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }
        startForeground(1, builder.build());

        ServiceUtils.setRequestingLocationUpdates(getApplicationContext(), true);
    }

    private void stopForegroundService() {
        Log.i(TAG, "Stop Foreground Service");
        Log.d(TAG, "stopForegroundService: ");
        ServiceUtils.setRequestingLocationUpdates(getApplicationContext(), false);

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        // Stop Foreground Service and Remove Notification
        stopForeground(true);
        // Stop the service
        stopSelf();
    }

    private void createNotificationChannel() {

        Log.d(TAG, "createNotificationChannel: ");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                int importance = NotificationManager.IMPORTANCE_DEFAULT;

                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.setSound(null, null);
                notificationChannel.setShowBadge(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {

        Log.d(TAG, "createLocationRequest: ");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getLastLocation() {

        Log.d(TAG, "getLastLocation: ");

        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLocation = task.getResult();

                            onNewLocation(mLocation);

                        } else {
                            Log.w(TAG, "Failed to get location.");
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    String getUserId() {

        return FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    private void onNewLocation(Location location) {

        mLocation = location;

        Map<String, Object> map = new HashMap<>();

        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));

        map.put("lat", location.getLatitude());
        map.put("lng", location.getLongitude());
        map.put("hash", hash);

        FirebaseFirestore.getInstance().collection("Users").document(getUserId())
                .update(map);

//        FirebaseRequests.GetFirebaseRequests(getApplicationContext()).UpdateLocation(location.getLatitude()+"", location.getLongitude()+"");
//
//        if (startingLatLng == null)
//        {
//            startingLatLng = location;
//        }
//
//        if (lastKnownLocation == null)
//        {
//            lastKnownLocation = location;
//        }
//
//        if (lastKnownLocation != location)
//        {
//            setDistanceCovered(location);
//        }
//
//        lastKnownLocation = location;
//        // Notify anyone listening for broadcasts about the new location.
//        Intent intent = new Intent(ACTION_BROADCAST);
//        intent.putExtra(EXTRA_LOCATION, location);
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        // You can send Location to Firebase Notification for Tracking Purposes

    }

//    void setDistanceCovered(Location location) {
//        float distanceCovered = distance(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), location.getLatitude(), location.getLongitude());
//
//        UserSessions.GetUserSession().setTotalWalkDistance(distanceCovered, getApplicationContext());
//
//        FirebaseRequests.GetFirebaseRequests(getApplicationContext()).getUser((snapshot, error) -> {
//
//            if (snapshot.getValue() != null)
//            {
//                User user = snapshot.getValue(User.class);
//
//                if (user != null && user.distanceCovered != null)
//                {
//                    float distance = distanceCovered + user.distanceCovered;
//
//                    DatabaseReference databaseReferenceFriendRequestsUser= FirebaseDatabase.getInstance().getReference().child(Constant.constant.getUserTable()).child(UserSessions.GetUserSession().getFirebaseUserID(getApplicationContext()))
//                            .child("distanceCovered");
//
//                    if (distance > 0)
//                    {
//                        databaseReferenceFriendRequestsUser.setValue(distance);
//                    }
//                }
//                else
//                {
//                    DatabaseReference databaseReferenceFriendRequestsUser= FirebaseDatabase.getInstance().getReference().child(Constant.constant.getUserTable()).child(UserSessions.GetUserSession().getFirebaseUserID(getApplicationContext()))
//                            .child("distanceCovered");
//                    databaseReferenceFriendRequestsUser.setValue(distanceCovered);
//                }
//            }
//
//        });

//    }

//    private Float distance(double lat1, double lon1, double lat2, double lon2) { ///distance in miles
//
//        Log.d(TAG, "distance: ");
//
//        double theta = lon1 - lon2;
//        double dist = Math.sin(deg2rad(lat1))
//                * Math.sin(deg2rad(lat2))
//                + Math.cos(deg2rad(lat1))
//                * Math.cos(deg2rad(lat2))
//                * Math.cos(deg2rad(theta));
//        dist = Math.acos(dist);
//        dist = rad2deg(dist);
//        dist = dist * 60 * 1.1515;
//        return  Double.valueOf((dist)).floatValue();
//    }
//
//    private double deg2rad(double deg) {
//        return (deg * Math.PI / 180.0);
//    }
//
//    private double rad2deg(double rad) {
//        return (rad * 180.0 / Math.PI);
//    }

}
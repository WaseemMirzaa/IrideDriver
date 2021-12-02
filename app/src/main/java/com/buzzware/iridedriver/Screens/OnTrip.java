package com.buzzware.iridedriver.Screens;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.buzzware.iridedriver.LocationServices.LocationTracker;
import com.buzzware.iridedriver.Models.RideModel;
import com.buzzware.iridedriver.Models.response.directions.DirectionsApiResponse;
import com.buzzware.iridedriver.Models.response.directions.Leg;
import com.buzzware.iridedriver.Models.response.directions.Route;
import com.buzzware.iridedriver.Models.response.directions.Step;
import com.buzzware.iridedriver.Models.response.distanceMatrix.DistanceMatrixResponse;
import com.buzzware.iridedriver.Models.response.distanceMatrix.Element;
import com.buzzware.iridedriver.Models.response.distanceMatrix.Row;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.ActivityOnTripBinding;
import com.buzzware.iridedriver.retrofit.Controller;
import com.buzzware.iridedriver.utils.AppConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnTrip extends BaseActivity implements OnMapReadyCallback {

    RideModel rideModel;

    ActivityOnTripBinding mBinding;

    public GoogleMap mMap;

    public Marker marker;

    private SimpleLocation location;

    Boolean hasLocationPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_on_trip);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_trip);

        getExtrasFromIntent();

        checkPermissionsAndInit();

        setListeners();
    }


    private void startLocationService() {

        Intent serviceIntent = new Intent(this, LocationTracker.class);

        serviceIntent.setAction(LocationTracker.ACTION_START_FOREGROUND_SERVICE);

        startService(serviceIntent);
    }

    private void stopLocationService() {

        Intent serviceIntent = new Intent(this, LocationTracker.class);

        serviceIntent.setAction(LocationTracker.ACTION_STOP_FOREGROUND_SERVICE);

        startService(serviceIntent);
    }

    private void setListeners() {

        mBinding.navigateCV.setOnClickListener(v -> performNavigation());

        mBinding.actionTV.setOnClickListener(v -> updateRideStatus());
    }

    private void updateRideStatus() {

        if (rideModel.status.equalsIgnoreCase("driverAccepted")) {

           rideModel.status = "driverReached";

        } else if (rideModel.status.equalsIgnoreCase("driverReached")) {

            rideModel.status = "rideStarted";

        } else if (rideModel.status.equalsIgnoreCase("rideStarted")) {

            rideModel.status = "rideCompleted";

            stopLocationService();

        }

        FirebaseFirestore.getInstance().collection("Bookings")
                .document(rideModel.id).
                set(rideModel);

        setRideButton();

    }

    private void showCurrentLocation() {

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0F));

    }

    private void performNavigation() {

        Uri gmmIntentUri = Uri.parse("google.navigation:q="+rideModel.destination.lat+","+rideModel.destination.lng);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        mapIntent.setPackage("com.google.android.apps.maps");

        startActivity(mapIntent);

    }

    private void getExtrasFromIntent() {

        rideModel = getIntent().getParcelableExtra("ride");

    }

    Call<String> reverseCall;

    void calculateDistance() {

        String url = "/maps/api/distancematrix/json?departure_time&origins=" + location.getLatitude() + "," + location.getLongitude() + "&destinations=" + rideModel.destination.lat + "," + rideModel.destination.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi().getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DistanceMatrixResponse resp = gson.fromJson(response.body(), DistanceMatrixResponse.class);

                    setDistance(resp);

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }

    private void setDistance(DistanceMatrixResponse resp) {

        String distance = "";
        String time = "";
        String currentAddress = "";

        if (resp.origin_addresses != null && resp.origin_addresses.size() > 0) {

            currentAddress = resp.origin_addresses.get(0);

        }

        if (resp.rows != null && resp.rows.size() > 0) {

            Row row = resp.rows.get(0);

            if (row.elements != null && row.elements.size() > 0) {

                Element element = row.elements.get(0);

                if (element.distance != null)

                    distance = element.distance.text;

                if (element.duration != null)

                    time = element.duration.text;

            }

        }

        mBinding.timeTV.setText(time);
        mBinding.kmTV.setText(distance);
        mBinding.currentLocationTV.setText(currentAddress);
    }

    void getDirections() {

        String url = "/maps/api/directions/json?origin=" + location.getLatitude() + "," + location.getLongitude() + "&destination=" + rideModel.destination.lat + "," + rideModel.destination.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi().getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DirectionsApiResponse resp = gson.fromJson(response.body(), DirectionsApiResponse.class);

                    drawPaths(resp);

                }

                calculateDistance();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }

    private void drawPaths(DirectionsApiResponse res) {

        ArrayList<LatLng> path = new ArrayList<>();

        try {

            if (res.routes != null && res.routes.size() > 0) {

                Route route = res.routes.get(0);

                if (route.legs != null) {

                    for (int i = 0; i < route.legs.size(); i++) {

                        Leg leg = route.legs.get(i);

                        if (leg.steps != null) {

                            for (int j = 0; j < leg.steps.size(); j++) {

                                Step step1 = leg.steps.get(j);

                                if (step1.polyline != null) {

                                    List<LatLng> decoded = PolyUtil.decode(step1.polyline.points);

                                    path.addAll(decoded);

                                }

                            }

                        }

                    }

                }

            }

        } catch (Exception ex) {

        }

        //Draw the polyline
        if (path.size() > 0) {

            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(8);

            mMap.addPolyline(opts);

        }

    }

    void setRideButton() {

        if (rideModel.status.equalsIgnoreCase("driverAccepted")) {

            mBinding.actionTV.setText("Arrived");

        } else if (rideModel.status.equalsIgnoreCase("driverReached")) {

            mBinding.actionTV.setText("Start Ride");

        } else if (rideModel.status.equalsIgnoreCase("rideStarted")) {
            
            mBinding.actionTV.setText("Complete");

        } else {

            mBinding.rideBt.setVisibility(View.GONE);

        }
    }

    private void checkPermissionsAndInit() {

        setRideButton();

        String[] permissions = {Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        Permissions.check(OnTrip.this/*context*/, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                hasLocationPermissions = true;

                location = new SimpleLocation(OnTrip.this);

                if (!location.hasLocationEnabled()) {
                    // ask the user to enable location access
                    showEnableLocationDialog("Please enable location from setting in order to proceed to the app");

                    return;
                }

                location.beginUpdates();

                startLocationService();

                init();

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

                hasLocationPermissions = false;

                showPermissionsDeniedError("Please enable location permissions from setting in order to proceed to the app.");

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        checkIfPermissionsGranted();

    }

    private void checkIfPermissionsGranted() {

        if (ContextCompat.checkSelfPermission(OnTrip.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(OnTrip.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(OnTrip.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            stopLocationService();

            return;
        }

        hasLocationPermissions = true;

        location = new SimpleLocation(OnTrip.this);

        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            showEnableLocationDialog("Please enable location from setting in order to proceed to the app");

            return;
        }

        location.beginUpdates();

        init();

    }

    private void init() {

        mBinding.homeMapView.onCreate(null);

        mBinding.homeMapView.onResume();

        mBinding.homeMapView.getMapAsync(this);

        location.setListener(() -> {

            if (marker != null && location != null) {

                marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));

            }

        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0F));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(rideModel.destination.lat, rideModel.destination.lng))
                .title("Destination")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        getDirections();

        mBinding.currentLocationIV.setOnClickListener(v -> showCurrentLocation());

    }
}
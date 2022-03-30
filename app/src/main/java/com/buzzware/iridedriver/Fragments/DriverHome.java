package com.buzzware.iridedriver.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iridedriver.Adapters.UpcomingRidesAdapter;
import com.buzzware.iridedriver.Firebase.FirebaseInstances;
import com.buzzware.iridedriver.Models.RideModel;
import com.buzzware.iridedriver.Models.SearchedPlaceModel;
import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.Models.VehicleModel;
import com.buzzware.iridedriver.Models.response.geoCode.ReverseGeoCodeResponse;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.Home;
import com.buzzware.iridedriver.Screens.OnTrip;
import com.buzzware.iridedriver.databinding.FragmentDriverHomeBinding;
import com.buzzware.iridedriver.databinding.FragmentHomeBinding;
import com.buzzware.iridedriver.events.NewRideEvent;
import com.buzzware.iridedriver.retrofit.Controller;
import com.buzzware.iridedriver.utils.AppConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHome extends BaseFragment {

    private SimpleLocation location;

    Boolean hasLocationPermissions;

    public RideModel rideModel;
    public String selectedId;

    FragmentDriverHomeBinding mBinding;
    GoogleMap mMap;

    public DriverHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_driver_home, container, false);

        mBinding.backIV.setOnClickListener(v -> Home.OpenCloseDrawer());

        mBinding.homeMapView.onCreate(savedInstanceState);

        mBinding.homeMapView.onResume();

        return mBinding.getRoot();
    }


    private void checkIfPermissionsGranted() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        hasLocationPermissions = true;

        location = new SimpleLocation(getActivity());

        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            showEnableLocationDialog("Please enable location from setting in order to proceed to the app");

            return;
        }

        try {

            location.endUpdates();

        } catch (Exception e) {


        }

        location.beginUpdates();

        location.setListener(() -> reverseGeoCode(location.getLatitude(), location.getLongitude()));

        mBinding.homeMapView.getMapAsync(this::onMapReady);
    }

    void checkPermissionsAndGetRide() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            showErrorAlert("Please enable location permissions first.");
            return;
        }

        mBinding.homeMapView.getMapAsync(this::onMapReady);
    }

    private void checkPermissionsAndInit() {

        String[] permissions = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        Permissions.check(getActivity()/*context*/, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                hasLocationPermissions = true;

                location = new SimpleLocation(getActivity());

                if (!location.hasLocationEnabled()) {
                    // ask the user to enable location access
                    showEnableLocationDialog("Please enable location from setting in order to proceed to the app");

                    return;
                }
                try {

                    location.endUpdates();

                } catch (Exception e) {


                }

                location.beginUpdates();

                location.setListener(() -> reverseGeoCode(location.getLatitude(), location.getLongitude()));

                mBinding.homeMapView.getMapAsync(DriverHome.this::onMapReady);

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

                hasLocationPermissions = false;

                showPermissionsDeniedError("Please enable location permissions from setting in order to proceed to the app.");

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);

        mBinding.homeMapView.onResume();
        checkPermissionsAndInit();

    }

    public static void OpenCloseDrawer() {

        if (Home.mBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) {

            Home.mBinding.drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            Home.mBinding.drawerLayout.openDrawer(GravityCompat.START);

        }

    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);

        mBinding.homeMapView.onPause();

        super.onPause();

    }

    @Override
    public void onDestroyView() {
        mBinding.homeMapView.onDestroy();
        super.onDestroyView();


    }

    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0F));

        mBinding.currentLocationIV.setOnClickListener(v -> showCurrentLocation());


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showRideAlert(NewRideEvent event) {

        if(event.id != null) {

            getRide(event.id);

        }

    }

    AlertDialog newRideDialog;

    private void getRide(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("New Ride")
                .setMessage("You have a new Ride")
                .setCancelable(false)
                .setPositiveButton("Accept", (dialog, which) -> {
                    newRideDialog.dismiss();
                    newRideDialog = null;
                    accept(id);
                })
                .setNegativeButton("Decline", (dialog, which) -> {

                    newRideDialog.dismiss();
                    newRideDialog = null;
                });

        if(newRideDialog == null) {
            newRideDialog = builder.create();
            builder.show();
        }


    }

    Call<String> reverseCall;

    void reverseGeoCode(double lat, double lng) {

        Map<String, Object> map = new HashMap<>();

        map.put("lat", location.getLatitude());
        map.put("lng", location.getLongitude());

        FirebaseFirestore.getInstance().collection("Users").document(getUserId())
                .update(map);

        String url = "/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi().getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    ReverseGeoCodeResponse reverseGeoCodeResponse = gson.fromJson(response.body(), ReverseGeoCodeResponse.class);

                    setAddress(reverseGeoCodeResponse);

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                reverseCall = null;
            }
        });
    }

    private void setAddress(ReverseGeoCodeResponse reverseGeoCodeResponse) {

        if (reverseGeoCodeResponse.results != null && reverseGeoCodeResponse.results.size() > 0) {

            String address = reverseGeoCodeResponse.results.get(0).formatted_address;

            if (address != null)

                mBinding.currentLocationTV.setText(address);


        }

    }


    private void showCurrentLocation() {

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0F));

    }

    public void accept(String id) {

        selectedId = id;

        FirebaseInstances.usersCollection
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {

                    hideLoader();

                    if (task.isSuccessful()) {

                        User user = task.getResult().toObject(User.class);

                        if (user != null) {

                            if (user.isVerified != null && user.isVerified.equalsIgnoreCase("approved")) {

                                checkVehicleDetails();

                            } else {

                                showErrorAlert("Please Add Vehicle Data First");

                            }


                        }

                    }

                });

    }


    private void checkVehicleDetails() {

        FirebaseFirestore.getInstance().collection("Vehicle")
                .get()
                .addOnCompleteListener(
                        this::parseVehicleSnapshot
                );

    }

    void parseVehicleSnapshot(Task<QuerySnapshot> task) {

        VehicleModel selectedVehicle = null;

        if (task.getResult() != null) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                VehicleModel vehicle = document.toObject(VehicleModel.class);

                vehicle.id = document.getId();

                if (vehicle.userId.equalsIgnoreCase(getUserId())) {

                    selectedVehicle = vehicle;

                    break;
                }

            }

        }

        if (selectedVehicle != null)

            checkStripeStatus(selectedVehicle);

        else

            hideLoader();
    }

    void checkStripeStatus(VehicleModel vehicleModel) {

        FirebaseInstances.usersCollection
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {


                    if (task.isSuccessful()) {

                        User user = task.getResult().toObject(User.class);

                        if (user != null) {

                            if (user.stripeStatus != null && user.stripeStatus.equalsIgnoreCase("approved"))

                                acceptRide(vehicleModel);

                            else {

                                showErrorAlert("Please wait for admin approval.");
                            }
                        }
                    }
                });

    }

    private void acceptRide(VehicleModel vehicle) {

        if (selectedId == null) {

            hideLoader();

            return;
        }


        FirebaseInstances.bookingsCollection.document(selectedId)
                .get()
                .addOnCompleteListener(task -> {

                    hideLoader();

                    if (task.isSuccessful()) {

                        RideModel rideModel = task.getResult().toObject(RideModel.class);

                        if (rideModel != null) {

                            rideModel.id = task.getResult().getId();

                            if(rideModel.vehicleId != null && !rideModel.vehicleId.isEmpty()) {

                                showErrorAlert("Sorry, This ride is already accepted by another driver.");

                                return;
                            }

                            rideModel.driverId = getUserId();
                            rideModel.vehicleId = vehicle.getId();
                            rideModel.status = "driverAccepted";

                            FirebaseInstances.usersCollection.document(getUserId())
                                    .update("isActive", false);

                            FirebaseFirestore.getInstance().collection("Bookings").document(rideModel.id)
                                    .set(rideModel);

                            Toast.makeText(getActivity(), "Successfully Accepted", Toast.LENGTH_SHORT).show();

                            moveToOnTrip(rideModel);
                        }


                    }

                });


    }

    private void moveToOnTrip(RideModel rideModel) {


        String[] permissions = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        Permissions.check(getActivity()/*context*/, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                startActivity(new Intent(getActivity(), OnTrip.class)
                        .putExtra("ride", rideModel));

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

                showPermissionsDeniedError("Please enable location permissions from setting in order to proceed to the app.");

            }
        });


    }


}
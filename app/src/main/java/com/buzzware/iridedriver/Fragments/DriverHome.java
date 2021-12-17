package com.buzzware.iridedriver.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.buzzware.iridedriver.retrofit.Controller;
import com.buzzware.iridedriver.utils.AppConstants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

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

    FragmentDriverHomeBinding mBinding;
    GoogleMap mMap;

    public DriverHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_driver_home, container, false);

        checkPermissionsAndInit();

        mBinding.backIV.setOnClickListener(v -> Home.OpenCloseDrawer());

        mBinding.homeMapView.onCreate(savedInstanceState);

        mBinding.homeMapView.onResume();

        return mBinding.getRoot();
    }



    private void checkIfPermissionsGranted() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            showErrorAlert("Please enable location permissions first.");
            return;
        }

        mBinding.homeMapView.getMapAsync(this::onMapReady);
    }

    private void checkPermissionsAndInit() {

//        setRideButton();

        String[] permissions = {Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

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

        mBinding.homeMapView.onResume();
        checkIfPermissionsGranted();

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

        if(reverseGeoCodeResponse.results != null && reverseGeoCodeResponse.results.size() > 0) {

            String address = reverseGeoCodeResponse.results.get(0).formatted_address;

            if(address != null)

                mBinding.currentLocationTV.setText(address);


        }

    }


    private void showCurrentLocation() {

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0F));

    }
}
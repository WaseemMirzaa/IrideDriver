package com.buzzware.iridedriver.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iridedriver.Adapters.UpcomingRidesAdapter;
import com.buzzware.iridedriver.Models.RideModel;
import com.buzzware.iridedriver.Models.SearchedPlaceModel;
import com.buzzware.iridedriver.Models.VehicleModel;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.Home;
import com.buzzware.iridedriver.Screens.OnTrip;
import com.buzzware.iridedriver.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import im.delight.android.location.SimpleLocation;

public class HomeFragment extends BaseFragment implements View.OnClickListener {

    RideType rideType;

    ArrayList<RideModel> rides;

    RideModel selectedRide;

    FragmentHomeBinding mBinding;

    private SimpleLocation location;

    Boolean hasLocationPermissions;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        rideType = RideType.completed;

        setListeners();

        checkPermissionsAndInit();

        return mBinding.getRoot();
    }

    private void checkIfPermissionsGranted() {

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
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

        getRides();
    }

    void checkPermissionsAndGetRide() {

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            showErrorAlert("Please enable location permissions first.");
            return;
        }

        getRides();
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

                getRides();

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

        checkIfPermissionsGranted();

    }

    private void setListeners() {

        ////init click
        mBinding.firstTabLay.setOnClickListener(this);

        mBinding.secondTabLay.setOnClickListener(this);

        mBinding.runningTabLay.setOnClickListener(this);

        mBinding.drawerIcon.setOnClickListener(this);
    }


    private void getRides() {

        Query query;

        if (rideType == RideType.upcoming) {

            query = FirebaseFirestore.getInstance().collection("Bookings")
                    .whereEqualTo(
                            "status",
                            "booked"
                    );

        } else if (rideType == RideType.running) {

            query = FirebaseFirestore.getInstance().collection("Bookings")
                    .whereEqualTo("driverId", getUserId())
                    .whereIn("status", Arrays.asList("driverAccepted", "driverReached", "rideStarted"));

        } else {

            query = FirebaseFirestore.getInstance().collection("Bookings")
                    .whereEqualTo("driverId", getUserId())
                    .whereIn("status", Arrays.asList("rated", "rideCompleted"));

        }

        showLoader();
        query.get()
                .addOnCompleteListener(
                        this::parseSnapshot
                );

    }

    void parseSnapshot(Task<QuerySnapshot> task) {

        hideLoader();

        rides = new ArrayList<>();

        if (task.getResult() != null) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                RideModel rideModel = document.toObject(RideModel.class);

                rideModel.id = document.getId();

                rides.add(rideModel);

            }

        }

        for (RideModel rideModel : rides) {

            if (rideModel.driverId == null) {

                SearchedPlaceModel pickUp = rideModel.tripDetail.pickUp;

                double distanceInMiles = distance(pickUp.lat, pickUp.lng, location.getLatitude(), location.getLongitude());

                if (distanceInMiles > 10) {

                    rides.remove(rideModel);

                }

            }

        }

        setAdapter(rides);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void setAdapter(ArrayList<RideModel> rides) {

        mBinding.ridesRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBinding.ridesRV.setAdapter(new UpcomingRidesAdapter(getActivity(),
                rides,
                new UpcomingRidesAdapter.UpcomingRideActionListener() {
                    @Override
                    public void acceptRide(RideModel rideModel) {

                        accept(rideModel);

                    }

                    @Override
                    public void moveToCompleteScreen(RideModel rideModel) {

                        moveToOnTrip(rideModel);

                    }
                },
                rideType));

    }

    private void moveToOnTrip(RideModel rideModel) {


        String[] permissions = {Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

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

    public void accept(RideModel rideModel) {

        this.selectedRide = rideModel;

        showLoader();

        checkVehicleDetails();
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

            acceptRide(selectedVehicle);

        else

            hideLoader();
    }

    private void acceptRide(VehicleModel vehicle) {

        if (selectedRide == null) {

            hideLoader();

            return;
        }

        selectedRide.driverId = getUserId();
        selectedRide.vehicleId = vehicle.getId();
        selectedRide.status = "driverAccepted";

        FirebaseFirestore.getInstance().collection("Bookings").document(selectedRide.id)
                .set(selectedRide);

        getRides();
    }


    @Override
    public void onClick(View v) {

        if (v == mBinding.firstTabLay) {

            SetupTabView(0);

        } else if (v == mBinding.secondTabLay) {

            SetupTabView(2);

        } else if (v == mBinding.runningTabLay) {

            SetupTabView(1);

        } else if (v == mBinding.drawerIcon) {

            SetStatusBarColor();

            OpenCloseDrawer();

        }
    }

    public void SetStatusBarColor() {


    }

    public static void OpenCloseDrawer() {

        if (Home.mBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) {

            Home.mBinding.drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            Home.mBinding.drawerLayout.openDrawer(GravityCompat.START);

        }

    }

    public void SetupTabView(int position) {

        if (position == 0) {

            mBinding.seconfTabTv.setTextColor(getResources().getColor(R.color.gray_light));

            mBinding.runningTV.setTextColor(getResources().getColor(R.color.gray_light));

            mBinding.seccondTabLine.setBackgroundColor(getResources().getColor(R.color.white));

            mBinding.runningTV.setTextColor(getResources().getColor(R.color.gray_light));

            mBinding.runningTabLine.setBackgroundColor(getResources().getColor(R.color.white));

            mBinding.firstTabTv.setTextColor(getResources().getColor(R.color.black));

            mBinding.firstTabLine.setBackgroundColor(getResources().getColor(R.color.purple_200));

            rideType = RideType.completed;

        } else if (position == 1) {

            mBinding.firstTabTv.setTextColor(getResources().getColor(R.color.gray_light));

            mBinding.firstTabLine.setBackgroundColor(getResources().getColor(R.color.white));

            mBinding.seconfTabTv.setTextColor(getResources().getColor(R.color.gray_light));

            mBinding.seccondTabLine.setBackgroundColor(getResources().getColor(R.color.white));

            mBinding.runningTV.setTextColor(getResources().getColor(R.color.black));

            mBinding.runningTabLine.setBackgroundColor(getResources().getColor(R.color.purple_200));

            rideType = RideType.running;

        } else {

            mBinding.firstTabTv.setTextColor(getResources().getColor(R.color.gray_light));

            mBinding.firstTabLine.setBackgroundColor(getResources().getColor(R.color.white));

            mBinding.runningTV.setTextColor(getResources().getColor(R.color.gray_light));

            mBinding.runningTabLine.setBackgroundColor(getResources().getColor(R.color.white));

            mBinding.seconfTabTv.setTextColor(getResources().getColor(R.color.black));

            mBinding.seccondTabLine.setBackgroundColor(getResources().getColor(R.color.purple_200));

            rideType = RideType.upcoming;

        }

        checkPermissionsAndGetRide();
    }
}
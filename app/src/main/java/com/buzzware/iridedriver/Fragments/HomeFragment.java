package com.buzzware.iridedriver.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.buzzware.iridedriver.Adapters.PaymentsAdapter;
import com.buzzware.iridedriver.Adapters.ScheduledRidesAdapter;
import com.buzzware.iridedriver.Adapters.UpcomingRidesAdapter;
import com.buzzware.iridedriver.Firebase.FirebaseInstances;
import com.buzzware.iridedriver.Models.Payouts.PayoutObj;
import com.buzzware.iridedriver.Models.Payouts.RideWithPayoutModel;
import com.buzzware.iridedriver.Models.RideModel;
import com.buzzware.iridedriver.Models.ScheduleModel;
import com.buzzware.iridedriver.Models.SearchedPlaceModel;
import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.Models.VehicleModel;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.DriverHome;
import com.buzzware.iridedriver.Screens.Home;
import com.buzzware.iridedriver.Screens.OnTrip;
import com.buzzware.iridedriver.databinding.FragmentHomeBinding;
import com.buzzware.iridedriver.events.OnlineStatusChanged;
import com.buzzware.iridedriver.utils.AppConstants;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
//import com.savvi.rangedatepicker.CalendarPickerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import im.delight.android.location.SimpleLocation;

public class HomeFragment extends BaseFragment implements View.OnClickListener {

    RideType rideType;

    ArrayList<RideModel> rides;

    RideModel selectedRide;

    FragmentHomeBinding mBinding;

    private SimpleLocation location;

    Boolean hasLocationPermissions;

    Boolean defaultScheduleSelected;

    int position = 0;

    public HomeFragment() {
        position = 0;
        // Required empty public constructor
    }

    public HomeFragment(int position) {
        this.position = position;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        rideType = RideType.upcoming;

        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText("ON-DEMAND"));
        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText("CURRENT"));
//        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText("SCHEDULED"));
        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText("COMPLETED"));

        setListeners();

//        checkPermissionsAndInit();

        defaultScheduleSelected = true;

        return mBinding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);

    }


    private void checkIfPermissionsGranted() {

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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

        mBinding.tabLayout.getChildAt(position).setSelected(true);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onlineStatusChanged(OnlineStatusChanged statusChanged) {

        getRides();

    }

    void checkPermissionsAndGetRide() {

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            showErrorAlert("Please enable location permissions first.");
            return;
        }

        getRides();
    }

    private void checkPermissionsAndInit() {

//        setRideButton();

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

        EventBus.getDefault().register(this);
        checkPermissionsAndInit();

    }

    TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {

            SetupTabView(tab.getPosition());

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void setListeners() {

        mBinding.calendarCV.setOnDayClickListener(eventDay -> {

            mBinding.calendarCV.setSelectedDates(new ArrayList<>());

//            List<Calendar> list = new ArrayList<>();

//            list.add(eventDay.getCalendar());

            try {

                mBinding.calendarCV.setDate(eventDay.getCalendar().getTime());

            } catch (OutOfDateRangeException e) {

                e.printStackTrace();

            }

            mBinding.calendarCV.setSelected(true);

            selectedDate = new Date().getTime();

            getRides();
        });

        mBinding.tabLayout.addOnTabSelectedListener(onTabSelectedListener);

        mBinding.drawerIcon.setOnClickListener(this);
    }


    private void getRides() {

        Query query;

        if (rideType == RideType.upcoming) {

            query = FirebaseFirestore.getInstance().collection("Bookings")
                    .whereIn("status", Arrays.asList("booked", "reBooked"));


        } else if (rideType == RideType.running) {

            query = FirebaseFirestore.getInstance().collection("Bookings")
                    .whereEqualTo("driverId", getUserId())
                    .whereIn("status", Arrays.asList("driverAccepted", "driverReached", "rideStarted"));

        } else {

//            query = FirebaseFirestore.getInstance().collection("Bookings")
//                    .whereEqualTo("driverId", getUserId())
//                    .whereIn("status", Arrays.asList("rated", "rideCompleted"));
            getCompletedRides();

            return;
        }

        showLoader();
        query.addSnapshotListener((value, error) -> {

            if (value != null) {

                if (rideType == RideType.scheduled) {

                    parseSchedules(value);

                } else {

                    parseSnapshot(value);

                }
            }
        });

    }

    private void getPayouts() {

        FirebaseInstances.payoutsCollection

                .whereEqualTo("driverId", getUserId())

                .get()

                .addOnCompleteListener(this::onPayoutsInfoReceived);

    }

    private void onPayoutsInfoReceived(Task<QuerySnapshot> task) {

        payouts = new ArrayList<>();

        if (task.isSuccessful()) {

            if (task.getResult() != null) {

                for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                    PayoutObj payoutObj = snapshot.toObject(PayoutObj.class);

                    if (payoutObj != null) {

                        payoutObj.id = snapshot.getId();

                        updateRides(payoutObj);

                        payouts.add(payoutObj);

                    }

                }

            }

        }

        List<RideWithPayoutModel> list = new ArrayList<>();

        for (RideWithPayoutModel ride : completedRides) {

            if (ride.payout != null) {
                list.add(ride);
            }
        }

        completedRides.clear();
        completedRides.addAll(list);
        hideLoader();

        setAdapter();

    }


    private void updateRides(PayoutObj payoutObj) {

        if (payoutObj.orderId == null)

            return;

        for (int i = 0; i < completedRides.size(); i++) {

            if (payoutObj.orderId.equalsIgnoreCase(completedRides.get(i).id)) {

                completedRides.get(i).payout = payoutObj;

                return;

            }

        }

    }

    private void setAdapter() {

        mBinding.ridesRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBinding.ridesRV.setAdapter(new PaymentsAdapter(getActivity(), completedRides));
    }

    private void getCompletedRides() {

        showLoader();

        FirebaseInstances.bookingsCollection
                .whereEqualTo("driverId", getUserId())
                .whereIn("status", Arrays.asList(AppConstants.RideStatus.RIDE_COMPLETED, AppConstants.RideStatus.RATED))
                .get()
                .addOnCompleteListener(this::onGetRidesTaskCompleted);

    }

    List<RideWithPayoutModel> completedRides = new ArrayList<>();

    List<PayoutObj> payouts = new ArrayList<>();

    private void onGetRidesTaskCompleted(Task<QuerySnapshot> task) {

        completedRides = new ArrayList<>();

        if (task.isSuccessful()) {

            if (task.getResult() != null) {

                for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                    RideWithPayoutModel rideModel = snapshot.toObject(RideWithPayoutModel.class);

                    if (rideModel != null) {

                        rideModel.id = snapshot.getId();

                        completedRides.add(rideModel);
                    }

                }
            }

        }

        getPayouts();

    }

    void parseSnapshot(QuerySnapshot task) {

        hideLoader();

        rides = new ArrayList<>();


        for (QueryDocumentSnapshot document : task) {

            RideModel rideModel = document.toObject(RideModel.class);

            rideModel.id = document.getId();

            rides.add(rideModel);

        }


        for (int i = 0; i < rides.size(); i++) {

            RideModel rideModel = rides.get(i);

            if (rideModel.driverId == null) {

                SearchedPlaceModel pickUp = rideModel.tripDetail.pickUp;

                double distanceInMiles = distance(pickUp.lat, pickUp.lng, location.getLatitude(), location.getLongitude());

                if (distanceInMiles > 10 && rideType == RideType.upcoming) {

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

        if (rideType == RideType.upcoming) {

            FirebaseInstances.usersCollection.document(getUserId())
                    .get()
                    .addOnCompleteListener(task -> {

                        hideLoader();

                        if (task.isSuccessful()) {

                            User user = task.getResult().toObject(User.class);

                            if (user != null) {

                                if ((user.isActive != null && !user.isActive) || (user.isOnline != null && !user.isOnline)) {

                                    rides.clear();

                                }
                                setRidesAdapterForUpcoming(rides);

                            }
                        }

                    });

            return;
        }

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


    Query query;

    ArrayList<ScheduleModel> scheduledRides;


    void parseSchedules(QuerySnapshot task) {

        query = null;

        hideLoader();

        ArrayList<Calendar> dateArrayList = new ArrayList<>();

        scheduledRides = new ArrayList<>();

        if (task.getDocuments() != null) {

            for (DocumentSnapshot document : task.getDocuments()) {

                ScheduleModel scheduleModel = document.toObject(ScheduleModel.class);

                scheduleModel.id = document.getId();

                double distanceInMiles = distance(scheduleModel.tripDetail.pickUp.lat, scheduleModel.tripDetail.pickUp.lng, location.getLatitude(), location.getLongitude());

                if (selectedDate < 0) {

                    Calendar calendar = Calendar.getInstance();

                    Date date = new Date();

                    date.setTime(scheduleModel.scheduleTimeStamp);

                    calendar.setTime(date);

                    dateArrayList.add(calendar);

                    scheduledRides.add(scheduleModel);

                } else {
//
                    for (Calendar c : mBinding.calendarCV.getSelectedDates()) {

                        Date bookingDate = new Date();

                        bookingDate.setTime(scheduleModel.bookingDate);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");

                        String date = simpleDateFormat.format(c.getTime());
                        String date1 = simpleDateFormat1.format(bookingDate);

                        if (date.equalsIgnoreCase(date1)) {

                            scheduledRides.add(scheduleModel);

                        }
                    }
                }

                if (selectedDate < 0) {

                    mBinding.calendarCV.setSelectedDates(dateArrayList);

                }

            }

            selectedDate = new Date().getTime();

        }

        for (int i = 0; i < scheduledRides.size(); i++) {

            ScheduleModel rideModel = scheduledRides.get(i);

            SearchedPlaceModel pickUp = rideModel.tripDetail.pickUp;

            double distanceInMiles = distance(pickUp.lat, pickUp.lng, location.getLatitude(), location.getLongitude());

            if (distanceInMiles > 50 && rideType == RideType.upcoming) {

                scheduledRides.remove(rideModel);

            }

        }

        setScheduleAdapter(scheduledRides);

    }

    long selectedDate = -1;

    private void onDateSelected(CalendarView view, int year, int month, int dayOfMonth) {

//        view.getDate()
//        selectedDate = mBinding.calendarCV.getDate();

//        SetupTabView(3);

    }

    private void setScheduleAdapter(ArrayList<ScheduleModel> rides) {

        mBinding.ridesRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBinding.ridesRV.setAdapter(new ScheduledRidesAdapter(getActivity(),
                rides));

    }

    private void setRidesAdapterForUpcoming(ArrayList<RideModel> rides) {
        mBinding.ridesRV.setAdapter(new UpcomingRidesAdapter(getActivity(),
                this.rides,
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


        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

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

        if (selectedRide == null) {

            hideLoader();

            return;
        }

        selectedRide.driverId = getUserId();
        selectedRide.vehicleId = vehicle.getId();
        selectedRide.status = "driverAccepted";

        FirebaseInstances.usersCollection.document(getUserId())
                .update("isActive", false);

        FirebaseFirestore.getInstance().collection("Bookings").document(selectedRide.id)
                .set(selectedRide);

        getRides();

    }


    @Override
    public void onClick(View v) {
        if (v == mBinding.drawerIcon) {

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

            mBinding.calendarCV.setVisibility(View.GONE);

            rideType = RideType.upcoming;

        } else if (position == 1) {

            mBinding.calendarCV.setVisibility(View.GONE);

            rideType = RideType.running;

        } else {

            mBinding.calendarCV.setVisibility(View.GONE);

            rideType = RideType.completed;

        }

        checkPermissionsAndGetRide();
    }
}
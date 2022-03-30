package com.buzzware.iridedriver.Screens;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iridedriver.Firebase.FirebaseInstances;
import com.buzzware.iridedriver.LocationServices.LocationTracker;
import com.buzzware.iridedriver.Models.Payouts.PayoutObj;
import com.buzzware.iridedriver.Models.Promotion.PromotionObj;
import com.buzzware.iridedriver.Models.RideModel;
import com.buzzware.iridedriver.Models.ScheduleModel;
import com.buzzware.iridedriver.Models.SearchedPlaceModel;
import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.Models.response.directions.DirectionsApiResponse;
import com.buzzware.iridedriver.Models.response.directions.Leg;
import com.buzzware.iridedriver.Models.response.directions.Route;
import com.buzzware.iridedriver.Models.response.directions.Step;
import com.buzzware.iridedriver.Models.response.distanceMatrix.DistanceMatrixResponse;
import com.buzzware.iridedriver.Models.response.distanceMatrix.Element;
import com.buzzware.iridedriver.Models.response.distanceMatrix.Row;
import com.buzzware.iridedriver.Models.settings.DriverShare;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHome extends BaseActivity implements OnMapReadyCallback {

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

        setListeners();

        setOrderListener();
    }

    private void setOrderListener() {


        FirebaseFirestore.getInstance().collection("Bookings")
                .document(rideModel.id)
                .addSnapshotListener((value, error) -> {

                    try {

                        RideModel r = value.toObject(RideModel.class);

                        if (r.status.equalsIgnoreCase(AppConstants.RideStatus.CANCELLED)) {

                            FirebaseInstances.usersCollection.document(getUserId())
                                    .update("isActive", true);

                            Toast.makeText(this, "Ride Cancelled", Toast.LENGTH_SHORT).show();

                            finish();
                        } else  if (r.status.equalsIgnoreCase(AppConstants.RideStatus.DISPUTE)) {

                            FirebaseInstances.usersCollection.document(getUserId())
                                    .update("isActive", true);

                            Toast.makeText(this, "Ride Cancelled", Toast.LENGTH_SHORT).show();

                            finish();
                        }   if (r.status.equalsIgnoreCase(AppConstants.RideStatus.DISPUTED)) {

                            FirebaseInstances.usersCollection.document(getUserId())
                                    .update("isActive", true);

                            Toast.makeText(this, "Ride Cancelled", Toast.LENGTH_SHORT).show();

                            finish();
                        }  else if (r.status.equalsIgnoreCase(AppConstants.RideStatus.RIDE_COMPLETED)) {

                            FirebaseInstances.usersCollection.document(getUserId())
                                    .update("isActive", true);

                            Toast.makeText(this, "Ride Completed", Toast.LENGTH_SHORT).show();

                            finish();

                        } else if (r.status.equalsIgnoreCase(AppConstants.RideStatus.RATED)) {

                            FirebaseInstances.usersCollection.document(getUserId())
                                    .update("isActive", true);

                            Toast.makeText(this, "Order Rated", Toast.LENGTH_SHORT).show();

                            finish();
                        }

                    } catch (Exception e) {

                    }

                });
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

        mBinding.backIV.setOnClickListener(v -> finish());
    }

    private void updateRideStatus() {

        if (rideModel.status.equalsIgnoreCase("driverAccepted")) {

            rideModel.status = "driverReached";

        } else if (rideModel.status.equalsIgnoreCase("driverReached")) {

            rideModel.status = "rideStarted";

        } else if (rideModel.status.equalsIgnoreCase("rideStarted")) {

            if (AppConstants.RideStatus.isRideInProgress(rideModel.status)) {

                if (rideModel.tripDetail.destinations.size() > 1) {

                    if (rideModel.tripDetail.destinations.get(0).status.equalsIgnoreCase(AppConstants.RideDetailStatus.NOT_REACHED)) {

                        rideModel.tripDetail.destinations.get(0).status = "1";

                    } else {

                        rideModel.status = "rideCompleted";

                        FirebaseInstances.chatCollection.document(rideModel.id).delete();

                        stopLocationService();

                    }

                } else {

                    rideModel.status = "rideCompleted";

                    FirebaseInstances.chatCollection.document(rideModel.id).delete();

                    stopLocationService();

                }

            }

        }

        showLoader();

        updateRideModel();

        updateRideModel();
    }

    private void updateRideModel() {

        FirebaseFirestore.getInstance().collection("Bookings")
                .document(rideModel.id).
                set(rideModel)
                .addOnCompleteListener(task -> {

                    hideLoader();

                    if (rideModel.status.equalsIgnoreCase("rideCompleted")) {

                        getStripeID();

                        return;
                    }

                    setRideButton();

                    initPlace();

                });
    }

    void getStripeID() {

        FirebaseInstances.usersCollection
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {


                    if (task.isSuccessful()) {

                        User user = task.getResult().toObject(User.class);

                        if (user != null) {

                            if (user.stripeaccount_id != null)

                                createPayout(user.stripeaccount_id);

                            else {

                                showErrorAlert("Please wait for admin approval.");
                            }
                        }
                    }
                });

    }

    private void createPayout(String stripeaccount_id) {

        FirebaseInstances.settingsCollection
                .document("driverShare")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        DriverShare settings = task.getResult().toObject(DriverShare.class);

                        double percent = Double.parseDouble(settings.percent);

                        double amount = Double.parseDouble(rideModel.price) * (percent / 100);

                        PayoutObj payoutObj = new PayoutObj();

                        payoutObj.amount = new Double(amount).intValue();
                        payoutObj.orderId = rideModel.id;
                        payoutObj.stripeaccount_id = stripeaccount_id;
                        payoutObj.driverId = getUserId();
                        payoutObj.type = "order";
                        payoutObj.status = "unpaid";
                        payoutObj.completionDateTime = new Date().toString();
                        payoutObj.completionTimeStamp = new Date().getTime();

                        FirebaseInstances.payoutsCollection.document(rideModel.id)
                                .set(payoutObj)
                                .addOnCompleteListener(task1 -> {

                                    hideLoader();

                                    checkPromotions(stripeaccount_id);

                                });

                    }
                });

    }

    private void checkPromotions(String stripeaccount_id) {

        long time = new Date().getTime() / 1000L;

        FirebaseInstances.promotionsCollection
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                            PromotionObj promotionObj = documentSnapshot.toObject(PromotionObj.class);

                            promotionObj.id = documentSnapshot.getId();

                            if (promotionObj.startTime < time && time < promotionObj.endTime) {

                                if (promotionObj.rideModels == null)

                                    promotionObj.rideModels = new ArrayList<>();

                                promotionObj.rideModels.add(rideModel);

                                checkAndCreatePayout(promotionObj, stripeaccount_id);

                                FirebaseInstances.promotionsCollection.document(promotionObj.id)
                                        .set(promotionObj);

                            }

                        }

                    }


                    setRideButton();

                    initPlace();

                });

    }

    private void checkAndCreatePayout(PromotionObj promotionObj, String stripeaccount_id) {

        String userId = FirebaseAuth.getInstance().getUid();

        List<RideModel> rideModels = promotionObj.rideModels;

        int count = 0;

        for (RideModel rideModel : rideModels) {

            if (rideModel.driverId != null && rideModel.driverId.equalsIgnoreCase(userId)) {

                count++;

            }

        }

        if (count == promotionObj.noOfTrips) {

            createPayout(promotionObj, stripeaccount_id);

        }

    }

    private void createPayout(PromotionObj promotionObj, String stripeId) {

        PayoutObj payoutObj = new PayoutObj();

        payoutObj.amount = new Double(Double.parseDouble(promotionObj.amount)).intValue();
        payoutObj.orderId = null;
        payoutObj.driverId = getUserId();
        payoutObj.promotionId = promotionObj.id;
        payoutObj.type = "promotion";
        payoutObj.stripeaccount_id = stripeId;
        payoutObj.status = "unpaid";
        payoutObj.completionDateTime = new Date().toString();
        payoutObj.completionTimeStamp = new Date().getTime();

        FirebaseInstances.payoutsCollection.document()
                .set(payoutObj);
    }

    private void showCurrentLocation() {

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0F));

    }

    private void performNavigation() {

        double destLat = 0, destLng = 0;

        if (AppConstants.RideStatus.isRideDriverArriving(rideModel.status)) {

            destLat = rideModel.tripDetail.pickUp.lat;
            destLng = rideModel.tripDetail.pickUp.lng;

        } else if (AppConstants.RideStatus.isRideInProgress(rideModel.status)) {

            List<SearchedPlaceModel> destinations = rideModel.tripDetail.destinations;

            if (destinations.size() == 1) {

                destLat = rideModel.tripDetail.destinations.get(0).lat;
                destLng = rideModel.tripDetail.destinations.get(0).lng;

            } else if (destinations.size() == 2) {

                if (destinations.get(0).status.equalsIgnoreCase(AppConstants.RideStatus.DRIVER_REACHED)) {


                    destLat = rideModel.tripDetail.destinations.get(1).lat;
                    destLng = rideModel.tripDetail.destinations.get(1).lng;

                } else {

                    destLat = rideModel.tripDetail.destinations.get(0).lat;
                    destLng = rideModel.tripDetail.destinations.get(0).lng;

                }

            }
        }

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destLat + "," + destLng);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        mapIntent.setPackage("com.google.android.apps.maps");

        startActivity(mapIntent);

    }

    private void getExtrasFromIntent() {

        rideModel = getIntent().getParcelableExtra("ride");

    }

    Call<String> reverseCall;

    void calculateDistance() {


        String url = null;

        if (AppConstants.RideStatus.isRideDriverArriving(rideModel.status))

            url = "/maps/api/distancematrix/json?departure_time&origins=" + location.getLatitude() + "," + location.getLongitude() + "&destinations=" + rideModel.tripDetail.pickUp.lat + "," + rideModel.tripDetail.pickUp.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        else if (AppConstants.RideStatus.isRideInProgress(rideModel.status)) {

            List<SearchedPlaceModel> destinations = rideModel.tripDetail.destinations;

            if (destinations.size() == 1) {

                url = "/maps/api/distancematrix/json?departure_time&origins=" + location.getLatitude() + "," + location.getLongitude() + "&destinations=" + rideModel.tripDetail.destinations.get(0).lat + "," + rideModel.tripDetail.destinations.get(0).lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

            } else if (destinations.size() == 2) {

                if (destinations.get(0).status.equalsIgnoreCase(AppConstants.RideStatus.DRIVER_REACHED)) {

                    url = "/maps/api/distancematrix/json?departure_time&origins=" + location.getLatitude() + "," + location.getLongitude() + "&destinations=" + rideModel.tripDetail.destinations.get(1).lat + "," + rideModel.tripDetail.destinations.get(1).lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

                } else {

                    url = "/maps/api/distancematrix/json?departure_time&origins=" + location.getLatitude() + "," + location.getLongitude() + "&destinations=" + rideModel.tripDetail.destinations.get(0).lat + "," + rideModel.tripDetail.destinations.get(0).lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

                }

            }


        }

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

                    if (AppConstants.RideStatus.RIDE_STARTED.equalsIgnoreCase(rideModel.status) && rideModel.tripDetail.destinations.get(0).status.equalsIgnoreCase(AppConstants.RideDetailStatus.NOT_REACHED)) {

                        //ride is started handle case for driver hasn't reached any location yet draw polyline draw from dest 1 to dest 2
                        //drawing polyline from dest1 to second drop off 2

                        drawSecondPolyline(rideModel);

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }

    void calculateDistance2() {

        String url = "/maps/api/distancematrix/json?departure_time&origins=" + rideModel.tripDetail.destinations.get(0).lat + "," + rideModel.tripDetail.destinations.get(0).lng + "&destinations=" + rideModel.tripDetail.destinations.get(1).lat + "," + rideModel.tripDetail.destinations.get(1).lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

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

                    setDistance2(resp);

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }

    double km = 0;
    double seconds = 0;

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

                if (element.distance != null) {

                    km = element.distance.value;
                    distance = element.distance.text;

                }


                if (element.duration != null) {

                    time = element.duration.text;
                    seconds = element.duration.value;
                }

            }

        }

        mBinding.timeTV.setText(time);
        mBinding.kmTV.setText(distance);
        mBinding.currentLocationTV.setText(currentAddress);
    }

    private void setDistance2(DistanceMatrixResponse resp) {

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

                if (element.distance != null) {
                    distance = element.distance.text;
                    km = element.distance.value + km;
                }

                if (element.duration != null) {

                    time = element.duration.text;
                    seconds = element.duration.value + seconds;
                }

            }

        }

        double min = seconds / 60;
        km = km / 1000;


        mBinding.timeTV.setText(String.format("%.2f", min) + " min");
        mBinding.kmTV.setText(String.format("%.2f", km) + " km");
        mBinding.currentLocationTV.setText(currentAddress);
    }

    void getDirections() {

        String url = null;

        if (rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.DRIVER_ACCEPTED) || rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.DRIVER_REACHED)) {

            //moving directions towards user

            url = "/maps/api/directions/json?origin=" + location.getLatitude() + "," + location.getLongitude() + "&destination=" + rideModel.tripDetail.pickUp.lat + "," + rideModel.tripDetail.pickUp.lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        } else if (rideModel.status.equalsIgnoreCase(AppConstants.RideStatus.RIDE_STARTED)) {

            List<SearchedPlaceModel> destinations = rideModel.tripDetail.destinations;

            if (destinations.size() == 1) {

                url = "/maps/api/directions/json?origin=" + location.getLatitude() + "," + location.getLongitude() + "&destination=" + rideModel.tripDetail.destinations.get(0).lat + "," + rideModel.tripDetail.destinations.get(0).lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;


            } else if (destinations.size() > 1) {

                if (destinations.get(0).status.equalsIgnoreCase(AppConstants.RideDetailStatus.REACHED)) {

                    //from driver to destination 2

                    url = "/maps/api/directions/json?origin=" + location.getLatitude() + "," + location.getLongitude() + "&destination=" + rideModel.tripDetail.destinations.get(1).lat + "," + rideModel.tripDetail.destinations.get(1).lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

                } else {

                    // from driver to dest 1 then 2
                    url = "/maps/api/directions/json?origin=" + location.getLatitude() + "," + location.getLongitude() + "&destination=" + rideModel.tripDetail.destinations.get(0).lat + "," + rideModel.tripDetail.destinations.get(0).lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

                }

            }

        }

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


            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });
    }

    private void drawPaths(DirectionsApiResponse res) {

        path = new ArrayList<>();

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

        if (polyline != null)

            polyline.remove();

        //Draw the polyline
        if (path.size() > 0) {

            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(8);

            mMap.addPolyline(opts);

        }

        calculateDistance();


    }

    private void hideSecondPolyline() {

    }

    Call<String> reverseCall1;

    private void drawSecondPolyline(RideModel rideModel) {

        if (rideModel.tripDetail.destinations.size() > 1) {

            SearchedPlaceModel pickUp = rideModel.tripDetail.destinations.get(0);

            SearchedPlaceModel destination = rideModel.tripDetail.destinations.get(1);

            getDirections2(pickUp.lat, pickUp.lng, destination.lat, destination.lng, false);
        }

    }

    private void getDirections2(double lat, double lng, double lat1, double lng1, boolean b) {

        String url = "/maps/api/directions/json?origin=" + lat + "," + lng + "&destination=" + lat1 + "," + lng1 + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall1 != null) {

            reverseCall1.cancel();

            reverseCall1 = null;
        }

        reverseCall1 = Controller.getApi().getPlaces(url, "asdasd");

        reverseCall1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                reverseCall1 = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    DirectionsApiResponse resp = gson.fromJson(response.body(), DirectionsApiResponse.class);

                    drawPaths2(resp);

                    calculateDistance2();

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                reverseCall = null;
            }
        });

    }

    ArrayList<LatLng> path = new ArrayList<>();


    private void drawPaths2(DirectionsApiResponse res) {


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

        if (polyline != null) {

            polyline.remove();

        }
        //Draw the polyline
        if (path.size() > 0) {

            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(8);

            polyline = mMap.addPolyline(opts);


        }

    }

    Polyline polyline;

    void setRideButton() {

        mBinding.cancelCV.setVisibility(View.GONE);


        mBinding.actionTV.setText("Complete First Drop Off");
        if (rideModel.status.equalsIgnoreCase("driverAccepted")) {

            mBinding.actionTV.setText("Arrived");
            mBinding.cancelCV.setVisibility(View.VISIBLE);

        } else if (rideModel.status.equalsIgnoreCase("driverReached")) {

            mBinding.actionTV.setText("Start Ride");
            mBinding.cancelCV.setVisibility(View.VISIBLE);


        } else if (rideModel.status.equalsIgnoreCase("rideStarted")) {

            if (AppConstants.RideStatus.isRideInProgress(rideModel.status)) {

                if (rideModel.tripDetail.destinations.size() > 1) {

                    if (rideModel.tripDetail.destinations.get(0).status.equalsIgnoreCase(AppConstants.RideDetailStatus.NOT_REACHED)) {

                        mBinding.actionTV.setText("Complete First Drop Off");

                    } else {

                        mBinding.actionTV.setText("Complete");

                    }

                } else

                    mBinding.actionTV.setText("Complete");

            }

        } else {

            mBinding.rideBt.setVisibility(View.GONE);

        }

        mBinding.cancelCV.setOnClickListener(v -> cancelRide());
    }

    private void cancelRide() {

        rideModel.driverId = null;
        rideModel.vehicleId = null;
        rideModel.status = AppConstants.RideStatus.RE_BOOKED;

        FirebaseInstances.usersCollection.document(getUserId())
                .update("isActive", true);

        FirebaseFirestore.getInstance().collection("Bookings")
                .document(rideModel.id).
                set(rideModel);

        finish();
    }

    private void checkPermissionsAndInit() {

        setRideButton();

        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        Permissions.check(DriverHome.this/*context*/, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                hasLocationPermissions = true;

                location = new SimpleLocation(DriverHome.this);

//                if (!location.hasLocationEnabled()) {
//                    // ask the user to enable location access
//                    showEnableLocationDialog("Please enable location from setting in order to proceed to the app");
//
//                    return;
//                }

                location.beginUpdates();

                startLocationService();

                init();

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

                hasLocationPermissions = false;

//                showPermissionsDeniedError("Please enable location permissions from setting in order to proceed to the app.");

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        checkPermissionsAndInit();


    }

    private void checkIfPermissionsGranted() {

        if (ContextCompat.checkSelfPermission(DriverHome.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(DriverHome.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {

            stopLocationService();

            return;
        }

        hasLocationPermissions = true;

        location = new SimpleLocation(DriverHome.this);

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

        initPlace();

        mBinding.currentLocationIV.setOnClickListener(v -> showCurrentLocation());

    }

    List<Marker> markers = new ArrayList<>();

    void initPlace() {

        for (Marker marker : markers) {

            marker.remove();

        }

        markers = new ArrayList<>();

        List<SearchedPlaceModel> destinations = rideModel.tripDetail.destinations;

        if (AppConstants.RideStatus.isRideDriverArriving(rideModel.status)) {

            markers.add(mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(rideModel.tripDetail.pickUp.lat, rideModel.tripDetail.pickUp.lng))
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
            );

        } else {

            markers.add(mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(destinations.get(0).lat, destinations.get(0).lng))
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
            );

            if (destinations.size() > 1) {

                markers.add(mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(destinations.get(1).lat, destinations.get(1).lng))
                        .title("Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));

            }

        }

        getDirections();

        if (AppConstants.RideStatus.isRideInProgress(rideModel.status)) {

            if (rideModel.tripDetail.destinations.size() > 1) {

                if (rideModel.tripDetail.destinations.get(0).status.equalsIgnoreCase(AppConstants.RideDetailStatus.NOT_REACHED)) {

                    mBinding.actionTV.setText("Complete First Drop Off");

                }

            }

        }

    }

}
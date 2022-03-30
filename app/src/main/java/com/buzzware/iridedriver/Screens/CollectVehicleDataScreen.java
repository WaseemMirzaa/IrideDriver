package com.buzzware.iridedriver.Screens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.buzzware.iridedriver.Adapters.CustomSpinnerAdapter;
import com.buzzware.iridedriver.Firebase.FirebaseInstances;
import com.buzzware.iridedriver.Models.VehicleModel;
import com.buzzware.iridedriver.Models.prices.Prices;
import com.buzzware.iridedriver.Models.settings.Price;
import com.buzzware.iridedriver.databinding.ActivityVehicleDetailsBinding;
import com.buzzware.iridedriver.utils.RandomString;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CollectVehicleDataScreen extends BaseActivity {

    ActivityVehicleDetailsBinding binding;

    VehicleModel vehicle;

    FirebaseFirestore db;

    Boolean isFromSideMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityVehicleDetailsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        onViewCreated();
    }

    public static void startVehicleInformation(Context c) {

        c.startActivity(new Intent(c, CollectVehicleDataScreen.class)

                .putExtra("isFromSideMenu", true));

    }

    private void onViewCreated() {

        isFromSideMenu = false;

        initialize();

        getExtrasFromIntent();

        if (isFromSideMenu) {

            binding.continueTV.setText("Save");

        }

        checkVehicleDetails();

        setListeners();
    }

    ArrayList<Price> names = new ArrayList<>();

    private void getPrices() {

        FirebaseFirestore.getInstance().collection("Settings")
                .document("Prices")
                .get()
                .addOnCompleteListener(task -> {
                    hideLoader();
                    if (task.isSuccessful()) {

                        names = new ArrayList<>();

                        Prices settings = task.getResult().toObject(Prices.class);

                        if (settings == null)

                            return;

                        if (settings.iRide != null) {

                            names.add(settings.iRide);

                        }

                        if (settings.iRideLux != null)

                            names.add(settings.iRideLux);

                        if (settings.iRidePlus != null)

                            names.add(settings.iRidePlus);

                        binding.carTypeSp.setAdapter(new CustomSpinnerAdapter(getApplicationContext(), names));


                    } else {

                        if (task.getException() != null && task.getException().getLocalizedMessage() != null)

                            showErrorAlert(task.getException().getLocalizedMessage());
                    }

                });

    }

    private void getExtrasFromIntent() {

        if (getIntent().getExtras() != null) {

            Bundle b = getIntent().getExtras();

            isFromSideMenu = b.getBoolean("isFromSideMenu");

        }

    }

    private void setListeners() {

        binding.btnContinue.setOnClickListener(v -> validateAndSaveData());

        binding.appBarTitleInclude.backIcon.setOnClickListener(v -> finish());

    }

    private void validateAndSaveData() {

        if (validate()) {


            initializeVehicleModel();

            uploadDataToFirestore();

        }
    }

    private void uploadDataToFirestore() {

        showLoader();

        if (vehicle.id == null || vehicle.id.isEmpty())

            vehicle.id = RandomString.getAlphaNumericString(25);

        FirebaseFirestore.getInstance().collection("Vehicle")
                .document(vehicle.id)
                .set(vehicle);

        String isVerified = "notApproved";

        if (validateVerification()) {

            isVerified = "approved";

        }

        Map<String, Object> map = new HashMap<>();

        map.put("vehicleId", vehicle.id);

        map.put("isVerified", isVerified);

        FirebaseInstances.usersCollection
                .document(getUserId())
                .update(map);

        hideLoader();

        if (isFromSideMenu) {

            finish();

            return;

        }

    }

    private void initializeVehicleModel() {

        if (vehicle == null)

            vehicle = new VehicleModel();

        vehicle.make = binding.makeET.getText().toString();

        vehicle.tagNumber = binding.tagNumberET.getText().toString();

        vehicle.noOfDoors = binding.doorsET.getText().toString();

        vehicle.year = binding.yearET.getText().toString();

        vehicle.model = binding.modelET.getText().toString();

        vehicle.noOfSeatBelts = binding.seatBeltsET.getText().toString();

//        vehicle.name = binding.vehicleNameET.getText().toString();

        vehicle.color = binding.colorET.getText().toString();

        vehicle.carType = getCarType();

        vehicle.userId = getUserId();

    }

    private String getCarType() {

        int pos = binding.carTypeSp.getSelectedItemPosition();

        if (pos == 0) {

            return "iRide";

        } else if (pos == 1) {

            return "iRidePlus";

        } else {

            return "iRideLux";

        }
    }

    private boolean validate() {

        if (binding.colorET.getText().toString().isEmpty()) {

            showErrorAlert("Vehicle Color Required");

            return false;
        }

        if (binding.modelET.getText().toString().isEmpty()) {

            showErrorAlert("Vehicle Model Required");

            return false;
        }

        if (binding.tagNumberET.getText().toString().isEmpty()) {

            showErrorAlert("Tag Number Required");

            return false;
        }

        if (binding.seatBeltsET.getText().toString().isEmpty()) {

            showErrorAlert("Seat Belts Info Required");

            return false;
        }

        if (binding.makeET.getText().toString().isEmpty()) {

            showErrorAlert("Vehicle Make Required");

            return false;
        }

        if (binding.doorsET.getText().toString().isEmpty()) {

            showErrorAlert("Doors Info Required");

            return false;
        }

        if (binding.yearET.getText().toString().isEmpty()) {

            showErrorAlert("Year field Required");

            return false;
        }

        return true;
    }

    private void initialize() {

        binding.appBarTitleInclude.backAppBarTitle.setText("Vehicle Details");

        db = FirebaseFirestore.getInstance();

    }

    private void checkVehicleDetails() {

        db.collection("Vehicle")
                .whereEqualTo(
                        "userId",
                        getUserId()
                )
                .get()
                .addOnCompleteListener(
                        this::parseSnapshot
                );

    }

    void parseSnapshot(Task<QuerySnapshot> task) {

        if (task.getResult() != null || task.getResult().size() > 0) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                vehicle = document.toObject(VehicleModel.class);

                vehicle.id = document.getId();
            }

        }

        if (vehicle != null)

            setData();

        getPrices();
    }

    private void setData() {

//        binding.vehicleNameET.setText(vehicle.getName());

        binding.yearET.setText(vehicle.getYear());

        binding.doorsET.setText(vehicle.getNoOfDoors());

        binding.makeET.setText(vehicle.getMake());

        binding.seatBeltsET.setText(vehicle.getNoOfSeatBelts());

        binding.tagNumberET.setText(vehicle.getTagNumber());

        binding.modelET.setText(vehicle.getModel());

        if (vehicle.color != null)

            binding.colorET.setText(vehicle.color);
    }

    Boolean validateVerification() {

        if (!hasImage(vehicle.backInCarUrl)) {

            return false;

        }

        if (!hasImage(vehicle.frontCarUrl)) {

            return false;

        }

        if (!hasImage(vehicle.rearCarUrl)) {

            return false;

        }

        if (!hasImage(vehicle.frontInCarUrl)) {

            return false;

        }

        if (!hasImage(vehicle.leftCarUrl)) {

            return false;

        }

        if (!hasImage(vehicle.rightCarUrl)) {

            return false;

        }

        if (!hasImage(vehicle.registrationUrl)) {

            return false;

        }

        if (!hasImage(vehicle.insuranceUrl)) {

            return false;

        }

        if (!hasImage(vehicle.backInCarUrl)) {

            return false;

        }
//
//        if (vehicle.getName().isEmpty()) {
//
//            return false;
//
//        }

        if(vehicle.color == null)

            return false;

        if (vehicle.getYear().isEmpty()) {

            return false;

        }

        if (vehicle.getNoOfDoors().isEmpty()) {

            return false;

        }

        if (vehicle.getMake().isEmpty()) {

            return false;

        }

        if (vehicle.getNoOfSeatBelts().isEmpty()) {

            return false;

        }

        if (vehicle.getTagNumber().isEmpty()) {

            return false;

        }

        if (vehicle.getModel().isEmpty()) {

            return false;

        }

        return true;
    }

    Boolean hasImage(String image) {

        return !(image == null || image.isEmpty() || image.equalsIgnoreCase("null"));
    }


}

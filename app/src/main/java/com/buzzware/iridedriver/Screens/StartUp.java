package com.buzzware.iridedriver.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.buzzware.iridedriver.Models.VehicleModel;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.ActivityStartupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

public class StartUp extends BaseActivity {

    ActivityStartupBinding mBinding;

    FirebaseFirestore db;

    VehicleModel vehicle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        mBinding = ActivityStartupBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());
//        DataBindingUtil.setContentView(this, R.layout.activity_startup);

    }

    public void Continue(View view) {

        checkIfAlreadySignedUp();

    }

    private void checkIfAlreadySignedUp() {

        if (getUserId().isEmpty()) {

            startActivity(new Intent(this, Authentication.class));

        } else {

            checkVehicleDetails();

        }
    }

    private void checkVehicleDetails() {

        showLoader();

        db = FirebaseFirestore.getInstance();

        db.collection("Vehicle")
                    .whereEqualTo(
                            "userId",
                            getUserId()
                    )
                    .get()
                    .addOnCompleteListener(
                            (OnCompleteListener<QuerySnapshot>)
                                    this::parseSnapshot
                    );


        }



    private void parseSnapshot(Task<QuerySnapshot> task) {

        vehicle = null;

        hideLoader();

        if (task.getResult() != null) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                vehicle = document.toObject(VehicleModel.class);

            }

        }

        if (vehicle == null)

            moveToVehicleRegistrationScreen();

        else {

            checkIfImagesExists();

        }
    }

    private void moveToVehicleRegistrationScreen() {

        startActivity(new Intent(StartUp.this, CollectVehicleDataScreen.class));

        finish();

    }

    Boolean validate() {

        if(!hasImage(vehicle.backInCarUrl)) {

            return false;

        }

        if(!hasImage(vehicle.frontCarUrl)) {

            return false;

        }

        if(!hasImage(vehicle.rearCarUrl)) {

            return false;

        }

        if(!hasImage(vehicle.frontInCarUrl)) {

            return false;

        }

        if(!hasImage(vehicle.leftCarUrl)) {

            return false;

        }

        if(!hasImage(vehicle.rightCarUrl)) {

            return false;

        }

        if(!hasImage(vehicle.registrationUrl)) {

            return false;

        }

        if(!hasImage(vehicle.insuranceUrl)) {

            return false;

        }

        return hasImage(vehicle.backInCarUrl);
    }

    Boolean hasImage(String image) {

        return !(image == null || image.isEmpty() || image.equalsIgnoreCase("null"));
    }

    private void checkIfImagesExists() {

        if (!validate()) {

            moveToAddImagesScreen();

            return;
        }

        moveToHomeScreen();

    }

    private void moveToHomeScreen() {

        startActivity(new Intent(StartUp.this, Home.class));

        finish();

    }

    private void moveToAddImagesScreen() {

        startActivity(new Intent(StartUp.this, UploadVehicleImagesScreen.class));

        finish();

    }

}
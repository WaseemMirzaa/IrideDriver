package com.buzzware.iridedriver.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.Models.VehicleModel;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.CollectVehicleDataScreen;
import com.buzzware.iridedriver.Screens.Home;
import com.buzzware.iridedriver.Screens.StartUp;
import com.buzzware.iridedriver.Screens.UploadVehicleImagesScreen;
import com.buzzware.iridedriver.databinding.FragmentSignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SignInFragment extends BaseFragment {

    FragmentSignInBinding mBinding;

    VehicleModel vehicle;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false);


        FirebaseApp.initializeApp(getActivity());

        init();;

        return mBinding.getRoot();
    }

    private void init() {

        mBinding.btnLogin.setOnClickListener(v -> signIn());
    }

    private void signIn() {

        if (validateLogin()) {

            String email = mBinding.emailET.getText().toString();
            String password = mBinding.passwordET.getText().toString();

            showLoader();

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this::onComplete);
        }
    }

    private boolean validateLogin() {

        if (mBinding.emailET.getText().toString().isEmpty()) {

            showErrorAlert("Email Required");

            return false;
        }

        if (mBinding.passwordET.getText().toString().isEmpty()) {

            showErrorAlert("Password Required");

            return false;
        }

        return true;
    }

    private void onComplete(Task<AuthResult> task) {

        hideLoader();

        if (task.isSuccessful()) {

            getCurrentUserData();

        } else {

            if (task.getException() == null || task.getException().getLocalizedMessage() == null)

                return;

            showErrorAlert(task.getException().getMessage());
        }
    }

    private void getCurrentUserData() {

        DocumentReference users = FirebaseFirestore.getInstance().collection("Users").document(getUserId());

        users.addSnapshotListener((value, error) -> {

            if (value != null) {

                User user = value.toObject(User.class);

                if (user == null || user.userRole == null)
                    return;

                if (user.userRole.equalsIgnoreCase("driver")) {

                    checkVehicleDetails();

                } else {

                    FirebaseAuth.getInstance().signOut();

                    showErrorAlert("Invalid Email. You have used this email as a customer. Can't use same email in driver app.");

                }
            }


        });

    }

    private void checkVehicleDetails() {

        showLoader();

        FirebaseFirestore.getInstance().collection("Vehicle")
                .whereEqualTo(
                        "userId",
                        getUserId()
                )
                .get()
                .addOnCompleteListener(
                        this::parseSnapshot
                );


    }



    private void parseSnapshot(Task<QuerySnapshot> task) {

        hideLoader();

        if (task.getResult() != null || task.getResult().size() > 0) {

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

        startActivity(new Intent(getActivity(), CollectVehicleDataScreen.class));

        getActivity().finish();

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

        if(!hasImage(vehicle.backInCarUrl)) {

            return false;

        }

        return true;
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

        startActivity(new Intent(getActivity(), Home.class));

        getActivity().finish();

    }

    private void moveToAddImagesScreen() {

        startActivity(new Intent(getActivity(), UploadVehicleImagesScreen.class));

        getActivity().finish();

    }

}
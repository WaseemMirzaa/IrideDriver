package com.buzzware.iridedriver.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.CollectVehicleDataScreen;
import com.buzzware.iridedriver.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpFragment extends BaseFragment {

    FragmentSignUpBinding mBinding;

    public String city, state, zipCode;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_sign_up,
                container,
                false);

        init();

        return mBinding.getRoot();
    }

    private void init() {

        mBinding.btnContinue.setOnClickListener(v -> signIn());

    }

    private void signIn() {

        if (validate()) {

            String email = mBinding.emailET.getText().toString();

            String password = mBinding.passwordET.getText().toString();

            showLoader();

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this::onComplete);

        }

    }

    private boolean validate() {

        if (mBinding.fNameET.getText().toString().isEmpty()) {

            showErrorAlert("First Name Required");

            return false;
        }

        if (mBinding.lNameET.getText().toString().isEmpty()) {

            showErrorAlert("Last Name Required");

            return false;
        }

        if (mBinding.emailET.getText().toString().isEmpty()) {

            showErrorAlert("Email Required");

            return false;
        }

        if (mBinding.cityET.getText().toString().isEmpty()) {

            showErrorAlert("City Required");

            return false;
        }

        if (mBinding.stateET.getText().toString().isEmpty()) {

            showErrorAlert("State Required");

            return false;
        }

        if (mBinding.zipET.getText().toString().isEmpty()) {

            showErrorAlert("Zip Code Required");

            return false;
        }

        if (mBinding.phoneET.getText().toString().isEmpty()) {

            showErrorAlert("Phone Required");

            return false;
        }

        if (mBinding.cityET.getText().toString().isEmpty()) {

            showErrorAlert("City Required");

            return false;
        }

        if (mBinding.passwordET.getText().toString().isEmpty()) {

            showErrorAlert("Password Required");

            return false;
        }

        return true;
    }

    private void onComplete(Task<AuthResult> task) {

        if (task.isSuccessful()) {

            User user = new User();

            user.address = mBinding.cityET.getText().toString();
            user.email = mBinding.emailET.getText().toString();
            user.firstName = mBinding.fNameET.getText().toString();
            user.lastName = mBinding.lNameET.getText().toString();
            user.phoneNumber = mBinding.phoneET.getText().toString();
            user.state = mBinding.stateET.getText().toString();
            user.zipcode = mBinding.zipET.getText().toString();

            uploadDataToFirestore(user);

        } else {

            hideLoader();

            if (task.getException() == null || task.getException().getLocalizedMessage() == null)

                return;

            showErrorAlert(task.getException().getMessage());
        }
    }

    private void uploadDataToFirestore(User user) {

        hideLoader();

        FirebaseFirestore.getInstance().collection("Users")
                .document(getUserId())
                .set(user);

        Intent i = new Intent(getActivity(), CollectVehicleDataScreen.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(i);

    }

}
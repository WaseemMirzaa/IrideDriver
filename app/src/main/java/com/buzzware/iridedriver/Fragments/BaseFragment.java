package com.buzzware.iridedriver.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import im.delight.android.location.SimpleLocation;

public class BaseFragment extends Fragment {

    AlertDialog alertDialog, locationPermissionsDialog;

    ProgressDialog progressDialog;

    public void showErrorAlert(String msg) {

        if (alertDialog != null && alertDialog.isShowing())

            alertDialog.dismiss();

        alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(msg)
                .setTitle("Alert")
                .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                .create();

        alertDialog.show();
    }

    public void showPermissionsDeniedError(String msg) {

        if (alertDialog != null && alertDialog.isShowing())

            return;

        if (locationPermissionsDialog != null && locationPermissionsDialog.isShowing())

            return;

        locationPermissionsDialog = new AlertDialog.Builder(getActivity())
                .setMessage(msg)
                .setTitle("Alert")
                .setCancelable(false)
                .setPositiveButton("Open Settings", (dialog, which) -> {

                    dialog.dismiss();

                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:com.buzzware.iridedriver")));

                })
                .create();

        locationPermissionsDialog.show();
    }

    public void showEnableLocationDialog(String msg) {

        if (alertDialog != null && alertDialog.isShowing())

            alertDialog.dismiss();

        if (locationPermissionsDialog != null && locationPermissionsDialog.isShowing())

            return;

        locationPermissionsDialog = new AlertDialog.Builder(getActivity())
                .setMessage(msg)
                .setTitle("Alert")
                .setCancelable(false)
                .setPositiveButton("Settings", (dialog, which) -> {

                    dialog.dismiss();

                    SimpleLocation.openSettings(getActivity());

                })
                .create();

        locationPermissionsDialog.show();
    }

    public void showLoader() {

        if (progressDialog != null && progressDialog.isShowing())

            progressDialog.dismiss();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);

        progressDialog.show();
    }

    public void hideLoader() {

        if (progressDialog != null && progressDialog.isShowing())

            progressDialog.dismiss();
    }

    public String getUserId() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getUid() != null)

            return user.getUid();

        return "";
    }
}

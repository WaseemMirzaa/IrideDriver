package com.buzzware.iridedriver.Screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.buzzware.iridedriver.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import im.delight.android.location.SimpleLocation;

public class BaseActivity extends AppCompatActivity {

    AlertDialog alertDialog, locationPermissionsDialog;

    ProgressDialog progressDialog;

    public void showErrorAlert(String msg) {

        if (alertDialog != null && alertDialog.isShowing())

            alertDialog.dismiss();

        alertDialog = new AlertDialog.Builder(BaseActivity.this)
                .setMessage(msg)
                .setTitle("Alert")
                .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                .create();

        alertDialog.show();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) BaseActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(BaseActivity.this);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showPermissionsDeniedError(String msg) {

        if (alertDialog != null && alertDialog.isShowing())

            return;

        if (locationPermissionsDialog != null && locationPermissionsDialog.isShowing())

            return;

        locationPermissionsDialog = new AlertDialog.Builder(BaseActivity.this)
                .setMessage(msg)
                .setTitle("Alert")
                .setCancelable(false)
                .setPositiveButton("Open Settings", (dialog, which) -> {

                    dialog.dismiss();

                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)));

                })
                .create();

        locationPermissionsDialog.show();
    }

    public void showEnableLocationDialog(String msg) {
//
//        if (alertDialog != null && alertDialog.isShowing())
//
//            alertDialog.dismiss();
//
//        if (locationPermissionsDialog != null && locationPermissionsDialog.isShowing())
//
//            return;
//
//        locationPermissionsDialog = new AlertDialog.Builder(BaseActivity.this)
//                .setMessage(msg)
//                .setTitle("Alert")
//                .setCancelable(false)
//                .setPositiveButton("Settings", (dialog, which) -> {
//
//                    dialog.dismiss();
//
//                    SimpleLocation.openSettings(BaseActivity.this);
//
//                })
//                .create();
//
//        locationPermissionsDialog.show();
    }

    public void showLoader() {

        if (progressDialog != null && progressDialog.isShowing())

            progressDialog.dismiss();

        progressDialog = new ProgressDialog(BaseActivity.this);
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

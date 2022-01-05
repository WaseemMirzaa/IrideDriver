package com.buzzware.iridedriver.app;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.buzzware.iridedriver.utils.AppConstants;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Places.initialize(getApplicationContext(), AppConstants.GOOGLE_PLACES_API_KEY);

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        FirebaseApp.initializeApp(this);
    }
}

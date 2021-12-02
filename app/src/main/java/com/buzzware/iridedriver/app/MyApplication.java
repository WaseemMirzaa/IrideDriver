package com.buzzware.iridedriver.app;

import android.app.Application;

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

        FirebaseApp.initializeApp(this);
    }
}

package com.buzzware.iridedriver.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iridedriver.Firebase.FirebaseInstances;
import com.buzzware.iridedriver.Models.Stripe.UrlResponse;
import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.Models.response.geoCode.ReverseGeoCodeResponse;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.EditProfileActivity;
import com.buzzware.iridedriver.Screens.Home;
import com.buzzware.iridedriver.Screens.Notifications;
import com.buzzware.iridedriver.Screens.WebViewActivity;
import com.buzzware.iridedriver.databinding.FragmentProfileBinding;
import com.buzzware.iridedriver.retrofit.Controller;
import com.buzzware.iridedriver.utils.AppConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import im.delight.android.location.SimpleLocation;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    FragmentProfileBinding binding;

    Context context;

    SimpleLocation location;
    Boolean hasLocationPermissions;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        Init();

        getCurrentUserData();

        setListener();


        return binding.getRoot();
    }


    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> FirebaseInstances.usersCollection.document(getUserId())
                    .update("stripeStatus", "pending"));

    public void openWebViewActivity(User user) {

//        launchSomeActivity.launch(new Intent(getActivity(), WebViewActivity.class)

//                .putExtra("url", user.stripeaccountlinkurl));
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

                location.setListener(() -> reverseGeoCode(location.getLatitude(), location.getLongitude()));

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

                hasLocationPermissions = false;

                showPermissionsDeniedError("Please enable location permissions from setting in order to proceed to the app.");

            }
        });
    }



    Call<String> reverseCall;

    void reverseGeoCode(double lat, double lng) {

        Map<String, Object> map = new HashMap<>();

        map.put("lat", location.getLatitude());
        map.put("lng", location.getLongitude());

        FirebaseFirestore.getInstance().collection("Users").document(getUserId())
                .update(map);

        String url = "/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if (reverseCall != null) {

            reverseCall.cancel();

            reverseCall = null;
        }

        reverseCall = Controller.getApi().getPlaces(url, "asdasd");

        reverseCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                reverseCall = null;

                Gson gson = new Gson();

                if (response.body() != null && response.isSuccessful()) {

                    ReverseGeoCodeResponse reverseGeoCodeResponse = gson.fromJson(response.body(), ReverseGeoCodeResponse.class);

                    setAddress(reverseGeoCodeResponse);

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                reverseCall = null;
            }
        });
    }

    private void setAddress(ReverseGeoCodeResponse reverseGeoCodeResponse) {

        binding.userAddressTV.setText(reverseGeoCodeResponse.results.get(0).formatted_address);

    }

    @Override
    public void onResume() {
        super.onResume();

//        EventBus.getDefault().register(this);

//        checkPermissionsAndInit();

    }


    private void getMyProfile() {

        FirebaseInstances.usersCollection
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {

                    hideLoader();

                    if (task.isSuccessful()) {

                        User user = task.getResult().toObject(User.class);

                        if (user != null) {

//                            if (shouldOpenUrl(user)) {
//
//                                FirebaseInstances.usersCollection.document(getUserId())
//                                        .update("stripeStatus", "await")
//                                        .addOnCompleteListener(task1 -> getLink(user));
//
//
//                            }

                        }

                    }

                });

    }

    private Boolean shouldOpenUrl(User user) {

        if (user.stripeaccount_id != null && !user.stripeaccount_id.isEmpty()) {

            if (user.stripeStatus == null)

                return true;

            if (!user.stripeStatus.equalsIgnoreCase("pending")) {

                return true;

            } else {

                if (user.stripeStatus.equalsIgnoreCase("pending"))

                    showErrorAlert("Please wait, Your account details is under observation. It will take some time to get approved.");

                return false;

            }

        }

        showErrorAlert("Please wait for stripe setup");

        return false;
    }




    private void setListener() {

        binding.editPaymentInfo.setOnClickListener(v -> {
//            getMyProfile();
        });

        binding.editIcon.setOnClickListener(v->{

            startActivity(new Intent(getContext(), EditProfileActivity.class));

        });

        binding.btnChat.setOnClickListener(v -> {

            startActivity(new Intent(getContext(), Chat.class));

        });

        binding.btnSettings.setOnClickListener(v -> {

            startActivity(new Intent(getContext(), EditProfileActivity.class));

        });

        binding.btnNotifications.setOnClickListener(v->{

            startActivity(new Intent(getContext(), Notifications.class));

        });

        binding.drawerIcon.setOnClickListener(v -> OpenCloseDrawer());

    }

    ListenerRegistration snapshotListener;

    private void getCurrentUserData() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && user.getUid() != null) {

            DocumentReference reference = firebaseFirestore.collection("Users").document(user.getUid());

            snapshotListener = reference.addSnapshotListener((value, error) -> {

                User user1 = value.toObject(User.class);

                setUserData(user1);

                checkPermissionsAndInit();

            });

        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(snapshotListener != null)
            snapshotListener.remove();
    }

    private void setUserData(User user) {

        if(user == null)
            return;

        if (user.image != null && !user.image.isEmpty()) {
            Glide.with(getContext()).load(user.image).apply(new RequestOptions().centerCrop()).into(binding.userImageIV);
        }
        binding.userNameTV.setText(user.firstName + " " + user.lastName);
//        binding.userAddressTV.setText(user.homeAddress);
        binding.userPhoneNumberTV.setText(user.phoneNumber);

    }

    private void Init() {
        context = getContext();

        ///init click
        binding.btnNotifications.setOnClickListener(this);
        binding.btnSettings.setOnClickListener(this);

        binding.btnChat.setOnClickListener(v -> moveToChat());
    }

    private void moveToChat() {

        startActivity(new Intent(getContext(), Chat.class));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    private void checkIfPermissionsGranted() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {

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

        location.setListener(() -> reverseGeoCode(location.getLatitude(), location.getLongitude()));

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnNotifications) {
            startActivity(new Intent(getActivity(), Notifications.class));
        } else if (v == binding.btnSettings) {
//            startActivity(new Intent(this, Pgo.class));
        } else if (v == binding.btnChat) {
            startActivity(new Intent(getActivity(), Chat.class));
        }
    }

    public static void OpenCloseDrawer() {

        if (Home.mBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) {

            Home.mBinding.drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            Home.mBinding.drawerLayout.openDrawer(GravityCompat.START);

        }

    }

}
package com.buzzware.iridedriver.Screens;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.buzzware.iridedriver.Firebase.FirebaseInstances;
import com.buzzware.iridedriver.Fragments.CompletedFragment;
import com.buzzware.iridedriver.Fragments.DriverHome;
import com.buzzware.iridedriver.Fragments.HomeFragment;
import com.buzzware.iridedriver.Fragments.InvitationFragment;
import com.buzzware.iridedriver.Fragments.NotificationFragment;
import com.buzzware.iridedriver.Fragments.ProfileFragment;
import com.buzzware.iridedriver.Fragments.PromotionFragment;
import com.buzzware.iridedriver.Fragments.RemaindersFragment;
import com.buzzware.iridedriver.Fragments.WalletFragment;
import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.ActivityHomeBinding;
import com.buzzware.iridedriver.events.OnlineStatusChanged;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity implements View.OnClickListener {

    public static ActivityHomeBinding mBinding;

    Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        setFireBaseToken();

        Init();

    }

    SwitchCompat onlineSwitch;

    private void setFireBaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        return;

                    }

                    String token = task.getResult();

                    addTokenToDB(token);

                });
    }

    private void addTokenToDB(String token) {

        Map<String, Object> userData = new HashMap<>();
        userData.put("token", token);

        FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update(userData);
    }

    private void Init() {

        if (getIntent().getExtras() != null && getIntent().getStringExtra("action") != null) {

            String action = getIntent().getStringExtra("action");

            if (action.equalsIgnoreCase("promotionFragment")) {

                selectedFragment = new PromotionFragment();

            } else if (action.equalsIgnoreCase("remainderFragment")) {

                selectedFragment = new RemaindersFragment();

            } else if (getIntent().getBooleanExtra("l", false))

                selectedFragment = new HomeFragment();

            else {

                selectedFragment = new HomeFragment(2);

            }

//            if (getIntent().getBooleanExtra("newRide", false))
//                selectedFragment = new HomeFragment();
//            else if (getIntent().getBooleanExtra("newRemainder", false))
//                selectedFragment = new RemaindersFragment();
//            else
//                selectedFragment = new HomeFragment(2);
        } else {

            selectedFragment = new HomeFragment();

        }

        drawerListener();

        ((AppCompatActivity) this).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack("bookingsfragment").commit();

        ///init clicks
        mBinding.navView.findViewById(R.id.homeLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.bookingsLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.walletLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.promotionLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.notificationLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.profileLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.inviteLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.csLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.rideHistoryLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.reminderLL).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.vehicleInfoLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.documentsLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.logoutLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.privacyPolicyLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.covidLay).setOnClickListener(v -> {

            OpenCloseDrawer();

            startActivity(new Intent(Home.this, Covid.class));

        });
        mBinding.navView.findViewById(R.id.aboutUsLay).setOnClickListener(v -> {

            OpenCloseDrawer();

            startActivity(new Intent(Home.this, AboutUs.class));

        });

        onlineSwitch = mBinding.navView.findViewById(R.id.OnlineSwitch);

        onlineSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> setOnline(isChecked));

        getCurrentUserData();

    }

    private void setOnline(boolean isChecked) {

        if (user == null)

            return;

        if (user.isOnline != null) {


        } else {

            user.isOnline = false;
        }

        if (user.isOnline != isChecked)

            FirebaseInstances.usersCollection.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .update("isOnline", isChecked)
                    .addOnCompleteListener(task -> {
                        EventBus.getDefault().post(new OnlineStatusChanged());
                    });

    }

    User user;

    private void getCurrentUserData() {

        DocumentReference users = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        users.addSnapshotListener((value, error) -> {

            if (value != null) {

                user = value.toObject(User.class);

                View headerLayout =
                        mBinding.navView.getHeaderView(0);

                if (user == null)

                    return;

                if (user.isOnline != null) {

                    onlineSwitch.setChecked(user.isOnline);

                } else {

                    setOnline(false);

                }


            }


        });

    }

    private void moveToRideHistory() {

        OpenCloseDrawer();

        ((AppCompatActivity) this).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CompletedFragment()).addToBackStack("wallet").commit();

    }

    private void moveToVehicleInfo() {

        OpenCloseDrawer();

        CollectVehicleDataScreen.startVehicleInformation(this);
    }

    private void moveToVehicleDocumentsInfo() {

        OpenCloseDrawer();

        UploadVehicleImagesScreen.startVehicleInformation(this);
    }

    public void drawerListener() {
        mBinding.drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {
            }

            @Override
            public void onDrawerOpened(View view) {
            }

            @Override
            public void onDrawerClosed(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.white));
                }
            }

            @Override
            public void onDrawerStateChanged(int i) {
            }
        });
    }

    public void SetFragemnt() {
        ((AppCompatActivity) this).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack("home").commit();
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.navView.findViewById(R.id.homeLay)) {
            SetFragemnt();
            OpenCloseDrawer();
        } else if (v == mBinding.navView.findViewById(R.id.bookingsLay)) {
            OpenCloseDrawer();
            ((AppCompatActivity) this).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack("home").commit();

        } else if (v == mBinding.navView.findViewById(R.id.promotionLay)) {
            OpenCloseDrawer();
            ((AppCompatActivity) this).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PromotionFragment()).addToBackStack("promotion").commit();
        } else if (v == mBinding.navView.findViewById(R.id.notificationLay)) {
            OpenCloseDrawer();
            ((AppCompatActivity) this).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationFragment()).addToBackStack("notification").commit();
        } else if (v == mBinding.navView.findViewById(R.id.walletLay)) {
            OpenCloseDrawer();
            ((AppCompatActivity) this).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WalletFragment()).addToBackStack("wallet").commit();
        } else if (v == mBinding.navView.findViewById(R.id.profileLay)) {
            OpenCloseDrawer();
            ((AppCompatActivity) this).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).addToBackStack("profile").commit();
        } else if (v == mBinding.navView.findViewById(R.id.inviteLay)) {
            OpenCloseDrawer();
            ((AppCompatActivity) this).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InvitationFragment()).addToBackStack("invite").commit();
        } else if (v == mBinding.navView.findViewById(R.id.reminderLL)) {
            OpenCloseDrawer();
            ((AppCompatActivity) this).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RemaindersFragment()).addToBackStack("reminder").commit();
        } else if (v == mBinding.navView.findViewById(R.id.csLay)) {

            OpenCloseDrawer();

            startActivity(new Intent(Home.this, CreateNewRequestActivity.class));

        } else if (v == mBinding.navView.findViewById(R.id.rideHistoryLay)) {
            OpenCloseDrawer();
            ((AppCompatActivity) this).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CompletedFragment()).addToBackStack("history").commit();
        } else if (v == mBinding.navView.findViewById(R.id.privacyPolicyLay)) {
            OpenCloseDrawer();
            startActivity(new Intent(Home.this, PrivacyPolicyActivity.class));


//            startActivity(new Intent(this, PrivacyPolicyActivity.class));
        } else if (v == mBinding.navView.findViewById(R.id.vehicleInfoLay)) {
            moveToVehicleInfo();
        } else if (v == mBinding.navView.findViewById(R.id.documentsLay)) {
            moveToVehicleDocumentsInfo();
        } else if (v == mBinding.navView.findViewById(R.id.logoutLay)) {
            logout();
        }
    }

    private void logout() {

        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(Home.this, Authentication.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

        finish();

    }

    public static void OpenCloseDrawer() {
        if (Home.mBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
            Home.mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Home.mBinding.drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finishAffinity();
        }
    }
}
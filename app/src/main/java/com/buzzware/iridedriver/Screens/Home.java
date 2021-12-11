package com.buzzware.iridedriver.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iridedriver.Firebase.FirebaseInstances;
import com.buzzware.iridedriver.Fragments.CompletedFragment;
import com.buzzware.iridedriver.Fragments.CustomerRequestsFragment;
import com.buzzware.iridedriver.Fragments.CustomerServiceFragment;
import com.buzzware.iridedriver.Fragments.HomeFragment;
import com.buzzware.iridedriver.Fragments.InvitationFragment;
import com.buzzware.iridedriver.Fragments.NotificationFragment;
import com.buzzware.iridedriver.Fragments.ProfileFragment;
import com.buzzware.iridedriver.Fragments.PromotionFragment;
import com.buzzware.iridedriver.Fragments.WalletFragment;
import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

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

    Switch onlineSwitch;

    private void setFireBaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                        if (!task.isSuccessful()) {

//                            Log.w("FireBase Token", "Fetching FCM registration token failed", task.getException());
                            return;

                        }

                        String token = task.getResult();

                        addTokenToDB(token);

                    }
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

        selectedFragment = new HomeFragment();

        drawerListener();

        ///set defualt fragment
        SetFragemnt();

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
        mBinding.navView.findViewById(R.id.vehicleInfoLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.documentsLay).setOnClickListener(this);
        onlineSwitch = mBinding.navView.findViewById(R.id.OnlineSwitch);

        onlineSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> setOnline(isChecked));

        //   mBinding.vehicleInfoLay.setOnClickListener(v -> moveToVehicleInfo());
        //   mBinding.documentsLay.setOnClickListener(v -> moveToVehicleDocumentsInfo());
        //  mBinding.rideHistoryLay.setOnClickListener(v -> moveToRideHistory());


        getCurrentUserData();

    }

    private void setOnline(boolean isChecked) {

        FirebaseInstances.usersCollection.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update("isOnline", isChecked);

    }

    private void getCurrentUserData() {

        DocumentReference users = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        users.addSnapshotListener((value, error) -> {

            if (value != null) {

                User user = value.toObject(User.class);

                View headerLayout =
                        mBinding.navView.getHeaderView(0);

                if (user == null)

                    return;

                if (user.isOnline != null) {

                    onlineSwitch.setChecked(user.isOnline);

                } else {

                    setOnline(false);

                }

                ImageView picIV = headerLayout.findViewById(R.id.picIV);

                TextView nameTV = headerLayout.findViewById(R.id.nameTV);

                nameTV.setText(user.firstName + " " + user.lastName);

                Glide.with(this).load(user.image).apply(new RequestOptions().centerCrop().placeholder(R.drawable.dummy_girl)).into(picIV);

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
            SetFragemnt();
            OpenCloseDrawer();
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
        } else if (v == mBinding.navView.findViewById(R.id.csLay)) {
            OpenCloseDrawer();
            ((AppCompatActivity) this).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CustomerRequestsFragment()).addToBackStack("cService").commit();
        } else if (v == mBinding.navView.findViewById(R.id.rideHistoryLay)) {
            OpenCloseDrawer();
            ((AppCompatActivity) this).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CompletedFragment()).addToBackStack("history").commit();
        } else if (v == mBinding.navView.findViewById(R.id.vehicleInfoLay)) {
            moveToVehicleInfo();
        } else if (v == mBinding.navView.findViewById(R.id.documentsLay)) {
           moveToVehicleDocumentsInfo();
        }
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
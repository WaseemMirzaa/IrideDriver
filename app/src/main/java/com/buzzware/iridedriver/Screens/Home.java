package com.buzzware.iridedriver.Screens;

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

import com.buzzware.iridedriver.Fragments.CompletedFragment;
import com.buzzware.iridedriver.Fragments.CustomerServiceFragment;
import com.buzzware.iridedriver.Fragments.HomeFragment;
import com.buzzware.iridedriver.Fragments.InvitationFragment;
import com.buzzware.iridedriver.Fragments.ProfileFragment;
import com.buzzware.iridedriver.Fragments.WalletFragment;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity implements View.OnClickListener {

    public static ActivityHomeBinding mBinding;
    Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        try {

            Init();

        } catch (Exception e) {

            e.printStackTrace();

        }
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
        mBinding.navView.findViewById(R.id.profileLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.inviteLay).setOnClickListener(this);
        mBinding.navView.findViewById(R.id.csLay).setOnClickListener(this);

        mBinding.vehicleInfoLay.setOnClickListener(v -> moveToVehicleInfo());
        mBinding.documentsLay.setOnClickListener(v -> moveToVehicleDocumentsInfo());
        mBinding.rideHistoryLay.setOnClickListener(v -> moveToRideHistory());


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
            ((AppCompatActivity) this).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CustomerServiceFragment()).addToBackStack("cService").commit();
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
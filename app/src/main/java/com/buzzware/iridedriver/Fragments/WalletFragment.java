package com.buzzware.iridedriver.Fragments;

import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.Home;
import com.buzzware.iridedriver.databinding.FragmentWalletBinding;

public class WalletFragment extends Fragment {

    FragmentWalletBinding mBinding;

    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet, container, false);

        Init();

        return mBinding.getRoot();
    }

    private void Init() {

        mBinding.drawerIcon.setOnClickListener(v->{
            OpenCloseDrawer();
        });

    }
    public static void OpenCloseDrawer() {

        if (Home.mBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) {

            Home.mBinding.drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            Home.mBinding.drawerLayout.openDrawer(GravityCompat.START);

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}
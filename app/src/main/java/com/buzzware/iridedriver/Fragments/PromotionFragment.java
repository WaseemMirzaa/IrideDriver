package com.buzzware.iridedriver.Fragments;

import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iridedriver.Adapters.PromotionsAdapter;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.Home;
import com.buzzware.iridedriver.databinding.FragmentPromotionBinding;

public class PromotionFragment extends Fragment {


    FragmentPromotionBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_promotion, container, false);

        setListener();

        setRecycler();

        return binding.getRoot();

    }

    private void setRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        binding.promotionRV.setLayoutManager(layoutManager);

        PromotionsAdapter normalBottleAdapter = new PromotionsAdapter(getContext(), null);

        binding.promotionRV.setAdapter(normalBottleAdapter);

    }

    private void setListener() {

        binding.drawerIcon.setOnClickListener(v-> OpenCloseDrawer() );

    }

    public static void OpenCloseDrawer() {

        if (Home.mBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) {

            Home.mBinding.drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            Home.mBinding.drawerLayout.openDrawer(GravityCompat.START);

        }

    }


}
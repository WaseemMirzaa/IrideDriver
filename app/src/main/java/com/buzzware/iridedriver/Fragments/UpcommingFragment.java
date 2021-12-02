package com.buzzware.iridedriver.Fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iridedriver.Adapters.UpcomingRidesAdapter;
import com.buzzware.iridedriver.Models.RideModel;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.FragmentUpcommingBinding;

import java.util.ArrayList;
import java.util.List;

public class UpcommingFragment extends Fragment {

    FragmentUpcommingBinding mBinding;
    UpcomingRidesAdapter upcomingRidesAdapter;
    List<RideModel> historyModelList;

    public UpcommingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_upcomming, container, false);
        try {
            Init();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return mBinding.getRoot();
    }

    private void Init() {
        historyModelList= new ArrayList<>();
        SetDummyList();
        mBinding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
//        upcomingRidesAdapter = new UpcomingRidesAdapter(getContext(), historyModelList, rideModel -> {
//
//        }, rideType);
//        mBinding.rvHistory.setAdapter(upcomingRidesAdapter);
        upcomingRidesAdapter.notifyDataSetChanged();
    }

    private void SetDummyList() {
        historyModelList.add(new RideModel());
        historyModelList.add(new RideModel());
        historyModelList.add(new RideModel());
        historyModelList.add(new RideModel());
        historyModelList.add(new RideModel());
        historyModelList.add(new RideModel());
        historyModelList.add(new RideModel());
        historyModelList.add(new RideModel());
        historyModelList.add(new RideModel());
        historyModelList.add(new RideModel());
    }
}
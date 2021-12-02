package com.buzzware.iridedriver.Fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iridedriver.Adapters.CompletedRidesAddapter;
import com.buzzware.iridedriver.Models.RideModel;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.databinding.FragmentCompletedBinding;

import java.util.ArrayList;
import java.util.List;

public class CompletedFragment extends Fragment {

    FragmentCompletedBinding mBinding;
    CompletedRidesAddapter historyAddapter;
    List<RideModel> historyModelList;

    public CompletedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_completed, container, false);
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
        historyAddapter= new CompletedRidesAddapter(getContext(), historyModelList);
        mBinding.rvHistory.setAdapter(historyAddapter);
        historyAddapter.notifyDataSetChanged();
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
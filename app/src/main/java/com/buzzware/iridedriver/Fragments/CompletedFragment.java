package com.buzzware.iridedriver.Fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iridedriver.Adapters.CompletedRidesAddapter;
import com.buzzware.iridedriver.Adapters.UpcomingRidesAdapter;
import com.buzzware.iridedriver.Models.RideModel;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.Home;
import com.buzzware.iridedriver.databinding.FragmentCompletedBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompletedFragment extends BaseFragment {

    FragmentCompletedBinding mBinding;

    public CompletedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_completed, container, false);

        getRides();

        setListeners();

        return mBinding.getRoot();
    }

    private void setListeners() {

        mBinding.menuAppBar.backAppBarTitle.setText("History");
        mBinding.menuAppBar.backIcon.setOnClickListener(v -> Home.OpenCloseDrawer());

    }

    private void getRides() {

        showLoader();

        Query query = FirebaseFirestore.getInstance().collection("Bookings")
                .whereEqualTo("driverId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereIn("status", Arrays.asList("rated", "rideCompleted"));

        query.get()
                .addOnCompleteListener(
                        this::parseSnapshot
                );

    }

    ArrayList<RideModel> rides;

    void parseSnapshot(Task<QuerySnapshot> task) {

        hideLoader();

        rides = new ArrayList<>();

        if (task.getResult() != null) {

            for (QueryDocumentSnapshot document : task.getResult()) {

                RideModel rideModel = document.toObject(RideModel.class);

                rideModel.id = document.getId();

                rides.add(rideModel);

            }

        }

        setAdapter(rides);
    }

    private void setAdapter(ArrayList<RideModel> rides) {

        mBinding.rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBinding.rvHistory.setAdapter(new UpcomingRidesAdapter(getActivity(),
                rides,
                new UpcomingRidesAdapter.UpcomingRideActionListener() {
                    @Override
                    public void acceptRide(RideModel rideModel) {

//                        accept(rideModel);

                    }

                    @Override
                    public void moveToCompleteScreen(RideModel rideModel) {

//                        moveToOnTrip(rideModel);

                    }
                },
                RideType.completed));

    }
}
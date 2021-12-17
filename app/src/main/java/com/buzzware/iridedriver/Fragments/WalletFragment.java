package com.buzzware.iridedriver.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iridedriver.Adapters.PaymentsAdapter;
import com.buzzware.iridedriver.Firebase.FirebaseInstances;
import com.buzzware.iridedriver.Models.Payouts.PayoutObj;
import com.buzzware.iridedriver.Models.Payouts.RideWithPayoutModel;
import com.buzzware.iridedriver.Models.RideModel;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.Home;
import com.buzzware.iridedriver.databinding.FragmentWalletBinding;
import com.buzzware.iridedriver.utils.AppConstants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WalletFragment extends BaseFragment {

    FragmentWalletBinding mBinding;

    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet, container, false);

        init();

        getRides();

        return mBinding.getRoot();
    }

    private void getRides() {

        showLoader();

        FirebaseInstances.bookingsCollection
                .whereEqualTo("driverId", getUserId())
                .whereIn("status", Arrays.asList(AppConstants.RideStatus.RIDE_COMPLETED, AppConstants.RideStatus.RATED))
                .get()
                .addOnCompleteListener(this::onGetRidesTaskCompleted);

    }

    List<RideWithPayoutModel> completedRides = new ArrayList<>();
    List<PayoutObj> payouts = new ArrayList<>();

    private void onGetRidesTaskCompleted(Task<QuerySnapshot> task) {

        completedRides = new ArrayList<>();

        if (task.isSuccessful()) {

            if (task.getResult() != null) {

                for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                    RideWithPayoutModel rideModel = snapshot.toObject(RideWithPayoutModel.class);

                    if (rideModel != null) {

                        rideModel.id = snapshot.getId();

                        completedRides.add(rideModel);
                    }

                }
            }

        }

        getPayouts();

    }

    private void getPayouts() {

        FirebaseInstances.payoutsCollection

                .whereEqualTo("driverId", getUserId())

                .get()

                .addOnCompleteListener(this::onPayoutsInfoReceived);

    }

    private void onPayoutsInfoReceived(Task<QuerySnapshot> task) {

        payouts = new ArrayList<>();

        if (task.isSuccessful()) {

            if (task.getResult() != null) {

                for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                    PayoutObj payoutObj = snapshot.toObject(PayoutObj.class);

                    if (payoutObj != null) {

                        payoutObj.id = snapshot.getId();

                        updateRides(payoutObj);

                        payouts.add(payoutObj);

                    }

                }

            }

        }

        List<RideWithPayoutModel> list = new ArrayList<>();

        for (RideWithPayoutModel ride : completedRides) {

            if(ride.payout != null) {
                list.add(ride);
            }
        }

        completedRides.clear();
        completedRides.addAll(list);
        hideLoader();

        setAdapter();

    }

    Float amount = 0.0f;

    private void setAdapter() {

        mBinding.paymentsRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBinding.paymentsRV.setAdapter(new PaymentsAdapter(getActivity(), completedRides));

        calculateAmount();
    }

    @SuppressLint("SetTextI18n")
    private void calculateAmount() {

        for (PayoutObj payoutObj : payouts) {

            if (!payoutObj.status.equalsIgnoreCase("paid")) {

                amount = Float.parseFloat(payoutObj.amount) + amount;

            }
        }

        mBinding.amountTV.setText("$" + amount);

    }

    private void updateRides(PayoutObj payoutObj) {

        if (payoutObj.orderId == null)

            return;

        for (int i = 0; i < completedRides.size(); i++) {

            if (payoutObj.orderId.equalsIgnoreCase(completedRides.get(i).id)) {

                completedRides.get(i).payout = payoutObj;

                return;

            }

        }

    }

    private void init() {

        mBinding.drawerIcon.setOnClickListener(v -> openCloseDrawer());

    }

    public static void openCloseDrawer() {

        if (Home.mBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) {

            Home.mBinding.drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            Home.mBinding.drawerLayout.openDrawer(GravityCompat.START);

        }
    }
}
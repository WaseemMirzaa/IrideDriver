package com.buzzware.iridedriver.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iridedriver.Adapters.PaymentsAdapter;
import com.buzzware.iridedriver.Firebase.FirebaseInstances;
import com.buzzware.iridedriver.Models.Payouts.PayoutObj;
import com.buzzware.iridedriver.Models.Payouts.RideWithPayoutModel;
import com.buzzware.iridedriver.Models.Stripe.UrlResponse;
import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.Home;
import com.buzzware.iridedriver.Screens.WebViewActivity;
import com.buzzware.iridedriver.databinding.FragmentWalletBinding;
import com.buzzware.iridedriver.retrofit.Controller;
import com.buzzware.iridedriver.utils.AppConstants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletFragment extends BaseFragment {

    FragmentWalletBinding mBinding;

    String TAG = "WalletFragment";

    int LAUNCH_WEB_VIEW_CLIENT = 100;

    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet, container, false);

        init();

        setListeners();

        getRides();

        return mBinding.getRoot();
    }

    private void setListeners() {

        mBinding.addAccountBt.setOnClickListener(v -> {

            getMyProfile();

        });

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

                            if (shouldOpenUrl(user)) {

                                FirebaseInstances.usersCollection.document(getUserId())
                                        .update("stripeStatus", "await")
                                        .addOnCompleteListener(task1 -> getLink(user));


                            }

                        }

                    }

                });

    }

    private void getLink(User user) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("account_id", user.stripeaccount_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Controller.getApiClient(Controller.Base_Url_CLoudFunctions)
                .getStripeLink(body)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.body() != null) {

                            UrlResponse urlResponse = null;
                            try {

                                urlResponse = new Gson().fromJson(response.body(), UrlResponse.class);

                            } catch (Exception e) {

                                e.printStackTrace();
                            }

                            if(urlResponse != null && urlResponse.url != null) {

                                user.stripeaccountlinkurl = urlResponse.url;

                                openWebViewActivity(user);

                            }

                        }

                        Log.d(TAG, "onResponse: " + response.body());

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

    }

    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> FirebaseInstances.usersCollection.document(getUserId())
                    .update("stripeStatus", "pending"));

    public void openWebViewActivity(User user) {

        launchSomeActivity.launch(new Intent(getActivity(), WebViewActivity.class)

                .putExtra("url", user.stripeaccountlinkurl));
    }

    private Boolean shouldOpenUrl(User user) {

        if (user.stripeaccount_id != null && !user.stripeaccount_id.isEmpty()) {

            if (user.stripeStatus == null)

                return true;

            if (!user.stripeStatus.equalsIgnoreCase("approved") && !user.stripeStatus.equalsIgnoreCase("pending")) {

                return true;

            } else {

                if (user.stripeStatus.equalsIgnoreCase("approved"))

                    showErrorAlert("Your Account is Already Approved");

                if (user.stripeStatus.equalsIgnoreCase("pending"))

                    showErrorAlert("Please wait, Your account details is under observation. It will take some time to get approved.");

                return false;

            }

        }

        showErrorAlert("Please wait for stripe setup");

        return false;
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

            if (ride.payout != null) {
                list.add(ride);
            }
        }

        completedRides.clear();
        completedRides.addAll(list);
        hideLoader();

        setAdapter();

    }

    int amount = 0;

    private void setAdapter() {

        mBinding.paymentsRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBinding.paymentsRV.setAdapter(new PaymentsAdapter(getActivity(), completedRides));

        calculateAmount();
    }

    @SuppressLint("SetTextI18n")
    private void calculateAmount() {

        for (PayoutObj payoutObj : payouts) {

            if (!payoutObj.status.equalsIgnoreCase("paid")) {

                amount = payoutObj.amount + amount;

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
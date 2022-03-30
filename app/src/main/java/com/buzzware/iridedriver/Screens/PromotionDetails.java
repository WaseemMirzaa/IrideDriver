package com.buzzware.iridedriver.Screens;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iridedriver.Models.Promotion.PromotionObj;
import com.buzzware.iridedriver.Models.RideModel;
import com.buzzware.iridedriver.databinding.ActivityPromotionDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PromotionDetails extends BaseActivity {

    PromotionObj promotionObj;

    ActivityPromotionDetailsBinding binding;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPromotionDetailsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.include2.backIcon.setOnClickListener(v -> onBackPressed());

        binding.include2.backAppBarTitle.setText("Promotion");

        getExtrasFromIntent();

        setTexts();

    }

    private void setTexts() {

        if (promotionObj != null) {

            binding.progressBar.setProgress(getProgress(promotionObj.rideModels, promotionObj));

            if(promotionObj.url != null)
            {
                binding.picIV.setVisibility(View.VISIBLE);

                Glide.with(PromotionDetails.this)
                        .load(promotionObj.url)
                        .apply(new RequestOptions().centerCrop())
                        .into(binding.picIV);
            }

            binding.titleTV.setText(promotionObj.getTitle());

            binding.descTV.setText(promotionObj.getMessage());

            binding.endTimeTv.setText("End " + getDateTime(promotionObj.endTime));

            binding.startTimeTV.setText("Start " + getDateTime(promotionObj.startTime));

            binding.remainingRides.setText("Required Rides "+getRemainingRides(promotionObj.rideModels, promotionObj));

        }

    }

    private void getExtrasFromIntent() {

        if (getIntent().getExtras() != null) {

            promotionObj = getIntent().getParcelableExtra("promotion");

        }

    }


    private int getProgress(List<RideModel> rideModels, PromotionObj promotionObj) {

        int count = 0;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (rideModels == null || rideModels.size() == 0) {

            return 0;

        }

        for (RideModel rideModel : rideModels) {

            if (rideModel.driverId != null && rideModel.driverId.equalsIgnoreCase(userId)) {

                count++;

            }
        }

        double progress = (Double.valueOf(count) / Double.valueOf(promotionObj.noOfTrips)) * 100;

        if (count >= promotionObj.noOfTrips) {

            return 100;

        }

        return Double.valueOf(progress).intValue();

    }

    private int getRemainingRides(List<RideModel> rideModels, PromotionObj promotionObj) {

        int count = 0;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (rideModels == null || rideModels.size() == 0) {

            return 0;

        }

        for (RideModel rideModel : rideModels) {

            if (rideModel.driverId != null && rideModel.driverId.equalsIgnoreCase(userId)) {

                count++;

            }
        }

        double remaining = Double.valueOf(promotionObj.noOfTrips) - Double.valueOf(count);

        if (remaining >= 0) {

            return Double.valueOf(remaining).intValue();

        } else return 0;
    }


    private String getDateTime(long bookingDate) {

        Date date = new Date(bookingDate);

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd - HH:mm a");

        return formatter.format(date);

    }

}

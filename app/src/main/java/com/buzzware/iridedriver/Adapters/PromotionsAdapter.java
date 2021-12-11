package com.buzzware.iridedriver.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.iridedriver.Models.Promotion.PromotionObj;
import com.buzzware.iridedriver.Models.RideModel;
import com.buzzware.iridedriver.databinding.PromotionItemDesginBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class PromotionsAdapter extends RecyclerView.Adapter<PromotionsAdapter.ViewHolder> {

    private List<PromotionObj> list;

    private Context mContext;


    public PromotionsAdapter(Context mContext, List<PromotionObj> list) {

        this.list = list;

        this.mContext = mContext;

    }

    @NonNull
    @Override
    public PromotionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(PromotionItemDesginBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        PromotionObj promotionObj = list.get(i);

        viewHolder.binding.progressBar.setProgress(getProgress(promotionObj.rideModels, promotionObj));

        viewHolder.binding.titleTV.setText(promotionObj.getTitle());
        viewHolder.binding.descTV.setText(promotionObj.getMessage());
        viewHolder.binding.endTimeTv.setText("End " + getDateTime(promotionObj.endTime));
        viewHolder.binding.startTimeTV.setText("Start " + getDateTime(promotionObj.startTime));
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

    private String getDateTime(long bookingDate) {

        Date date = new Date(bookingDate * 1000L);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat tf = new SimpleDateFormat(" HH:mm a");

        return "Date: " + df.format(date) + ", Time: " + tf.format(date);

    }


    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        PromotionItemDesginBinding binding;


        public ViewHolder(@NonNull PromotionItemDesginBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}

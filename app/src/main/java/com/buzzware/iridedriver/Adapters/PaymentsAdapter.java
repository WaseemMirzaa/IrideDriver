package com.buzzware.iridedriver.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.iridedriver.Models.Payouts.RideWithPayoutModel;
;
import com.buzzware.iridedriver.databinding.ItemPaymentBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.PaymentHolder> {

    Context context;

    List<RideWithPayoutModel> list;

    public PaymentsAdapter(Context context, List<RideWithPayoutModel
            > list) {

        this.context = context;

        this.list = list;

    }

    @Override
    public @NotNull
    PaymentHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemPaymentBinding binding = ItemPaymentBinding.inflate(LayoutInflater.from(context), parent, false);

        return new PaymentHolder(binding);
    }

    @Override
    public void onBindViewHolder(final PaymentHolder holder, final int position) {

        RideWithPayoutModel
                ride = list.get(position);

        holder.bind(ride);

    }

    @Override
    public int getItemCount() {

        return list.size();

    }

    public class PaymentHolder extends RecyclerView.ViewHolder {

        public ItemPaymentBinding binding;

        public PaymentHolder(ItemPaymentBinding binding) {

            super(binding.getRoot());

            this.binding = binding;

        }

        @SuppressLint("SetTextI18n")
        public void bind(RideWithPayoutModel
                                 ride) {

            if (ride.payout != null)

                binding.statusTV.setText(getPayoutStatus(ride.payout.status));

            else

                binding.statusTV.setText("Pending");

            if (ride.price != null)

                binding.priceTV.setText("Price $" + ride.payout.amount);

            setDestinations(ride);

            binding.timeTV.setText(getDateTime(ride.bookingDate));

        }

        private void setDestinations(RideWithPayoutModel ride) {

            String pickupAddress = getPickUpAddress(ride);
            String destinationAddress = getDestinationAddress(ride);

            if (pickupAddress != null)

                binding.pickUpAddressTV.setText(pickupAddress);

            if (destinationAddress != null)

                binding.destinationAddressTV.setText(destinationAddress);

            if (ride.tripDetail.destinations.size() > 1) {

                String dropOffAddress = ride.tripDetail.destinations.get(0).address;
                destinationAddress = ride.tripDetail.destinations.get(1).address;

                binding.destinationAddressTV.setText(destinationAddress);

                binding.dropOffTv.setText(dropOffAddress);

                binding.dropOffVw.setVisibility(View.VISIBLE);
                binding.dropOffLL.setVisibility(View.VISIBLE);

            } else {

                binding.dropOffVw.setVisibility(View.GONE);

                binding.dropOffLL.setVisibility(View.GONE);

            }
        }

        private String getDestinationAddress(RideWithPayoutModel
                                                     ride) {

            if (ride.tripDetail.destinations.get(0) != null && ride.tripDetail.destinations.get(0).address != null)

                return ride.tripDetail.destinations.get(0).address;

            return "";
        }

        private String getDateTime(long bookingDate) {

            Date date = new Date(bookingDate);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat tf = new SimpleDateFormat(" HH:mm");

            return "Date: " + df.format(date) + "\nTime: " + tf.format(date);

        }

        private String getPickUpAddress(RideWithPayoutModel
                                                ride) {

            if (ride.tripDetail.pickUp != null && ride.tripDetail.pickUp.address != null)

                return ride.tripDetail.pickUp.address;

            return "";
        }


        private String getPayoutStatus(String status) {

            if(status.equalsIgnoreCase("paid"))

                return "Paid";

            return "Pending";

        }

    }

}

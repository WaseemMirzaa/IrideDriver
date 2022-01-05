package com.buzzware.iridedriver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.iridedriver.Firebase.FirebaseInstances;
import com.buzzware.iridedriver.Fragments.RideType;
import com.buzzware.iridedriver.Models.RideModel;
import com.buzzware.iridedriver.Models.User;
import com.buzzware.iridedriver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UpcomingRidesAdapter extends RecyclerView.Adapter<UpcomingRidesAdapter.UpcomingRidesHolder> {

    Context context;

    List<RideModel> historyModels;

    public UpcomingRideActionListener listener;

    RideType rideType;

    public UpcomingRidesAdapter(Context context, List<RideModel> historyModels, UpcomingRideActionListener listener, RideType rideType) {

        this.listener = listener;

        this.context = context;

        this.historyModels = historyModels;

        this.rideType = rideType;

    }

    @Override
    public UpcomingRidesHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcomming_rides_item_lay,
                parent,
                false
        );

        return new UpcomingRidesHolder(view);
    }

    @Override
    public void onBindViewHolder(final UpcomingRidesHolder holder, final int position) {

        RideModel ride = historyModels.get(position);

        holder.pickUpAddressTV.setText(getPickUpAddress(ride));

        holder.destinationAddressTV.setText(getDestinationAddress(ride));

        if (ride.tripDetail.destinations.size() > 1) {

            holder.destinationAddressTV.setText(ride.tripDetail.destinations.get(1).address);

            holder.dropOffTv.setText(ride.tripDetail.destinations.get(0).address);

            holder.dropOffVw.setVisibility(View.VISIBLE);
            holder.dropOffLL.setVisibility(View.VISIBLE);

        } else {

            holder.dropOffVw.setVisibility(View.GONE);

            holder.dropOffLL.setVisibility(View.GONE);

        }

        holder.timeTV.setText(getDateTime(ride.bookingDate));

        if (rideType == RideType.completed)

            holder.acceptNowBt.setVisibility(View.GONE);

        else if (rideType == RideType.running) {

            holder.acceptTV.setText("View");

        } else

            holder.acceptTV.setText("Accept Now");

        holder.acceptNowBt.setOnClickListener(v -> {

            if (rideType == RideType.running) {

                listener.moveToCompleteScreen(ride);

            } else

                listener.acceptRide(ride);


        });

    }


    private String getDateTime(long bookingDate) {

        Date date = new Date(bookingDate);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy HH:MM");

        return formatter.format(date);

    }

    private String getPickUpAddress(RideModel ride) {

        if (ride.tripDetail.pickUp != null && ride.tripDetail.pickUp.address != null)

            return ride.tripDetail.pickUp.address;

        return "";
    }

    private String getDestinationAddress(RideModel ride) {

        if (ride.tripDetail.destinations.get(0) != null && ride.tripDetail.destinations.get(0).address != null)

            return ride.tripDetail.destinations.get(0).address;

        return "";
    }

    @Override
    public int getItemCount() {
        return historyModels.size();
    }

    public class UpcomingRidesHolder extends RecyclerView.ViewHolder {

        public TextView pickUpAddressTV;

        public TextView destinationAddressTV;

        public TextView timeTV;

        public TextView acceptTV;

        public RelativeLayout acceptNowBt;
        public RelativeLayout relativeLayout;
        public View dropOffVw;
        public RelativeLayout dropOffLL;
        public TextView dropOffTv;

        public UpcomingRidesHolder(View itemView) {
            super(itemView);

            initView(itemView);
        }

        private void initView(View view) {

            pickUpAddressTV = itemView.findViewById(R.id.pickUpAddressTV);

            timeTV = itemView.findViewById(R.id.timeTV);

            destinationAddressTV = itemView.findViewById(R.id.destinationAddressTV);

            acceptNowBt = itemView.findViewById(R.id.acceptNowBt);

            acceptTV = itemView.findViewById(R.id.acceptTV);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            dropOffVw = (View) itemView.findViewById(R.id.dropOffVw);
            dropOffLL = (RelativeLayout) itemView.findViewById(R.id.dropOffLL);
            dropOffTv = (TextView) itemView.findViewById(R.id.dropOffTv);
        }
    }

    public interface UpcomingRideActionListener {

        void acceptRide(RideModel rideModel);

        void moveToCompleteScreen(RideModel rideModel);

    }
}

package com.buzzware.iridedriver.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


import com.buzzware.iridedriver.Models.RideModel;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.OnTrip;

import java.util.List;

public class CompletedRidesAddapter extends RecyclerView.Adapter<CompletedRidesAddapter.imageViewHolder>{
    public Context context;
    List<RideModel> historyModels;

    public CompletedRidesAddapter(Context context, List<RideModel> historyModels) {
        this.context = context;
        this.historyModels = historyModels;
    }

    @Override
    public imageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.complete_rides_item_lay, parent, false);
        return new imageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final imageViewHolder holder, final int position) {
        final RideModel historyModel= historyModels.get(position);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, OnTrip.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyModels.size();
    }

    public class imageViewHolder extends RecyclerView.ViewHolder {

        View view;

        public imageViewHolder(View itemView) {
            super(itemView);
            view= itemView;
        }
    }
}

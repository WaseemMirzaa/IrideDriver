package com.buzzware.iridedriver.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iridedriver.Screens.NotificationDetail;
import com.buzzware.iridedriver.databinding.NotificationReadItemBinding;
import com.buzzware.iridedriver.databinding.ReminderItemBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private List<Reminder> list;

    private Context mContext;


    public ReminderAdapter(Context mContext, List<Reminder> list) {

        this.list = list;

        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ReminderItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        Reminder notificationModel=list.get(i);

        viewHolder.binding.messageTV.setText(notificationModel.message);

        viewHolder.binding.messageTV.setText(notificationModel.r_date);

        viewHolder.binding.getRoot().setOnClickListener(v -> {

            NotificationDetail.startNotificationDetail(mContext, "Reminder", notificationModel.message,notificationModel.url);

        });
    }


    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ReminderItemBinding binding;

        public ViewHolder(@NonNull ReminderItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

    public  String convertFormat(String inputDate) {
        Date date = null;

        date = new Date(Long.parseLong(inputDate));

        SimpleDateFormat dF = new SimpleDateFormat("hh:mm a");

        return dF.format(date);
    }


}

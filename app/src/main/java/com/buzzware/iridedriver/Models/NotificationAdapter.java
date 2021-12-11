package com.buzzware.iridedriver.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iridedriver.databinding.NotificationReadItemBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationModel> list;

    private Context mContext;


    public NotificationAdapter(Context mContext, List<NotificationModel> list) {

        this.list = list;

        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(NotificationReadItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        NotificationModel notificationModel=list.get(i);

        if (notificationModel.getImage() != null && !notificationModel.getImage().isEmpty()) {
            Glide.with(mContext).load(notificationModel.getImage()).apply(new RequestOptions().centerCrop()).into(viewHolder.binding.userImageIV);
        }

        viewHolder.binding.titleTV.setText(notificationModel.getTitle());

        viewHolder.binding.messageTV.setText(notificationModel.getMessage());

        viewHolder.binding.timeTV.setText(convertFormat(String.valueOf(notificationModel.getTime())));

    }


    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        NotificationReadItemBinding binding;


        public ViewHolder(@NonNull NotificationReadItemBinding binding) {
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

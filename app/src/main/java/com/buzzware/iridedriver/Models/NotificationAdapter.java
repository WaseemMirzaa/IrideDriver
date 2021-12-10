package com.buzzware.iridedriver.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.iridedriver.databinding.NotificationReadItemBinding;

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

        viewHolder.binding.titleTV.setText(notificationModel.getTitle());

        viewHolder.binding.messageTV.setText(notificationModel.getMessage());

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

}

package com.buzzware.iridedriver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.buzzware.iridedriver.Models.MyRequests;
import com.buzzware.iridedriver.databinding.RequestItemDesginBinding;
import com.buzzware.iridedriver.interfaces.RequestCallback;

import java.util.List;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private List<MyRequests> list;

    private Context mContext;

    RequestCallback requestCallback;


    public RequestAdapter(Context mContext, List<MyRequests> list, RequestCallback requestCallback) {

        this.list = list;

        this.mContext = mContext;

        this.requestCallback=requestCallback;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(RequestItemDesginBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {


        MyRequests request=list.get(i);

        viewHolder.binding.nameTV.setText("Name: "+request.name);
        viewHolder.binding.subjectTV.setText("Subject: "+request.subject);
        viewHolder.binding.messageTV.setText("Message: "+request.message);

        viewHolder.binding.mainLL.setOnClickListener(v-> requestCallback.onItemClick(request.id,request.conversationId));


    }


    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RequestItemDesginBinding binding;


        public ViewHolder(@NonNull RequestItemDesginBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}

package com.buzzware.iridedriver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.iridedriver.Models.ConversationModel;
import com.buzzware.iridedriver.databinding.ChatItemLayBinding;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder>  {

    private List<ConversationModel> list;
    private Context context;
    OnClickListener listener;

    public ConversationAdapter(Context mContext, List<ConversationModel> list, OnClickListener listener) {
        this.list = list;
        this.context = mContext;
        this.listener= listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ChatItemLayBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final ConversationModel conversationModel= list.get(i);

        viewHolder.binding.userNameTV.setText(conversationModel.getName());

        viewHolder.binding.lastMessageTV.setText(conversationModel.getLastMessage());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(conversationModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ChatItemLayBinding binding;

        public ViewHolder(@NonNull ChatItemLayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public interface OnClickListener{
        void onClick(ConversationModel conversationModel);
    }
}

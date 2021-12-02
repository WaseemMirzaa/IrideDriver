package com.buzzware.iridedriver.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.iridedriver.Fragments.MessagesFragment;
import com.buzzware.iridedriver.Models.ChatModel;
import com.buzzware.iridedriver.R;

import java.util.List;

public class ChatAddapter extends RecyclerView.Adapter<ChatAddapter.imageViewHolder>{
    public Context context;
    List<ChatModel> messageModels;

    public ChatAddapter(Context context, List<ChatModel> messageModels) {
        this.context = context;
        this.messageModels = messageModels;
    }

    @Override
    public imageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_lay, parent, false);
        return new imageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final imageViewHolder holder, final int position) {
        final ChatModel messageModel= messageModels.get(position);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).addToBackStack("messages").commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class imageViewHolder extends RecyclerView.ViewHolder {

        View view;

        public imageViewHolder(View itemView) {
            super(itemView);
            view= itemView;
        }
    }
}

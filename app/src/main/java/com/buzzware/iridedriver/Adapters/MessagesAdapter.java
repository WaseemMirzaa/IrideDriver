package com.buzzware.iridedriver.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.iridedriver.Models.MessageModel;
import com.buzzware.iridedriver.databinding.MessagesItemLayBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>  {

    private List<MessageModel> list;
    private Context mContext;
    String  userId;

    public MessagesAdapter(Context mContext, List<MessageModel> list, String currentUserId) {
        this.list = list;
        this.mContext = mContext;
        userId= currentUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(MessagesItemLayBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final MessageModel messageModel= list.get(i);
        if(messageModel.getFromID().equals(userId))
        {
            /////my mesage layout
            viewHolder.binding.othersMessageLay.getRoot().setVisibility(View.GONE);
            viewHolder.binding.myMessageLay.getRoot().setVisibility(View.VISIBLE);
            if(messageModel.getType().equals("text"))
            {
                viewHolder.binding.myMessageLay.messageTV.setText(messageModel.getContent());
                viewHolder.binding.myMessageLay.timeTV.setText(convertFormat(String.valueOf(messageModel.getTimestamp())));
            }
        }else{
            ///othe rmessage layout
            viewHolder.binding.myMessageLay.getRoot().setVisibility(View.GONE);
            viewHolder.binding.othersMessageLay.getRoot().setVisibility(View.VISIBLE);
            if(messageModel.getType().equals("text"))
            {
                viewHolder.binding.othersMessageLay.messageTV.setText(messageModel.getContent());
                viewHolder.binding.othersMessageLay.timeTV.setText(convertFormat(String.valueOf(messageModel.getTimestamp())));
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        MessagesItemLayBinding binding;

        public ViewHolder(@NonNull MessagesItemLayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public int RandomColors()
    {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;
    }

    public static String convertFormat(String inputDate) {
        Date date = null;
        date = new Date(Long.parseLong(inputDate));

        if (date == null) {
            return "";
        }

        SimpleDateFormat convetDateFormat = new SimpleDateFormat("hh:mm a");

        return convetDateFormat.format(date);
    }

    public interface OnItemClick{
        void OnClick(String url, String time);
    }
}

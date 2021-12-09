package com.buzzware.iridedriver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.iridedriver.databinding.PromotionItemDesginBinding;

import java.util.List;


public class PromotionsAdapter extends RecyclerView.Adapter<PromotionsAdapter.ViewHolder> {

    private List<String> list;

    private Context mContext;


    public PromotionsAdapter(Context mContext, List<String> list) {

        this.list = list;

        this.mContext = mContext;

    }

    @NonNull
    @Override
    public PromotionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(PromotionItemDesginBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {


    }


    @Override
    public int getItemCount() {

        return 6;
       // return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        PromotionItemDesginBinding binding;


        public ViewHolder(@NonNull PromotionItemDesginBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}

package com.buzzware.iridedriver.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iridedriver.Models.RideModel;
import com.buzzware.iridedriver.Models.UploadImageModel;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.OnTrip;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class VehicleImagesAdapter extends RecyclerView.Adapter<VehicleImagesAdapter.VehicleImageHolder> {

    public Context context;

    public ArrayList<UploadImageModel> list;

    OnImageClickListener listener;

    public VehicleImagesAdapter(Context context, ArrayList<UploadImageModel> images,OnImageClickListener listener) {

        this.list = images;

        this.context = context;

        this.listener = listener;

    }

    @Override
    public VehicleImagesAdapter.VehicleImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle_image,
                parent,
                false);

        return new VehicleImagesAdapter.VehicleImageHolder(view);
    }

    @Override
    public void onBindViewHolder(final VehicleImagesAdapter.VehicleImageHolder holder, final int position) {

        UploadImageModel uploadImageModel = list.get(position);

        if (uploadImageModel.url != null) {

            Glide.with(context).load(uploadImageModel.url).apply(new RequestOptions().centerCrop()).into(holder.picCIV);

            holder.placeHolderIV.setVisibility(View.GONE);

        }

        holder.titleTV.setText(uploadImageModel.title);

        holder.view.setOnClickListener(v -> listener.onImageTapped(uploadImageModel));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VehicleImageHolder extends RecyclerView.ViewHolder {

        public View view;

        public ImageView picCIV;

        public ImageView placeHolderIV;

        public TextView titleTV;

        public VehicleImageHolder(View itemView) {

            super(itemView);

            view = itemView;

            initView(view);
        }

        private void initView(View itemView) {

            picCIV = (ImageView) itemView.findViewById(R.id.picCIV);

            placeHolderIV = (ImageView) itemView.findViewById(R.id.placeHolderIV);

            titleTV = (TextView) itemView.findViewById(R.id.titleTV);
        }
    }

    public interface OnImageClickListener {

        void onImageTapped(UploadImageModel imageModel);

    }
}

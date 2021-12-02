package com.buzzware.iridedriver.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class RideModel implements Parcelable {

    public String id;

    public String userId;

    public String driverId;

    public String vehicleId;

    public long bookingDate;

    public String status;

    public String price;

    public AddressModel pickUp;

    public AddressModel destination;

    public RideModel() {

    }

    public RideModel(String userId, String driverId, String vehicleId, long bookingDate, Double driverLat, Double driverLng, String status) {
        this.userId = userId;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    protected RideModel(Parcel in) {
        id = in.readString();
        userId = in.readString();
        driverId = in.readString();
        vehicleId = in.readString();
        bookingDate = in.readLong();
        status = in.readString();
        price = in.readString();
        pickUp = in.readParcelable(AddressModel.class.getClassLoader());
        destination = in.readParcelable(AddressModel.class.getClassLoader());
    }

    public static final Creator<RideModel> CREATOR = new Creator<RideModel>() {
        @Override
        public RideModel createFromParcel(Parcel in) {
            return new RideModel(in);
        }

        @Override
        public RideModel[] newArray(int size) {
            return new RideModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(driverId);
        dest.writeString(vehicleId);
        dest.writeLong(bookingDate);
        dest.writeString(status);
        dest.writeString(price);
        dest.writeParcelable(pickUp, flags);
        dest.writeParcelable(destination, flags);
    }
}

package com.buzzware.iridedriver.Models.Payouts;

import android.os.Parcel;
import android.os.Parcelable;

import com.buzzware.iridedriver.Models.TripDetail;

public class RideWithPayoutModel implements Parcelable {

    public String id;

    public String userId;

    public String driverId;

    public String vehicleId;

    public long bookingDate;

    public Double driverLat;

    public Double driverLng;

    public String username;

    public String rideType;

    public TripDetail tripDetail;

    public String status;

    public String price;

    public PayoutObj payout;


    public RideWithPayoutModel() {

    }

    public RideWithPayoutModel(String userId, String driverId, String vehicleId, long bookingDate, Double driverLat, Double driverLng, String status) {
        this.userId = userId;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.bookingDate = bookingDate;
        this.driverLat = driverLat;
        this.driverLng = driverLng;
        this.status = status;
    }

    protected RideWithPayoutModel(Parcel in) {
        id = in.readString();
        userId = in.readString();
        driverId = in.readString();
        vehicleId = in.readString();
        bookingDate = in.readLong();
        if (in.readByte() == 0) {
            driverLat = null;
        } else {
            driverLat = in.readDouble();
        }
        if (in.readByte() == 0) {
            driverLng = null;
        } else {
            driverLng = in.readDouble();
        }
        username = in.readString();
        rideType = in.readString();
        tripDetail = in.readParcelable(TripDetail.class.getClassLoader());
        status = in.readString();
        price = in.readString();
    }

    public static final Creator<RideWithPayoutModel> CREATOR = new Creator<RideWithPayoutModel>() {
        @Override
        public RideWithPayoutModel createFromParcel(Parcel in) {
            return new RideWithPayoutModel(in);
        }

        @Override
        public RideWithPayoutModel[] newArray(int size) {
            return new RideWithPayoutModel[size];
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
        if (driverLat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(driverLat);
        }
        if (driverLng == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(driverLng);
        }
        dest.writeString(username);
        dest.writeString(rideType);
        dest.writeParcelable(tripDetail, flags);
        dest.writeString(status);
        dest.writeString(price);
    }
}

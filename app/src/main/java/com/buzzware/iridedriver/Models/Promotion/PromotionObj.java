package com.buzzware.iridedriver.Models.Promotion;

import android.os.Parcel;
import android.os.Parcelable;

import com.buzzware.iridedriver.Models.RideModel;

import java.util.List;

public class PromotionObj implements Parcelable {

    public String  amount;

    public String id;

    public long endTime;
    public long startTime;

    public int noOfTrips;

//    public int remainingRides;

    public String title;
    public String message;
    public String url;

    public List<RideModel> rideModels;

    public PromotionObj() {

    }

    protected PromotionObj(Parcel in) {
        amount = in.readString();
        id = in.readString();
        endTime = in.readLong();
        startTime = in.readLong();
        noOfTrips = in.readInt();
        title = in.readString();
        message = in.readString();
        rideModels = in.createTypedArrayList(RideModel.CREATOR);
    }

    public static final Creator<PromotionObj> CREATOR = new Creator<PromotionObj>() {
        @Override
        public PromotionObj createFromParcel(Parcel in) {
            return new PromotionObj(in);
        }

        @Override
        public PromotionObj[] newArray(int size) {
            return new PromotionObj[size];
        }
    };

    public double getAmountInDouble() {
        return Double.parseDouble(amount);
    }

    public String getId() {

        if (id == null)
            return "";

        return id;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getNoOfTrips() {
        return noOfTrips;
    }

//    public int getRemainingRides() {
//        return remainingRides;
//    }

    public String getTitle() {

        if(title == null)
            return "";

        return title;
    }

    public String getMessage() {

        if(message == null)
            return "";

        return message;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amount);
        dest.writeString(id);
        dest.writeLong(endTime);
        dest.writeLong(startTime);
        dest.writeInt(noOfTrips);
        dest.writeString(title);
        dest.writeString(message);
        dest.writeTypedList(rideModels);
    }
}
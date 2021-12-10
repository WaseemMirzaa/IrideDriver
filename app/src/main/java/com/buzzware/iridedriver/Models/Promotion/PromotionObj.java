package com.buzzware.iridedriver.Models.Promotion;

import com.buzzware.iridedriver.Models.RideModel;

import java.util.List;

public class PromotionObj {

    public double amount;

    public String id;

    public long endTime;
    public long startTime;

    public int noOfTrips;

    public int remainingRides;

    public String title;
    public String message;

    public List<RideModel> rideModels;

    public double getAmount() {
        return amount;
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

    public int getRemainingRides() {
        return remainingRides;
    }

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


}
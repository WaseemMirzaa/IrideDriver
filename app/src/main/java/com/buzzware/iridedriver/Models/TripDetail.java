package com.buzzware.iridedriver.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TripDetail implements Parcelable {

    public SearchedPlaceModel pickUp;

    public ArrayList<SearchedPlaceModel> destinations;

    public TripDetail() {

    }

    protected TripDetail(Parcel in) {
        pickUp = in.readParcelable(SearchedPlaceModel.class.getClassLoader());
        destinations = in.createTypedArrayList(SearchedPlaceModel.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(pickUp, flags);
        dest.writeTypedList(destinations);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TripDetail> CREATOR = new Creator<TripDetail>() {
        @Override
        public TripDetail createFromParcel(Parcel in) {
            return new TripDetail(in);
        }

        @Override
        public TripDetail[] newArray(int size) {
            return new TripDetail[size];
        }
    };
}

package com.buzzware.iridedriver.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class AddressModel implements Parcelable {

    public String id;

    public double lat;
    public double lng;

    public String name;
    public String address;

    public AddressModel() {

    }

    public AddressModel(Parcel in) {
        id = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        name = in.readString();
        address = in.readString();
    }

    public static final Creator<AddressModel> CREATOR = new Creator<AddressModel>() {
        @Override
        public AddressModel createFromParcel(Parcel in) {
            return new AddressModel(in);
        }

        @Override
        public AddressModel[] newArray(int size) {
            return new AddressModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(name);
        dest.writeString(address);
    }
}

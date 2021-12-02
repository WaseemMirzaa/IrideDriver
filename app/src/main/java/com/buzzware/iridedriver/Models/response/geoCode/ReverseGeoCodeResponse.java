package com.buzzware.iridedriver.Models.response.geoCode;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReverseGeoCodeResponse {

    public PlusCode plus_code;

    @SerializedName("results")
    public List<ReverseGeoCode> results;

    public String status;
}

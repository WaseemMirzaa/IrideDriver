package com.buzzware.iridedriver.Models.Stripe;

import com.google.gson.annotations.SerializedName;

public class UrlResponse {

    public int success;

    @SerializedName("transferinfo")
    public String url;
}

package com.buzzware.iridedriver.Models;

import com.google.gson.annotations.SerializedName;

public class Reminder {

    @SerializedName("r_date")
    public String r_date;

    @SerializedName("r_v_id")
    public String r_v_id;

    @SerializedName("r_message")
    public String message;

    public String id;
}

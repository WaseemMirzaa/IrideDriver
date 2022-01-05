package com.buzzware.iridedriver.retrofit;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface Api {

    @FormUrlEncoded
    @POST()
    Call<String> getPlaces(@Url String url, @Field("email")String ema);

    @POST("/widgets/stripelink")
    Call<String> getStripeLink(@Body RequestBody params);
}

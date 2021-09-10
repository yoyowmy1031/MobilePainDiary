package com.example.mobilepaindiary.weatherAPI;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitInterface {
    // get the weather data for Hong Kong
    @GET("weather?q=HongKong&appid=2ce706aa0302fb3d327a74283a0ee631")
    Call<Response> getWeatherData();
}

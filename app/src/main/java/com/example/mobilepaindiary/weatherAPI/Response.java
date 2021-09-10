package com.example.mobilepaindiary.weatherAPI;

import android.location.Geocoder;

import com.google.gson.annotations.SerializedName;

public class Response {
    @SerializedName("main")
    private Weather weather;
    private Geocoder geocoder;


    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

}

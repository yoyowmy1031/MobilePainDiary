package com.example.mobilepaindiary.weatherAPI;

import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("temp")
    String temperature;

    @SerializedName("humidity")
    String humidity;

    @SerializedName("pressure")
    String pressure;


    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temp) {
        this.temperature = temp;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }
}

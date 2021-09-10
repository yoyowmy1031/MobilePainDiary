package com.example.mobilepaindiary.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
public class PainRecord {
    @PrimaryKey(autoGenerate = true)
    public int eid;

    @ColumnInfo(name = "entry_date")
    @NonNull
    public String entryDate;

    @ColumnInfo(name = "user_email")
    @NonNull
    public String userEmail;

    @ColumnInfo(name = "pain_intensity")
    @NonNull
    public int painIntensity;

    @ColumnInfo(name = "pain_location")
    @NonNull
    public String painLocation;

    @ColumnInfo(name = "mood")
    @NonNull
    public String mood;

    @ColumnInfo(name = "goal_step")
    @NonNull
    public int goalStep;

    @ColumnInfo(name = "actual_step")
    @NonNull
    public int actualStep;

    @ColumnInfo(name = "temperature")
    @NonNull
    public double temperature;

    @ColumnInfo(name = "humidity")
    @NonNull
    public int humidity;

    @ColumnInfo(name = "pressure")
    @NonNull
    public int pressure;

    public PainRecord( @NonNull String userEmail,
                      @NonNull String entryDate, @NonNull int painIntensity,
                      @NonNull String painLocation,
                      @NonNull String mood, @NonNull int goalStep,
                      @NonNull int actualStep, @NonNull Double temperature,
                      @NonNull Integer humidity, @NonNull Integer pressure) {
        this.entryDate = entryDate;
        this.userEmail = userEmail;
        this.painIntensity = painIntensity;
        this.painLocation = painLocation;
        this.mood = mood;
        this.actualStep = actualStep;
        this.goalStep = goalStep;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
    }

    public int getEid() {
        return eid;
    }
    public String getDate() {
        return entryDate;
    }
    public int getPainIntensity() {
        return painIntensity;
    }
    public String getPainLocation() {
        return painLocation;
    }
    public String getMood() {
        return mood;
    }
    public int getActualStep() {
        return actualStep;
    }
    public int getGoalStep() {
        return goalStep;
    }
    public double getTemperature() { return temperature; }
    public int getHumidity() { return humidity; }
    public int getPressure() { return pressure; }
    public String getUserEmail(){return userEmail; }

}

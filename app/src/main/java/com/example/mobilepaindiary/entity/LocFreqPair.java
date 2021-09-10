package com.example.mobilepaindiary.entity;

public class LocFreqPair {
    private String painLocation;
    private int count;


    public LocFreqPair(String painLocation, int count){
        this.painLocation = painLocation;
        this.count = count;
    }
    public String getLocation(){
        return painLocation;
    }
    public int getCount(){
        return count;
    }
}

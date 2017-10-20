package com.example.android.quakereport;

import static com.example.android.quakereport.R.id.date;

/**
 * Created by thara on 10/12/17.
 */

public class EarthQuake {
    private double magnitude;
    private String place;
    private long timeinMilliSecs;
    private String URL;
    public EarthQuake( double mag, String loc, long dt){
        magnitude = mag;
        place = loc;
        timeinMilliSecs = dt;
    }
    public EarthQuake( double mag, String loc, long dt, String url){
        magnitude = mag;
        place = loc;
        timeinMilliSecs = dt;
        URL = url;
    }
    public double getMagnitude(){
        return magnitude;
    }
    public String getPlace(){
        return place;
    }
    public long getTimeinMilliSecs(){
        return timeinMilliSecs;
    }
    public String getURL(){
        return URL;
    }
    public String toString(){
        return "Word{ " +
                "Magnitude: "+magnitude+" " +
                "Place: "+place+
                " Date: "+date+
                "URL: "+URL+"}";
    }
}

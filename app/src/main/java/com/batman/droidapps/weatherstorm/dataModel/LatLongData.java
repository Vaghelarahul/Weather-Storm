package com.batman.droidapps.weatherstorm.dataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LatLongData {

    @SerializedName("Latitude")
    @Expose
    private Double Latitude;

    @SerializedName("Longitude")
    @Expose
    private Double Longitude;

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    private String getLatLong(){

        String latLong = Latitude + "," + Longitude;
        return latLong;
    }

}

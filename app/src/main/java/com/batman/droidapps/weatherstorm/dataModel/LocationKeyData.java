package com.batman.droidapps.weatherstorm.dataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationKeyData {


    @SerializedName("Key")
    @Expose
    private String locationKey;

    @SerializedName("GeoPosition")
    @Expose
    private LatLongData GeoPosition;


    public String getLocationKey() {
        return locationKey;
    }

    public void setLocationKey(String locationKey) {
        this.locationKey = locationKey;
    }

    public LatLongData getGeoPosition() {
        return GeoPosition;
    }

    public void setGeoPosition(LatLongData geoPosition) {
        GeoPosition = geoPosition;
    }
}

package com.batman.droidapps.weatherstorm.dataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DirectionData{

    @SerializedName("Degrees")
    @Expose
    private long Degrees;

    @SerializedName("Localized")
    @Expose
    private String Localized;

    @SerializedName("English")
    @Expose
    private String English;

    public long getDegrees() {
        return Degrees;
    }

    public void setDegrees(long degrees) {
        Degrees = degrees;
    }

    public String getLocalized() {
        return Localized;
    }

    public void setLocalized(String localized) {
        Localized = localized;
    }

    public String getEnglish() {
        return English;
    }

    public void setEnglish(String english) {
        English = english;
    }

}
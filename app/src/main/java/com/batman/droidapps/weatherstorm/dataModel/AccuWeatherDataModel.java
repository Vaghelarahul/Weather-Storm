package com.batman.droidapps.weatherstorm.dataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AccuWeatherDataModel {

    @SerializedName("DailyForecasts")
    @Expose
    private ArrayList<DailyForcastData> DailyForecasts;

    public ArrayList<DailyForcastData> getDailyForecasts() {
        return DailyForecasts;
    }

    public void setDailyForecasts(ArrayList<DailyForcastData> dailyForecasts) {
        DailyForecasts = dailyForecasts;
    }
}

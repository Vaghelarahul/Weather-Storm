package com.batman.droidapps.weatherstorm.dataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DailyForcastData {


    @SerializedName("Date")
    @Expose
    private String Date;

    @SerializedName("EpochDate")
    @Expose
    private Long EpochDate;

    @SerializedName("Temperature")
    @Expose
    private TempratureData Temperature;

    @SerializedName("Day")
    @Expose
    private DayData Day;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Long getEpochDate() {
        return EpochDate;
    }

    public void setEpochDate(Long epochDate) {
        EpochDate = epochDate;
    }

    public TempratureData getTemperature() {
        return Temperature;
    }

    public void setTemperature(TempratureData temperature) {
        Temperature = temperature;
    }

    public DayData getDay() {
        return Day;
    }

    public void setDay(DayData day) {
        Day = day;
    }


}

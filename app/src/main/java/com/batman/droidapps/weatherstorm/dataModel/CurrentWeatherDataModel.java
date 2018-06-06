package com.batman.droidapps.weatherstorm.dataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentWeatherDataModel {


    @SerializedName("LocalObservationDateTime")
    @Expose
    private String CurrentDateTime;

    @SerializedName("WeatherText")
    @Expose
    private String WeatherText;

    @SerializedName("WeatherIcon")
    @Expose
    private int WeatherIcon;

    @SerializedName("Temperature")
    @Expose
    private TempData Temperature;

    @SerializedName("RelativeHumidity")
    @Expose
    private float RelativeHumidity;

    @SerializedName("Wind")
    @Expose
    private CurentWindData Wind;

    @SerializedName("Pressure")
    @Expose
    private TempData Pressure;

    @SerializedName("CloudCover")
    @Expose
    private int CloudCover;

    @SerializedName("Visibility")
    @Expose
    private TempData Visibility;

    public TempData getVisibility() {
        return Visibility;
    }

    public void setVisibility(TempData visibility) {
        Visibility = visibility;
    }

    public int getCloudCover() {
        return CloudCover;
    }

    public void setCloudCover(int cloudCover) {
        CloudCover = cloudCover;
    }

    public String getCurrentDateTime() {
        return CurrentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        CurrentDateTime = currentDateTime;
    }

    public String getWeatherText() {
        return WeatherText;
    }

    public void setWeatherText(String weatherText) {
        WeatherText = weatherText;
    }

    public int getWeatherIcon() {
        return WeatherIcon;
    }

    public void setWeatherIcon(int weatherIcon) {
        WeatherIcon = weatherIcon;
    }

    public TempData getTemperature() {
        return Temperature;
    }

    public void setTemperature(TempData temperature) {
        Temperature = temperature;
    }

    public float getRelativeHumidity() {
        return RelativeHumidity;
    }

    public void setRelativeHumidity(float relativeHumidity) {
        RelativeHumidity = relativeHumidity;
    }

    public CurentWindData getWind() {
        return Wind;
    }

    public void setWind(CurentWindData wind) {
        Wind = wind;
    }

    public TempData getPressure() {
        return Pressure;
    }

    public void setPressure(TempData pressure) {
        Pressure = pressure;
    }
}

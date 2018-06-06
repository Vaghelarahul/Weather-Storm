package com.batman.droidapps.weatherstorm.dataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DayData {

    @SerializedName("Icon")
    @Expose
    private Integer Icon;

    @SerializedName("IconPhrase")
    @Expose
    private String IconPhrase;

    @SerializedName("ShortPhrase")
    @Expose
    private String ShortPhrase;

    @SerializedName("Wind")
    @Expose
    private WindData Wind;

    @SerializedName("CloudCover")
    @Expose
    private int CloudCover;

    public int getCloudCover() {
        return CloudCover;
    }

    public void setCloudCover(int cloudCover) {
        CloudCover = cloudCover;
    }

    public Integer getIcon() {
        return Icon;
    }

    public void setIcon(Integer icon) {
        Icon = icon;
    }

    public String getIconPhrase() {
        return IconPhrase;
    }

    public void setIconPhrase(String iconPhrase) {
        IconPhrase = iconPhrase;
    }

    public String getShortPhrase() {
        return ShortPhrase;
    }

    public void setShortPhrase(String shortPhrase) {
        ShortPhrase = shortPhrase;
    }

    public WindData getWind() {
        return Wind;
    }

    public void setWind(WindData wind) {
        Wind = wind;
    }
}

package com.batman.droidapps.weatherstorm.dataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TempData {

    @SerializedName("Metric")
    @Expose
    private TempAndSpeedValueUnitData Metric;

    public TempAndSpeedValueUnitData getMetric() {
        return Metric;
    }

    public void setMetric(TempAndSpeedValueUnitData metric) {
        Metric = metric;
    }
}

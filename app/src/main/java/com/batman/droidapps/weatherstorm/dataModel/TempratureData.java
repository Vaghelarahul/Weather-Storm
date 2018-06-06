package com.batman.droidapps.weatherstorm.dataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TempratureData {

    @SerializedName("Minimum")
    @Expose
    private TempAndSpeedValueUnitData Minimum;

    @SerializedName("Maximum")
    @Expose
    private TempAndSpeedValueUnitData Maximum;

    public TempAndSpeedValueUnitData getMinimum() {
        return Minimum;
    }

    public void setMinimum(TempAndSpeedValueUnitData minimum) {
        Minimum = minimum;
    }

    public TempAndSpeedValueUnitData getMaximum() {
        return Maximum;
    }

    public void setMaximum(TempAndSpeedValueUnitData maximum) {
        Maximum = maximum;
    }

}

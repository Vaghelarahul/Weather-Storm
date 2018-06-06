package com.batman.droidapps.weatherstorm.dataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TempAndSpeedValueUnitData {

    @SerializedName("Value")
    @Expose
    private float Value;

    @SerializedName("Unit")
    @Expose
    private String Unit;

    public float getValue() {
        return Value;
    }

    public void setValue(float value) {
        Value = value;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

}

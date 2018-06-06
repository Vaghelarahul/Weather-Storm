package com.batman.droidapps.weatherstorm.dataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WindData{

    @SerializedName("Speed")
    @Expose
    private TempAndSpeedValueUnitData Speed;

    @SerializedName("Direction")
    @Expose
    private DirectionData Direction;

    public TempAndSpeedValueUnitData getSpeed() {
        return Speed;
    }

    public void setSpeed(TempAndSpeedValueUnitData speed) {
        Speed = speed;
    }

    public DirectionData getDirection() {
        return Direction;
    }

    public void setDirection(DirectionData direction) {
        Direction = direction;
    }
}

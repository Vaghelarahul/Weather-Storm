package com.batman.droidapps.weatherstorm.dataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurentWindData {

    @SerializedName("Speed")
    @Expose
    private TempData Speed;

    @SerializedName("Direction")
    @Expose
    private DirectionData Direction;

    public TempData  getSpeed() {
        return Speed;
    }

    public void setSpeed(TempData  speed) {
        Speed = speed;
    }

    public DirectionData getDirection() {
        return Direction;
    }

    public void setDirection(DirectionData direction) {
        Direction = direction;
    }


}

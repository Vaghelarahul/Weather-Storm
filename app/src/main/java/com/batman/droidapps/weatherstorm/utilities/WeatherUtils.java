
package com.batman.droidapps.weatherstorm.utilities;

import android.content.Context;
import android.util.Log;

import com.batman.droidapps.weatherstorm.R;
import com.batman.droidapps.weatherstorm.data.WeatherPreferences;

public final class WeatherUtils {

    private static final String LOG_TAG = WeatherUtils.class.getSimpleName();

    private static double celsiusToFahrenheit(double temperatureInCelsius) {
        double temperatureInFahrenheit = (temperatureInCelsius * 1.8) + 32;
        return temperatureInFahrenheit;
    }

    public static String formatTemperature(Context context, double temperature) {
        if (!WeatherPreferences.isMetric(context)) {
            temperature = celsiusToFahrenheit(temperature);
        }

        int temperatureFormatResourceId = R.string.format_temperature;
        return String.format(context.getString(temperatureFormatResourceId), temperature);
    }

    public static String getFormattedWind(Context context, float windSpeed, float degrees) {
        int windFormat = R.string.format_wind_kmh;

        if (!WeatherPreferences.isMetric(context)) {
            windFormat = R.string.format_wind_mph;
            windSpeed = .621371192237334f * windSpeed;
        }

        String direction = "Unknown";
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = "N";
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = "NE";
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = "E";
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = "SE";
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = "S";
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = "SW";
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = "W";
        } else if (degrees >= 292.5 && degrees < 337.5) {
            direction = "NW";
        }

        return String.format(context.getString(windFormat), windSpeed, direction);
    }


    public static int getIconResourceIdForWeatherCondition(int weatherId) {


        if (weatherId == 1) {
            return R.drawable.ic_1_weather_icon;
        }else if (weatherId == 2){
            return R.drawable.ic_2_weather_icon;
        }else if (weatherId == 3){
            return R.drawable.ic_3_weather_icon;
        } else if (weatherId == 4){
            return R.drawable.ic_4_weather_icon;
        } else if (weatherId == 5){
            return R.drawable.ic_5_weather_icon;
        }else if (weatherId == 6){
            return R.drawable.ic_6_weather_icon;
        }else if (weatherId == 7){
            return R.drawable.ic_7_weather_icon;
        }else if (weatherId == 8){
            return R.drawable.ic_8_weather_icon;
        }else if (weatherId == 11){
            return R.drawable.ic_11_weather_icon;
        }else if (weatherId == 12){
            return R.drawable.ic_12_weather_icon;
        }else if (weatherId == 13){
            return R.drawable.ic_13_weather_icon;
        }else if (weatherId == 14){
            return R.drawable.ic_14_weather_icon;
        }else if (weatherId == 15){
            return R.drawable.ic_15_weather_icon;
        }else if (weatherId == 16){
            return R.drawable.ic_16_weather_icon;
        }else if (weatherId == 17){
            return R.drawable.ic_17_weather_icon;
        }else if (weatherId == 18){
            return R.drawable.ic_18_weather_icon;
        }else if (weatherId == 19){
            return R.drawable.ic_19_weather_icon;
        }else if (weatherId == 20){
            return R.drawable.ic_20_weather_icon;
        }else if (weatherId == 21){
            return R.drawable.ic_21_weather_icon;
        }else if (weatherId == 22){
            return R.drawable.ic_22_weather_icon;
        }else if (weatherId == 23){
            return R.drawable.ic_23_weather_icon;
        }else if (weatherId == 24){
            return R.drawable.ic_24_weather_icon;
        }else if (weatherId == 25){
            return R.drawable.ic_25_weather_icon;
        }else if (weatherId == 26){
            return R.drawable.ic_26_weather_icon;
        }else if (weatherId == 29){
            return R.drawable.ic_29_weather_icon;
        }else if (weatherId == 30){
            return R.drawable.ic_30_weather_icon;
        }else if (weatherId == 31){
            return R.drawable.ic_31_weather_icon;
        }else if (weatherId == 32){
            return R.drawable.ic_32_weather_icon;
        }else if (weatherId == 33){
            return R.drawable.ic_33_weather_icon;
        }else if (weatherId == 34){
            return R.drawable.ic_34_weather_icon;
        }else if (weatherId == 35){
            return R.drawable.ic_35_weather_icon;
        }else if (weatherId == 36){
            return R.drawable.ic_36_weather_icon;
        }else if (weatherId == 37){
            return R.drawable.ic_37_weather_icon;
        }else if (weatherId == 38){
            return R.drawable.ic_38_weather_icon;
        }else if (weatherId == 39){
            return R.drawable.ic_39_weather_icon;
        }else if (weatherId == 40){
            return R.drawable.ic_40_weather_icon;
        }else if (weatherId == 41){
            return R.drawable.ic_41_weather_icon;
        }else if (weatherId == 42){
            return R.drawable.ic_42_weather_icon;
        }else if (weatherId == 43){
            return R.drawable.ic_43_weather_icon;
        }else if (weatherId == 44){
            return R.drawable.ic_44_weather_icon;
        }

        Log.e(LOG_TAG, "Unknown Weather: " + weatherId);
        return R.drawable.ic_21_weather_icon;
    }
}

package com.batman.droidapps.weatherstorm.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.batman.droidapps.weatherstorm.utilities.DateUtils;


public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.batman.droidapps.weatherstorm";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WEATHER = "weather";

    public static final class WeatherEntryTable implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER)
                .build();

        public static final String TABLE_NAME = "weather";

        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_DATE_MILLIS = "dateInMillis";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_WEATHER_DESCRIPTION = "weatherDescription";
        public static final String COLUMN_WEATHER_DESCRIPTION_DETAIL = "weatherDescriptionDetails";
        public static final String COLUMN_AVG_TEMP = "avgTemprature";

        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_HUMIDITY = "humidity";

        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_WIND_SPEED_DIRECTION = "windSpeedDirection";

        public static final String COLUMN_DEGREES = "degrees";

        public static final String COLUMN_VISIBILITY= "visibility";
        public static final String COLUMN_CLOUD_COVER = "cloudCover";

        public static Uri buildWeatherUriWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(date))
                    .build();
        }

        public static String getDataSelectForToday() {
            long normalizedUtcNow = DateUtils.normalizeDate(System.currentTimeMillis());
            return WeatherEntryTable.COLUMN_DATE + " >= " + normalizedUtcNow;
        }
    }
}
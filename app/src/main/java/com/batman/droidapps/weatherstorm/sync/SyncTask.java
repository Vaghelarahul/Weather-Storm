package com.batman.droidapps.weatherstorm.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;

import com.batman.droidapps.weatherstorm.data.WeatherPreferences;
import com.batman.droidapps.weatherstorm.data.WeatherContract;
import com.batman.droidapps.weatherstorm.utilities.NetworkUtils;
import com.batman.droidapps.weatherstorm.utilities.NotificationUtils;

public class SyncTask {

    private static boolean isEmpty = false;

    synchronized public static void syncWeather(Context context) {

        try {

            ContentValues[] weatherValues = NetworkUtils.getWeatherData(context);

            Log.e("TAG", "weatherValuesLength: " + weatherValues.length);

            if (weatherValues != null && weatherValues.length != 0) {

                ContentResolver sunshineContentResolver = context.getContentResolver();

                sunshineContentResolver.delete(
                        WeatherContract.WeatherEntryTable.CONTENT_URI,
                        null,
                        null);

                sunshineContentResolver.bulkInsert(
                        WeatherContract.WeatherEntryTable.CONTENT_URI,
                        weatherValues);


                syncCurrentWeather(context);


                boolean notificationsEnabled = WeatherPreferences.areNotificationsEnabled(context);

                long timeSinceLastNotification = WeatherPreferences
                        .getEllapsedTimeSinceLastNotification(context);

                boolean oneDayPassedSinceLastNotification = false;

                if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                    oneDayPassedSinceLastNotification = true;
                }


                if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                    NotificationUtils.notifyUserOfNewWeather(context);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    synchronized public static void updateCurrentWeatherData(final Context context){

        Uri forecastQueryUri = WeatherContract.WeatherEntryTable.CONTENT_URI;

        String[] projectionColumns = {
                WeatherContract.WeatherEntryTable._ID,
                WeatherContract.WeatherEntryTable.COLUMN_DATE,
                WeatherContract.WeatherEntryTable.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntryTable.COLUMN_MAX_TEMP
        };

        String selectionStatement = WeatherContract.WeatherEntryTable
                .getDataSelectForToday();

        Cursor cursor = context.getContentResolver().query(
                forecastQueryUri,
                projectionColumns,
                selectionStatement,
                null,
                null);

        if (null != cursor || cursor.getCount() != 0) {

            ContentValues[] weatherValues = NetworkUtils.getCurrentWeatherData(context);

            if (weatherValues != null && weatherValues.length != 0) {

                String currentDate = weatherValues[0].getAsString(WeatherContract.WeatherEntryTable.COLUMN_DATE);
                if (cursor.moveToFirst()) {
                    do {

                        String DateString = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntryTable.COLUMN_DATE));

                        if (DateString.equals(currentDate)) {

                            double minTemp = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntryTable.COLUMN_MIN_TEMP));
                            double maxTemp = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntryTable.COLUMN_MAX_TEMP));

                            double avgTemp = weatherValues[0].getAsDouble(WeatherContract.WeatherEntryTable.COLUMN_AVG_TEMP);

                            if (avgTemp < minTemp) {
                                weatherValues[0].put(WeatherContract.WeatherEntryTable.COLUMN_MIN_TEMP, avgTemp);
                            }

                            if (avgTemp > maxTemp) {
                                weatherValues[0].put(WeatherContract.WeatherEntryTable.COLUMN_MAX_TEMP, avgTemp);
                            }
                        }

                    } while (cursor.moveToNext());
                }


                ContentResolver sunshineContentResolver = context.getContentResolver();

                sunshineContentResolver.update(WeatherContract.WeatherEntryTable.CONTENT_URI,
                        weatherValues[0],
                        WeatherContract.WeatherEntryTable.COLUMN_DATE,
                        new String[]{weatherValues[0].getAsString(WeatherContract.WeatherEntryTable.COLUMN_DATE)});
            }

        }

        if (cursor != null) {
            cursor.close();
        }


    }

    synchronized public static void syncCurrentWeather(final Context context) {

        Thread checkForEmpty = new Thread(new Runnable() {

            @Override
            public void run() {
                updateCurrentWeatherData(context);
            }
        });

        checkForEmpty.start();
    }

}
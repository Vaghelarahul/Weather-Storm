package com.batman.droidapps.weatherstorm.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.batman.droidapps.weatherstorm.R;
import com.batman.droidapps.weatherstorm.WeatherActivity;
import com.batman.droidapps.weatherstorm.data.WeatherPreferences;
import com.batman.droidapps.weatherstorm.data.WeatherContract;

public class NotificationUtils {


    public static final String[] WEATHER_NOTIFICATION_PROJECTION = {
            WeatherContract.WeatherEntryTable.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntryTable.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntryTable.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntryTable.COLUMN_WEATHER_DESCRIPTION,
    };

    public static final int INDEX_WEATHER_ID = 0;
    public static final int INDEX_MAX_TEMP = 1;
    public static final int INDEX_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_DESCRIPTION = 3;

    private static final int WEATHER_NOTIFICATION_ID = 3004;

    public static void notifyUserOfNewWeather(Context context) {

        Uri todaysWeatherUri = WeatherContract.WeatherEntryTable
                .buildWeatherUriWithDate(DateUtils.normalizeDate(System.currentTimeMillis()));

        Cursor todayWeatherCursor = context.getContentResolver().query(
                todaysWeatherUri,
                WEATHER_NOTIFICATION_PROJECTION,
                null,
                null,
                null);

        if (todayWeatherCursor.moveToFirst()) {

            int weatherId = todayWeatherCursor.getInt(INDEX_WEATHER_ID);
            String shortDescription = todayWeatherCursor.getString(INDEX_WEATHER_DESCRIPTION);
            double high = todayWeatherCursor.getDouble(INDEX_MAX_TEMP);
            double low = todayWeatherCursor.getDouble(INDEX_MIN_TEMP);

            Resources resources = context.getResources();
            int iconResourceId = WeatherUtils
                    .getIconResourceIdForWeatherCondition(weatherId);

            Bitmap largeIcon = BitmapFactory.decodeResource(
                    resources,
                    iconResourceId);

            String notificationTitle = context.getString(R.string.app_name);

            String notificationText = getNotificationText(context, shortDescription, weatherId, high, low);


            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                    .setSmallIcon(iconResourceId)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setAutoCancel(true);

            Intent detailIntentForToday = new Intent(context, WeatherActivity.class);
            detailIntentForToday.setData(todaysWeatherUri);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday);
            PendingIntent resultPendingIntent = taskStackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setContentIntent(resultPendingIntent);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(WEATHER_NOTIFICATION_ID, notificationBuilder.build());

            WeatherPreferences.saveLastNotificationTime(context, System.currentTimeMillis());
        }

        todayWeatherCursor.close();
    }

    private static String getNotificationText(Context context, String shortDescription,
                                              int weatherId, double high, double low) {

        String notificationFormat = context.getString(R.string.format_notification);

        String notificationText = String.format(notificationFormat,
                " " + shortDescription,
                WeatherUtils.formatTemperature(context, high),
                WeatherUtils.formatTemperature(context, low));

        return notificationText;
    }
}

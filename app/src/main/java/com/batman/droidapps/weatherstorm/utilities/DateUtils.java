
package com.batman.droidapps.weatherstorm.utilities;

import android.content.Context;
import android.util.Log;

import com.batman.droidapps.weatherstorm.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public final class DateUtils {

    public static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);

    public static long getNormalizedUtcDateForToday() {

        long utcNowMillis = System.currentTimeMillis();

        TimeZone currentTimeZone = TimeZone.getDefault();

        long gmtOffsetMillis = currentTimeZone.getOffset(utcNowMillis);

        long timeSinceEpochLocalTimeMillis = utcNowMillis + gmtOffsetMillis;

        long daysSinceEpochLocal = TimeUnit.MILLISECONDS.toDays(timeSinceEpochLocalTimeMillis);

        long normalizedUtcMidnightMillis = TimeUnit.DAYS.toMillis(daysSinceEpochLocal);

        return normalizedUtcMidnightMillis;
    }

    private static long elapsedDaysSinceEpoch(long utcDate) {
        return TimeUnit.MILLISECONDS.toDays(utcDate);
    }


    public static long normalizeDate(long date) {
        long daysSinceEpoch = elapsedDaysSinceEpoch(date);
        long millisFromEpochToTodayAtMidnightUtc = daysSinceEpoch * DAY_IN_MILLIS;
        return millisFromEpochToTodayAtMidnightUtc;
    }

    public static boolean isDateNormalized(long millisSinceEpoch) {

        boolean isDateNormalized = false;
        if (millisSinceEpoch % DAY_IN_MILLIS == 0) {
            isDateNormalized = true;
        }

        return isDateNormalized;
    }

    public static String getHumanReadableTime(Context context, long dateInMillis){

        String readableTime = "";

        Date weatherDate = new Date(dateInMillis);

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

        readableTime = formatter.format(weatherDate);
        return readableTime;
    }


    public static String getHumanReadableDate(Context context, long dateInMillis){

        String readableDate = "";

        Date weatherDate = new Date(dateInMillis);
        Date currentDate = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat formattedDateFormatter = new SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault());
        SimpleDateFormat formattedDateTomorrow = new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault());
        SimpleDateFormat formattedDateDayName = new SimpleDateFormat("EEEE", Locale.getDefault());

       String weatherStringDate = formatter.format(weatherDate);
       String currentStringDate = formatter.format(currentDate);

        try {
            weatherDate = formatter.parse(weatherStringDate);
            currentDate = formatter.parse(currentStringDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        long timeDiff = weatherDate.getTime() - currentDate.getTime();
        long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

        Log.e("TAG", "daysDiff: " + daysDiff + ",  " + weatherDate);

        if (weatherDate.equals(currentDate)){
            readableDate = formattedDateTomorrow.format(weatherDate);
            readableDate = "Today\n" + readableDate;

        } else if (daysDiff == 1){
            readableDate = "Tomorrow";

        } else {
            readableDate = formattedDateDayName.format(weatherDate);
        }


        return readableDate;
    }

}
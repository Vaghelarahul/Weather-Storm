
package com.batman.droidapps.weatherstorm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "weatherDatabase.db";

    private static final int DATABASE_VERSION = 3;

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + WeatherContract.WeatherEntryTable.TABLE_NAME + " (" +

                WeatherContract.WeatherEntryTable._ID                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                WeatherContract.WeatherEntryTable.COLUMN_DATE        + " TEXT NOT NULL, "                    +
                WeatherContract.WeatherEntryTable.COLUMN_DATE_MILLIS + " INTEGER NOT NULL, "                 +

                WeatherContract.WeatherEntryTable.COLUMN_WEATHER_ID  + " INTEGER NOT NULL,"                  +
                WeatherContract.WeatherEntryTable.COLUMN_WEATHER_DESCRIPTION  + " TEXT,"                  +
                WeatherContract.WeatherEntryTable.COLUMN_WEATHER_DESCRIPTION_DETAIL  + " TEXT,"                  +

                WeatherContract.WeatherEntryTable.COLUMN_AVG_TEMP    + " REAL NOT NULL, "                    +
                WeatherContract.WeatherEntryTable.COLUMN_MIN_TEMP    + " REAL NOT NULL, "                    +
                WeatherContract.WeatherEntryTable.COLUMN_MAX_TEMP    + " REAL NOT NULL, "                    +

                WeatherContract.WeatherEntryTable.COLUMN_HUMIDITY    + " REAL NOT NULL, "                    +
                WeatherContract.WeatherEntryTable.COLUMN_PRESSURE    + " REAL NOT NULL, "                    +

                WeatherContract.WeatherEntryTable.COLUMN_WIND_SPEED  + " REAL NOT NULL, "                    +
                WeatherContract.WeatherEntryTable.COLUMN_WIND_SPEED_DIRECTION  + " TEXT, "                    +
                WeatherContract.WeatherEntryTable.COLUMN_DEGREES     + " REAL NOT NULL, "                    +

                WeatherContract.WeatherEntryTable.COLUMN_VISIBILITY     + " REAL NOT NULL, "                 +
                WeatherContract.WeatherEntryTable.COLUMN_CLOUD_COVER     + " INTEGER NOT NULL, "             +


                " UNIQUE (" + WeatherContract.WeatherEntryTable.COLUMN_DATE + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntryTable.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
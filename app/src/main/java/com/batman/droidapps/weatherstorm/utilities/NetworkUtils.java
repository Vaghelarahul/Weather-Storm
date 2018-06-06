package com.batman.droidapps.weatherstorm.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.batman.droidapps.weatherstorm.data.WeatherPreferences;
import com.batman.droidapps.weatherstorm.data.WeatherContract;
import com.batman.droidapps.weatherstorm.dataModel.AccuWeatherDataModel;
import com.batman.droidapps.weatherstorm.dataModel.CurrentWeatherDataModel;
import com.batman.droidapps.weatherstorm.dataModel.DailyForcastData;
import com.batman.droidapps.weatherstorm.dataModel.LocationKeyData;
import com.batman.droidapps.weatherstorm.interfaces.RetrofitInterface;
import com.batman.droidapps.weatherstorm.sync.SyncUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

//    dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey=KQPPBirkiawDGNN59h98SYiXVe6iDMG5&q=28.714631,77.142991
    private static final String LOCATION_KEY_BY_LATLONG_URL = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/";
    private static final String LOCATION_KEY_BY_CITY_URL = "http://dataservice.accuweather.com/locations/v1/cities/";

//    http://dataservice.accuweather.com/forecasts/v1/daily/5day/3351925?apikey=O7ooQlVnH1KMvfGpfqxBpF5WeDacPhPL&details=true
    private static final String FORECAST_WEATHER_URL = "http://dataservice.accuweather.com/forecasts/v1/daily/";

//    http://dataservice.accuweather.com/currentconditions/v1/3351925?apikey=O7ooQlVnH1KMvfGpfqxBpF5WeDacPhPL&details=true
    private static final String CURRENT_WEATHER_URL = "http://dataservice.accuweather.com/currentconditions/";


    ////////////////////////////////////////////////////////////

    private static final String ACCU_WEATHER_API_KEY = "your_accuweather_API_key_here";
    private static final boolean ACCU_WEATHER_DETAILS = true;
    private static final boolean ACCU_WEATHER_IS_METRIC = true;


    public static void getLocationKey(Context context) {
        if (WeatherPreferences.isLocationLatLonAvailable(context)) {
            double[] preferredCoordinates = WeatherPreferences.getLocationCoordinates(context);
            double latitude = preferredCoordinates[0];
            double longitude = preferredCoordinates[1];

            String locationQuery = latitude + "," + longitude;
            getLocationKeyByLatLong(context, locationQuery, LOCATION_KEY_BY_LATLONG_URL);

        } else {
            String locationQuery = WeatherPreferences.getPreferredWeatherLocation(context);
            getLocationKeyForCity(context, locationQuery, LOCATION_KEY_BY_CITY_URL);
        }
    }

    public static ContentValues[] getWeatherData(Context context) {

        String locationKey = WeatherPreferences.getStoredLocationKey(context);
        return getWeatherDataFromServer(locationKey, FORECAST_WEATHER_URL);
    }

    public static ContentValues[] getCurrentWeatherData(Context context) {
        String locationKey = WeatherPreferences.getStoredLocationKey(context);
        return getWeatherCurrentCondition(locationKey, CURRENT_WEATHER_URL);
    }


    private static RetrofitInterface getRetrofitClient(String baseUrl) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RetrofitInterface.class);
    }


    private static ContentValues[] getWeatherDataFromServer(String mLocationKey, String baseUrl) {

        ContentValues[] weatherContentValues = null;

        RetrofitInterface retrofitInterface = getRetrofitClient(baseUrl);

        Call<AccuWeatherDataModel> weatherCall = retrofitInterface.getWeatherData(mLocationKey,
                ACCU_WEATHER_API_KEY, ACCU_WEATHER_DETAILS, ACCU_WEATHER_IS_METRIC);

        Log.d("TAG", "RetrofitUrl: " + weatherCall.request().url());

        Response<AccuWeatherDataModel> response;
        try {
            response = weatherCall.execute();

            if (response.isSuccessful() && response.body() != null) {

                AccuWeatherDataModel weatherData = response.body();

                if (weatherData != null) {

                    ArrayList<DailyForcastData> DailyForecastList = weatherData.getDailyForecasts();

                    if (DailyForecastList != null) {
                        Log.e("TAG", "weatherDataSize: " + DailyForecastList.size());

                        weatherContentValues = new ContentValues[DailyForecastList.size()];
                        long normalizedUtcStartDay = DateUtils.getNormalizedUtcDateForToday();

                        for (int index = 0; index < DailyForecastList.size(); index++) {

                            long dateTimeMillis = normalizedUtcStartDay + DateUtils.DAY_IN_MILLIS * index;

                            Date weatherDate = new Date(dateTimeMillis);

                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                            String stringDate = formatter.format(weatherDate);

                            String windDirectionString = DailyForecastList.get(index).getDay().getWind().getDirection().getLocalized();
                            String weatherDescription = DailyForecastList.get(index).getDay().getIconPhrase();
                            String weatherDescriptionDetailed = DailyForecastList.get(index).getDay().getShortPhrase();

                            double windSpeed = DailyForecastList.get(index).getDay().getWind().getSpeed().getValue();
                            double windDirection = DailyForecastList.get(index).getDay().getWind().getDirection().getDegrees();

                            double high = DailyForecastList.get(index).getTemperature().getMaximum().getValue();
                            double low = DailyForecastList.get(index).getTemperature().getMinimum().getValue();

                            int weatherId = DailyForecastList.get(index).getDay().getIcon();

                            int cloudCover = DailyForecastList.get(index).getDay().getCloudCover();

                            ContentValues weatherValues = new ContentValues();
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_DATE, stringDate);
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_DATE_MILLIS, dateTimeMillis);
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_HUMIDITY, 0);
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_PRESSURE, 0);
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_WIND_SPEED, windSpeed);
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_WIND_SPEED_DIRECTION, windDirectionString);
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_DEGREES, windDirection);
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_AVG_TEMP, 0);
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_MAX_TEMP, high);
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_MIN_TEMP, low);
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_WEATHER_ID, weatherId);
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_WEATHER_DESCRIPTION, weatherDescription);
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_WEATHER_DESCRIPTION_DETAIL, weatherDescriptionDetailed);
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_VISIBILITY, 0);
                            weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_CLOUD_COVER, cloudCover);

                            weatherContentValues[index] = weatherValues;
                        }
                    }
                }

            } else {
                Log.e("TAG", "Data Fetch Failed: " + response.errorBody().toString());
                Log.e("TAG", "Data Fetch Failed: " + response.message());
            }

        } catch (IOException e) {
            Log.e("TAG", "IOException");
            e.printStackTrace();
        }


        return weatherContentValues;
    }

    private static ContentValues[] getWeatherCurrentCondition(String mLocationKey, String baseUrl) {

        ContentValues[] weatherContentValues = null;

        RetrofitInterface retrofitInterface = getRetrofitClient(baseUrl);

        Call<ArrayList<CurrentWeatherDataModel>> currentWeathereatherCall = retrofitInterface.getCurrentCondition(mLocationKey,
                ACCU_WEATHER_API_KEY, ACCU_WEATHER_DETAILS);

        Log.d("TAG", "RetrofitUrl: " + currentWeathereatherCall.request().url());

        Response<ArrayList<CurrentWeatherDataModel>> response;
        try {
            response = currentWeathereatherCall.execute();

            if (response.isSuccessful() && response.body() != null) {

                ArrayList<CurrentWeatherDataModel> currentWeatherData = response.body();

                if (currentWeatherData != null) {

                    Log.e("TAG", "weatherDataSize: " + currentWeatherData.size());

                    weatherContentValues = new ContentValues[currentWeatherData.size()];

                    for (int index = 0; index < currentWeatherData.size(); index++) {


                        String stringDate = currentWeatherData.get(index).getCurrentDateTime();
                        if (stringDate != null && !stringDate.equals("null")) {
                            stringDate = stringDate.substring(0, 10);
                        }

                        Log.e("TAG", "stringDate: " + stringDate);

                        float pressure = currentWeatherData.get(index).getPressure().getMetric().getValue();
                        float humidity = currentWeatherData.get(index).getRelativeHumidity();

                        String weatherDescription = currentWeatherData.get(index).getWeatherText();

                        double windSpeed = currentWeatherData.get(index).getWind().getSpeed().getMetric().getValue();
                        double windDirection = currentWeatherData.get(index).getWind().getDirection().getDegrees();
                        String windDirectionString = currentWeatherData.get(index).getWind().getDirection().getLocalized();

                        double currentAverageTemp = currentWeatherData.get(index).getTemperature().getMetric().getValue();

                        int weatherId = currentWeatherData.get(index).getWeatherIcon();

                        double visibility = currentWeatherData.get(index).getVisibility().getMetric().getValue();
                        int cloudCover = currentWeatherData.get(index).getCloudCover();

                        ContentValues weatherValues = new ContentValues();
                        weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_DATE, stringDate);
                        weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_AVG_TEMP, currentAverageTemp);
                        weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_HUMIDITY, humidity);
                        weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_PRESSURE, pressure);
                        weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_WIND_SPEED, windSpeed);
                        weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_WIND_SPEED_DIRECTION, windDirectionString);
                        weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_DEGREES, windDirection);
                        weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_WEATHER_ID, weatherId);
                        weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_WEATHER_DESCRIPTION, weatherDescription);
                        weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_VISIBILITY, visibility);
                        weatherValues.put(WeatherContract.WeatherEntryTable.COLUMN_CLOUD_COVER, cloudCover);

                        weatherContentValues[index] = weatherValues;

                        Log.e("TAG", "Extras: " + humidity);
                        Log.e("TAG", "Extras: " + windSpeed);
                        Log.e("TAG", "Extras: " + pressure);

                    }

                }

            } else {

                Log.e("TAG", "Data Fetch Failed: " + response.errorBody().toString());
                Log.e("TAG", "Data Fetch Failed: " + response.message());
            }

        } catch (IOException e) {
            Log.e("TAG", "IOException");
            e.printStackTrace();
        }

        return weatherContentValues;
    }


    public static void getLocationKeyByLatLong(final Context context, String query, String baseUrl) {

        RetrofitInterface retrofitInterface =  getRetrofitClient(baseUrl);

        Call<LocationKeyData> locationKeyCall = retrofitInterface.getLocationKeyForLatLong(ACCU_WEATHER_API_KEY, query);

        Log.d("TAG", "LocationKey_URl: " + locationKeyCall.request().url());
        locationKeyCall.enqueue(new Callback<LocationKeyData>() {
            @Override
            public void onResponse(Call<LocationKeyData> call, Response<LocationKeyData> response) {

                if (response != null && response.body() != null) {

                        LocationKeyData locationKeyData = response.body();
                        String locationKey = locationKeyData.getLocationKey();

                        Log.e(TAG, "locationKey: " + locationKey);

                        double latitude = locationKeyData.getGeoPosition().getLatitude();
                        double longitude = locationKeyData.getGeoPosition().getLongitude();

                        WeatherPreferences.setLocationKey(context, locationKey);
                        SyncUtils.startImmediateSync(context);

                } else {
                    Log.e(TAG, "Daily limit over");

                }
            }

            @Override
            public void onFailure(Call<LocationKeyData> call, Throwable t) {

                if (t != null) {
                    Log.e("TAG", "onFailure: " + t.getMessage());
                    Log.e(TAG, "Daily limit over");
                }
            }
        });

    }


    public static void getLocationKeyForCity(final Context context, String query, String baseUrl) {

        RetrofitInterface retrofitInterface =  getRetrofitClient(baseUrl);

        Call<ArrayList<LocationKeyData>> locationKeyCall = retrofitInterface.getLocationKey(ACCU_WEATHER_API_KEY, query);

        Log.d("TAG", "LocationKey_URl: " + locationKeyCall.request().url());
        locationKeyCall.enqueue(new Callback<ArrayList<LocationKeyData>>() {
            @Override
            public void onResponse(Call<ArrayList<LocationKeyData>> call, Response<ArrayList<LocationKeyData>> response) {

                if (response != null && response.body() != null) {

                    if (response.body().size() != 0) {

                        LocationKeyData locationKeyData = response.body().get(0);
                        String locationKey = locationKeyData.getLocationKey();

                        double latitude = locationKeyData.getGeoPosition().getLatitude();
                        double longitude = locationKeyData.getGeoPosition().getLongitude();

//                        WeatherPreferences.setLocationDetails(context, latitude, longitude);
                        WeatherPreferences.setLocationKey(context, locationKey);

                        SyncUtils.startImmediateSync(context);

                    } else {
                        Toast.makeText(context, "No weather data found for this location", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "Location not found. Please provide more accurate location", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<LocationKeyData>> call, Throwable t) {

                if (t != null) {
                    Log.e("TAG", "onFailure: " + t.getMessage());
                    Toast.makeText(context, "Location not found. Please provide more accurate location", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
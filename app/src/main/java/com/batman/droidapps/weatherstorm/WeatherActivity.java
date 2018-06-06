package com.batman.droidapps.weatherstorm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.batman.droidapps.weatherstorm.data.WeatherContract;
import com.batman.droidapps.weatherstorm.data.WeatherPreferences;
import com.batman.droidapps.weatherstorm.sync.SyncTask;
import com.batman.droidapps.weatherstorm.sync.SyncUtils;
import com.batman.droidapps.weatherstorm.utilities.DateUtils;
import com.batman.droidapps.weatherstorm.utilities.NetworkUtils;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, WeatherForecastAdapter.ForecastAdapterOnClickHandler {

    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntryTable.COLUMN_DATE_MILLIS,
            WeatherContract.WeatherEntryTable.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntryTable.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntryTable.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntryTable.COLUMN_WEATHER_DESCRIPTION,
            WeatherContract.WeatherEntryTable.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntryTable.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntryTable.COLUMN_DEGREES,
            WeatherContract.WeatherEntryTable.COLUMN_PRESSURE,
            WeatherContract.WeatherEntryTable.COLUMN_AVG_TEMP,
            WeatherContract.WeatherEntryTable.COLUMN_VISIBILITY,
            WeatherContract.WeatherEntryTable.COLUMN_CLOUD_COVER
    };

    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;
    public static final int INDEX_WEATHER_DESCRIPTION = 4;
    public static final int INDEX_HUMIDITY = 5;
    public static final int INDEX_WIND_SPEED = 6;
    public static final int INDEX_WIND_SPEED_DEGREE = 7;
    public static final int INDEX_PRESSURE = 8;
    public static final int INDEX_AVG_TEMP = 9;
    public static final int INDEX_VISIBILITY = 10;
    public static final int INDEX_CLOUD_COVER = 11;
    private static final int ID_FORECAST_LOADER = 44;
    private static final int LOC_REQ_CODE = 23;
    private static final int LOC_SETTING_CODE = 32;

    private final String TAG = WeatherActivity.class.getSimpleName();
    private WeatherForecastAdapter mWeatherForecastAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private ProgressBar mLoadingIndicator;

    private FusedLocationProviderClient mFusedLocationClient;
    private Context mContext;
    private TextView mWeatherLocationLabel;

    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        mContext = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_custom_toolbar);
        setSupportActionBar(toolbar);


        mWeatherLocationLabel = (TextView) findViewById(R.id.tv_weather_location);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mWeatherForecastAdapter = new WeatherForecastAdapter(this, this);
        mRecyclerView.setAdapter(mWeatherForecastAdapter);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setNumUpdates(1)
                .setInterval(1000)
                .setExpirationDuration(10000)
                .setFastestInterval(2000);

        showLoading();

        getSupportLoaderManager().initLoader(ID_FORECAST_LOADER, null, this);

        if (WeatherPreferences.getPreferredWeatherLocation(mContext).equals("")) {
            requestLocationPermission();

        } else {
            long timeSinceLastNotification = WeatherPreferences
                    .getEllapsedTimeSinceLastNotification(this);

            if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {

                if (WeatherPreferences.isLocationKeyAvailable(mContext)) {
                    SyncUtils.initialize(this);
                } else {
                    NetworkUtils.getLocationKey(mContext);
                }
            }
        }


        if (WeatherPreferences.isLocationKeyAvailable(mContext)) {
            SyncTask.syncCurrentWeather(this);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        displayLocationInAppBar();
    }

    synchronized private void displayLocationInAppBar() {
        String location = WeatherPreferences.getPreferredWeatherLocation(mContext);
        mWeatherLocationLabel.setText(location);
    }


    private void registerLocationUpdatesRequest() {

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) return;
                Location location = locationResult.getLastLocation();

                Log.e("TAG", "location: " + location.getLatitude() + ", " + location.getLongitude());
                updateLocationToUser(location);
                super.onLocationResult(locationResult);
            }
        };


        if (!checkLocationPermission()) return;
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }


    private void unRegisterLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    private void updateLocationToUser(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        WeatherPreferences.setLocationDetails(mContext, latitude, longitude);

        NetworkUtils.getLocationKey(mContext);

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (addresses != null) {

            String address_1 = addresses.get(0).getAdminArea();
            String address_2 = addresses.get(0).getCountryName();

            String address = address_1 + ", " + address_2;
            Log.e("TAG", "locality: " + address);

            WeatherPreferences.setPreferredWeatherLocation(mContext, address);
            displayLocationInAppBar();

            unRegisterLocationUpdates();

        } else {
            Log.e("TAG", "location: " + "addresses Null");


        }
    }

    private boolean checkLocationPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOC_REQ_CODE);

            return false;
        }
        return true;
    }

    private void requestLocationPermission() {

        if (!checkLocationPermission()) return;
        setLocationSettings();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOC_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult");
                setLocationSettings();

            } else {
                Toast.makeText(mContext, "Location permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setLocationSettings() {

        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);

        Task<LocationSettingsResponse> settingsTask = settingsClient.checkLocationSettings(builder.build());
        settingsTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.e(TAG, "OnSuccess");
                registerLocationUpdatesRequest();
            }
        });

        settingsTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e instanceof ResolvableApiException) {

                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(WeatherActivity.this, LOC_SETTING_CODE);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                        Log.e(TAG, "Message : " + e1.getMessage());
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOC_SETTING_CODE && resultCode == RESULT_OK) {
            Log.e(TAG, "onActivityResult");
            registerLocationUpdatesRequest();
        }
    }

    private void openPreferredLocationInMap() {
        double[] coords = WeatherPreferences.getLocationCoordinates(this);
        String posLat = Double.toString(coords[0]);
        String posLong = Double.toString(coords[1]);
        Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {


        switch (loaderId) {

            case ID_FORECAST_LOADER:

                Uri forecastQueryUri = WeatherContract.WeatherEntryTable.CONTENT_URI;
                String sortOrder = WeatherContract.WeatherEntryTable.COLUMN_DATE + " ASC";
                String selection = WeatherContract.WeatherEntryTable.getDataSelectForToday();

                return new CursorLoader(this,
                        forecastQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.e(TAG, "CursorSize: " + data.getCount());

        mWeatherForecastAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showWeatherDataView();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mWeatherForecastAdapter.swapCursor(null);
    }


    @Override
    public void onClick(long date) {

//        Intent weatherDetailIntent = new Intent(WeatherActivity.this, DetailActivity.class);
//        Uri uriForDateClicked = WeatherContract.WeatherEntryTable.buildWeatherUriWithDate(date);
//        weatherDetailIntent.setData(uriForDateClicked);
//        startActivity(weatherDetailIntent);

    }

    private void showWeatherDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forecast_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

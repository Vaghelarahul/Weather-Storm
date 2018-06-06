package com.batman.droidapps.weatherstorm;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

import com.batman.droidapps.weatherstorm.data.WeatherPreferences;
import com.batman.droidapps.weatherstorm.data.WeatherContract;
import com.batman.droidapps.weatherstorm.utilities.NetworkUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    private static final int PLACE_REQ_CODE = 41;
    private final String TAG = SettingsActivity.class.getSimpleName();

    private Preference mPreference;

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {

            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }
    }


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preference_layout_);

        Preference locPreference = (Preference) findPreference(getString(R.string.pref_location_key));

        locPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                mPreference = preference;
                getPlacePicker();

                return true;
            }
        });


        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }

    }

    public void getPlacePicker() {

        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

        try {
            Intent intent = intentBuilder.build(getActivity());
            startActivityForResult(intent, PLACE_REQ_CODE);

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "latLong: " + "onActivityResult");

        if (resultCode == RESULT_OK && requestCode == PLACE_REQ_CODE) {

            Place place = PlacePicker.getPlace(getActivity(), data);

            if (place == null) {
                Log.e(TAG, "latLong: " + "Null");
                return;
            }

            LatLng latLng = place.getLatLng();
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;

            String address = "";

            if (place.getAddress() != null) {
                address = place.getAddress().toString();
            }

            WeatherPreferences.setLocationDetails(getActivity(), latitude, longitude);

            SharedPreferences pref = getPreferenceScreen().getSharedPreferences();
            pref.edit().putString(mPreference.getKey(), address).apply();

            Log.e(TAG, "latLong: " + latitude + "," + longitude);
            Log.e(TAG, "latLong: " + address);


        } else {
            Log.e(TAG, "latLong: " + "Not Null");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Activity activity = getActivity();

        if (key.equals(getString(R.string.pref_location_key))) {

            NetworkUtils.getLocationKey(activity);

        } else if (key.equals(getString(R.string.pref_units_key))) {
            activity.getContentResolver().notifyChange(WeatherContract.WeatherEntryTable.CONTENT_URI, null);
        }

        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }
    }

}

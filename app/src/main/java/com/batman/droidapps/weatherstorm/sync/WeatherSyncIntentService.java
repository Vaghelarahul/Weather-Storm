
package com.batman.droidapps.weatherstorm.sync;

import android.app.IntentService;
import android.content.Intent;

public class WeatherSyncIntentService extends IntentService {

    public WeatherSyncIntentService() {
        super("WeatherSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SyncTask.syncWeather(this);
    }
}
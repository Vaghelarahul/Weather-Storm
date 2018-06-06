
package com.batman.droidapps.weatherstorm.sync;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class SyncUtils {


    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    private static final String SUNSHINE_SYNC_TAG = "sunshine-sync";

    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncSunshineJob = dispatcher.newJobBuilder()
                .setService(WeatherFirebaseJobService.class)
                .setTag(SUNSHINE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncSunshineJob);
    }

    synchronized public static void initialize(@NonNull final Context context) {

        if (sInitialized) return;

        sInitialized = true;

        scheduleFirebaseJobDispatcherSync(context);

         startImmediateSync(context);


//        Thread checkForEmpty = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//
//                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
//
//
//                String[] projectionColumns = {WeatherContract.WeatherEntry._ID};
//                String selectionStatement = WeatherContract.WeatherEntry
//                        .getDataSelectForToday();
//
//                Cursor cursor = context.getContentResolver().query(
//                        forecastQueryUri,
//                        projectionColumns,
//                        selectionStatement,
//                        null,
//                        null);
//
//                if (null == cursor || cursor.getCount() == 0) {
//                    startImmediateSync(context);
//                }
//
//                if (cursor != null){
//                    cursor.close();
//                }
//            }
//        });
//        checkForEmpty.start();


    }


    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, WeatherSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
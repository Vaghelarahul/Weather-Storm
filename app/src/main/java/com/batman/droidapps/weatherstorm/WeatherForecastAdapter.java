package com.batman.droidapps.weatherstorm;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.batman.droidapps.weatherstorm.utilities.DateUtils;
import com.batman.droidapps.weatherstorm.utilities.WeatherUtils;

import static com.batman.droidapps.weatherstorm.WeatherActivity.INDEX_AVG_TEMP;
import static com.batman.droidapps.weatherstorm.WeatherActivity.INDEX_CLOUD_COVER;
import static com.batman.droidapps.weatherstorm.WeatherActivity.INDEX_HUMIDITY;
import static com.batman.droidapps.weatherstorm.WeatherActivity.INDEX_PRESSURE;
import static com.batman.droidapps.weatherstorm.WeatherActivity.INDEX_VISIBILITY;
import static com.batman.droidapps.weatherstorm.WeatherActivity.INDEX_WEATHER_DESCRIPTION;
import static com.batman.droidapps.weatherstorm.WeatherActivity.INDEX_WIND_SPEED;
import static com.batman.droidapps.weatherstorm.WeatherActivity.INDEX_WIND_SPEED_DEGREE;


class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.ForecastAdapterViewHolder> {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    private final Context mContext;

    final private ForecastAdapterOnClickHandler mClickHandler;
    private boolean mUseTodayLayout;
    private Cursor mCursor;

    public WeatherForecastAdapter(@NonNull Context context, ForecastAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutId;

        switch (viewType) {

            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.today_forecast_list_item;
                break;
            }

            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.forecast_list_item;
                break;
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);

        view.setFocusable(true);

        return new ForecastAdapterViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);


        int weatherId = mCursor.getInt(WeatherActivity.INDEX_WEATHER_CONDITION_ID);
        int weatherImageId;
        long dateInMillis;

        int viewType = getItemViewType(position);

        dateInMillis = mCursor.getLong(WeatherActivity.INDEX_WEATHER_DATE);
        weatherImageId = WeatherUtils
                .getIconResourceIdForWeatherCondition(weatherId);

        switch (viewType) {

            case VIEW_TYPE_TODAY:

                float humidity = mCursor.getFloat(INDEX_HUMIDITY);
                float pressure = mCursor.getFloat(INDEX_PRESSURE);
                float windSpeed = mCursor.getFloat(INDEX_WIND_SPEED);
                float windDirection = mCursor.getFloat(INDEX_WIND_SPEED_DEGREE);
                double avgTemperatureDoubleValue = mCursor.getDouble(INDEX_AVG_TEMP);
                int visibility = mCursor.getInt(INDEX_VISIBILITY);

                String pressureString = mContext.getResources().getString(R.string.format_pressure, pressure);
                String humidityString = mContext.getResources().getString(R.string.format_humidity, humidity);
                String windString = WeatherUtils.getFormattedWind(mContext, windSpeed, windDirection);
                String currentTemp = WeatherUtils.formatTemperature(mContext, avgTemperatureDoubleValue);
                String dateString = DateUtils.getHumanReadableTime(mContext, dateInMillis);

                forecastAdapterViewHolder.mHumidity.setText(humidityString);
                forecastAdapterViewHolder.mWindSpeed.setText(windString);
                forecastAdapterViewHolder.mPressure.setText(String.valueOf(pressureString));
                forecastAdapterViewHolder.mAvgTemp.setText(String.valueOf(visibility + " km"));
                forecastAdapterViewHolder.mTempNow.setText(currentTemp);
//                forecastAdapterViewHolder.mTempNowUpdateTime.setText(dateString);

                break;

            case VIEW_TYPE_FUTURE_DAY:

                break;

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        forecastAdapterViewHolder.iconView.setImageResource(weatherImageId);

        String dateString = DateUtils.getHumanReadableDate(mContext, dateInMillis);

        forecastAdapterViewHolder.dateView.setText(dateString);

        String description = mCursor.getString(INDEX_WEATHER_DESCRIPTION);

        if (description != null){
            description = description.trim();
        }

        forecastAdapterViewHolder.descriptionView.setText(description);

        double highInCelsius = mCursor.getDouble(WeatherActivity.INDEX_WEATHER_MAX_TEMP);

        String highString = WeatherUtils.formatTemperature(mContext, highInCelsius);


        float cloudCover = mCursor.getFloat(INDEX_CLOUD_COVER);
        String cloudCoverString = mContext.getResources().getString(R.string.format_cloud_cover, cloudCover);

        forecastAdapterViewHolder.cloudCover.setText(cloudCoverString);

        forecastAdapterViewHolder.highTempView.setText(highString);

        double lowInCelsius = mCursor.getDouble(WeatherActivity.INDEX_WEATHER_MIN_TEMP);

        String lowString = WeatherUtils.formatTemperature(mContext, lowInCelsius);


        forecastAdapterViewHolder.lowTempView.setText(lowString);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (mUseTodayLayout && position == 0) {
            return VIEW_TYPE_TODAY;
        } else {
            return VIEW_TYPE_FUTURE_DAY;
        }
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public interface ForecastAdapterOnClickHandler {
        void onClick(long date);
    }

    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView iconView;

        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;
        final TextView cloudCover;

        TextView mHumidity = null;
        TextView mWindSpeed = null;
        TextView mPressure = null;
        TextView mAvgTemp = null;
        TextView mTempNow = null;
        TextView mTempNowUpdateTime = null;

        ForecastAdapterViewHolder(View view, int viewType) {
            super(view);

            if (viewType == VIEW_TYPE_TODAY) {

                mHumidity = (TextView) view.findViewById(R.id.textView_humidity);
                mWindSpeed = (TextView) view.findViewById(R.id.textView_wind_speed);
                mPressure = (TextView) view.findViewById(R.id.textView_pressure);
                mAvgTemp = (TextView) view.findViewById(R.id.textView_avg_temp);
                mTempNow = (TextView) view.findViewById(R.id.textView_temp_now);
                mTempNowUpdateTime = (TextView) view.findViewById(R.id.textView_now_update_time);
            }

            iconView = (ImageView) view.findViewById(R.id.weather_icon);
            dateView = (TextView) view.findViewById(R.id.date);
            descriptionView = (TextView) view.findViewById(R.id.weather_description);
            highTempView = (TextView) view.findViewById(R.id.high_temperature);
            lowTempView = (TextView) view.findViewById(R.id.low_temperature);
            cloudCover = (TextView) view.findViewById(R.id.textView_cloud_cover);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(WeatherActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);
        }
    }
}
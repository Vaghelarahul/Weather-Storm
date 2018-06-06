package com.batman.droidapps.weatherstorm.interfaces;

import com.batman.droidapps.weatherstorm.dataModel.AccuWeatherDataModel;
import com.batman.droidapps.weatherstorm.dataModel.CurrentWeatherDataModel;
import com.batman.droidapps.weatherstorm.dataModel.LocationKeyData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

//    @GET("forecasts/{path}")
//    Call<WeatherDataType_APREIS_API> getWeatherData(@Path("path") String path,
//                                                    @Query("client_id") String client_id,
//                                                    @Query("client_secret") String client_secret,
//                                             @Query("limit") Integer limit);

//    http://dataservice.accuweather.com/forecasts/v1/daily/5day/3351925?
// apikey=O7ooQlVnH1KMvfGpfqxBpF5WeDacPhPL&details=true&metric=true

    @GET("5day/{path}")
    Call<AccuWeatherDataModel> getWeatherData(@Path("path") String path,
                                              @Query("apikey") String apikey,
                                              @Query("details") boolean isDetailsRequested,
                                              @Query("metric") boolean isMetricData);


//    http://dataservice.accuweather.com/currentconditions/v1/3351925?apikey=O7ooQlVnH1KMvfGpfqxBpF5WeDacPhPL&details=true

    @GET("v1/{path}")
    Call<ArrayList<CurrentWeatherDataModel>> getCurrentCondition(@Path("path") String path,
                                                                 @Query("apikey") String apikey,
                                                                 @Query("details") boolean isDetailsRequested);

    //    dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey=KQPPBirkiawDGNN59h98SYiXVe6iDMG5&q=28.714631,77.142991
    //    dataservice.accuweather.com/locations/v1/search?apikey=KQPPBirkiawDGNN59h98SYiXVe6iDMG5&q=manali
    @GET("search")
    Call<ArrayList<LocationKeyData>> getLocationKey(@Query("apikey") String apikey,
                                                    @Query("q") String latLong);

    @GET("search")
    Call<LocationKeyData> getLocationKeyForLatLong(@Query("apikey") String apikey,
                                                    @Query("q") String latLong);


}

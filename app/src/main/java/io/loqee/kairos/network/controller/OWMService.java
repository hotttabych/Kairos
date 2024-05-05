package io.loqee.kairos.network.controller;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OWMService {
    @GET("data/2.5/weather")
    Call<JsonObject> getWeather(@Query("q") String q, @Query("appid") String appid, @Query("units") String units, @Query("lang") String lang);

    @GET("data/2.5/forecast")
    Call<JsonObject> getForecast(@Query("q") String q, @Query("appid") String appid, @Query("units") String units, @Query("lang") String lang);
}

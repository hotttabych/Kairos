package io.loqee.kairos.network.controller;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OSMService {
    @GET("reverse")
    Call<JsonObject> getGeoData(@Query("format") String format, @Query("lat") double lat, @Query("lon") double lon, @Query("accept-language") String accept_language);
}

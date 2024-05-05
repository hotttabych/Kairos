package io.loqee.kairos.network.controller;

import android.util.Log;

import io.loqee.kairos.base.GlobalVars;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OSMConnection {
    private static OSMConnection instance;
    private final Retrofit retrofit;

    private OSMConnection() {
        retrofit = new Retrofit.Builder()
                .baseUrl(GlobalVars.OSM_NOMINATIM_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static OSMConnection getInstance() {
        Log.d("Retrofit", "getInstance: Retrofit connection established");
        if (instance == null) {
            instance = new OSMConnection();
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}

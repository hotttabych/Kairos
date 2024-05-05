package io.loqee.kairos.network.controller;

import android.util.Log;

import io.loqee.kairos.base.GlobalVars;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OWMConnection {
    private static OWMConnection instance;
    private final Retrofit retrofit;

    private OWMConnection() {
        retrofit = new Retrofit.Builder()
                .baseUrl(GlobalVars.OWM_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static OWMConnection getInstance() {
        Log.d("Retrofit", "getInstance: Retrofit connection established");
        if (instance == null) {
            instance = new OWMConnection();
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}

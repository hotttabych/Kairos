package io.loqee.kairos.network.controller;

import android.util.Log;

import io.loqee.kairos.base.GlobalVars;

import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class HoroscopeConnection {
    private static HoroscopeConnection instance;
    private final Retrofit retrofit;

    private HoroscopeConnection() {
        retrofit = new Retrofit.Builder()
                .baseUrl(GlobalVars.HOROSCOPE_SERVER_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
    }

    public static HoroscopeConnection getInstance() {
        Log.d("Retrofit", "getInstance: Retrofit connection established");
        if (instance == null) {
            instance = new HoroscopeConnection();
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}

package io.loqee.kairos.network.controller;

import io.loqee.kairos.network.viewModel.horoscopes.Horoscope;

import retrofit2.Call;
import retrofit2.http.GET;

public interface HoroscopeService {
    @GET("r/export/utf/xml/daily/com.xml")
    Call<Horoscope> getHoroscope();
}

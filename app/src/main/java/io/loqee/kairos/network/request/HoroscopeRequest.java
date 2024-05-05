package io.loqee.kairos.network.request;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.loqee.kairos.network.viewModel.horoscopes.Horoscope;
import io.loqee.kairos.network.controller.HoroscopeConnection;
import io.loqee.kairos.network.controller.HoroscopeService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HoroscopeRequest {
    private final MutableLiveData<String> horoscopeData = new MutableLiveData<>();
    private final HoroscopeService horoscopeService;


    public HoroscopeRequest() {
        horoscopeService = HoroscopeConnection.getInstance().getRetrofit().create(HoroscopeService.class);
    }

    public LiveData<String> getHoroscope(String zodiacSign) {
        Call<Horoscope> call = horoscopeService.getHoroscope();
        call.enqueue(new Callback<Horoscope>() {
            @Override
            public void onResponse(@NonNull Call<Horoscope> call, @NonNull Response<Horoscope> response) {
                if (response.isSuccessful()) {
                    Horoscope horoscope = response.body();
                    String todayHoroscope = "";
                    if (horoscope != null) {
                        switch (zodiacSign) {
                            case "aries":
                                todayHoroscope = horoscope.getAries().getToday();
                                break;
                            case "taurus":
                                todayHoroscope = horoscope.getTaurus().getToday();
                                break;
                            case "gemini":
                                todayHoroscope = horoscope.getGemini().getToday();
                                break;
                            case "cancer":
                                todayHoroscope = horoscope.getCancer().getToday();
                                break;
                            case "leo":
                                todayHoroscope = horoscope.getLeo().getToday();
                                break;
                            case "virgo":
                                todayHoroscope = horoscope.getVirgo().getToday();
                                break;
                            case "libra":
                                todayHoroscope = horoscope.getLibra().getToday();
                                break;
                            case "scorpio":
                                todayHoroscope = horoscope.getScorpio().getToday();
                                break;
                            case "sagittarius":
                                todayHoroscope = horoscope.getSagittarius().getToday();
                                break;
                            case "capricorn":
                                todayHoroscope = horoscope.getCapricorn().getToday();
                                break;
                            case "aquarius":
                                todayHoroscope = horoscope.getAquarius().getToday();
                                break;
                            case "pisces":
                                todayHoroscope = horoscope.getPisces().getToday();
                                break;
                        }
                    }
                    horoscopeData.postValue(todayHoroscope);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Horoscope> call, @NonNull Throwable t) {
                Log.e(this.getClass().getSimpleName(), "Failed to get weather and temperature data. Error message: " + t.getMessage());
            }
        });
        return horoscopeData;
    }
}

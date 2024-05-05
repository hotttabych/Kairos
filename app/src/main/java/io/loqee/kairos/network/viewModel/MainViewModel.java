package io.loqee.kairos.network.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import io.loqee.kairos.network.viewModel.forecast.ForecastModel;
import io.loqee.kairos.network.request.HoroscopeRequest;
import io.loqee.kairos.network.request.OpenStreetMapRequest;
import io.loqee.kairos.network.request.WeatherRequest;
import io.loqee.kairos.network.viewModel.weather.WeatherModel;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final WeatherRequest weatherRequest;
    private final OpenStreetMapRequest openStreetMapRequest;
    private final HoroscopeRequest horoscopeRequest;

    public MainViewModel(@NonNull Application application) {
        super(application);
        weatherRequest = new WeatherRequest();
        openStreetMapRequest = new OpenStreetMapRequest();
        horoscopeRequest = new HoroscopeRequest();
    }

    public LiveData<WeatherModel> getWeather(String q, String units, String lang) {
        return weatherRequest.getWeather(q, units, lang);
    }

    public LiveData<List<ForecastModel>> getWeatherForecast(String q, String units, String lang) {
        return weatherRequest.getWeatherForecast(q, units, lang);
    }

    public LiveData<String> getGeoData(String format, double lat, double lon) {
        return openStreetMapRequest.getGeoData(format, lat, lon);
    }

    public LiveData<String> getHoroscope(String zodiacSign) {
        return horoscopeRequest.getHoroscope(zodiacSign);
    }
}

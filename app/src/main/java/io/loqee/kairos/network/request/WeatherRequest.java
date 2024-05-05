package io.loqee.kairos.network.request;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.loqee.kairos.base.BuildVars;
import io.loqee.kairos.network.viewModel.forecast.ForecastModel;
import io.loqee.kairos.network.controller.OWMConnection;
import io.loqee.kairos.network.controller.OWMService;
import io.loqee.kairos.network.viewModel.weather.WeatherModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRequest {
    private final MutableLiveData<List<ForecastModel>> forecastData = new MutableLiveData<>();
    private final MutableLiveData<WeatherModel> weatherModelData = new MutableLiveData<>();
    private final OWMService owmService;

    public WeatherRequest() {
        owmService = OWMConnection.getInstance().getRetrofit().create(OWMService.class);
    }

    public LiveData<WeatherModel> getWeather(String q, String units, String lang) {
        String key = BuildVars.OWM_APP_ID;
        Call<JsonObject> call = owmService.getWeather(q, key, units, lang);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        String weatherResult = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
                        if (!weatherResult.isEmpty()) {
                            String tempResult = jsonObject.getAsJsonObject("main").getAsJsonPrimitive("temp").getAsString();
                            String feelsLikeResult = jsonObject.getAsJsonObject("main").getAsJsonPrimitive("feels_like").getAsString();
                            String pressureResult = jsonObject.getAsJsonObject("main").getAsJsonPrimitive("pressure").getAsString();
                            String windSpeedResult = jsonObject.getAsJsonObject("wind").getAsJsonPrimitive("speed").getAsString();
                            String humidityResult = jsonObject.getAsJsonObject("main").getAsJsonPrimitive("humidity").getAsString();
                            int sunriseResult = jsonObject.getAsJsonObject("sys").getAsJsonPrimitive("sunrise").getAsInt();
                            int sunsetResult = jsonObject.getAsJsonObject("sys").getAsJsonPrimitive("sunset").getAsInt();
                            weatherModelData.postValue(new WeatherModel(weatherResult, tempResult, feelsLikeResult, pressureResult, windSpeedResult, humidityResult, sunriseResult, sunsetResult));
                        } else {
                            weatherModelData.postValue(null);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e(this.getClass().getSimpleName(), "Failed to get weather and temperature data. Error message: " + t.getMessage());
            }
        });
        return weatherModelData;
    }

    public LiveData<List<ForecastModel>> getWeatherForecast(String q, String units, String lang) {
        String key = BuildVars.OWM_APP_ID;
        Call<JsonObject> call = owmService.getForecast(q, key, units, lang);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        JsonArray forecastResult = jsonObject.getAsJsonArray("list");
                        List<ForecastModel> forecastUnits = new ArrayList<>();
                        for (int i = 0; i < forecastResult.size(); i++) {
                            JsonObject forecastObject = forecastResult.get(i).getAsJsonObject();
                            String dateTime = forecastObject.getAsJsonPrimitive("dt_txt").getAsString();
                            String weather = forecastObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
                            String temperature = forecastObject.getAsJsonObject("main").getAsJsonPrimitive("temp").getAsString();
                            String feelsLike = forecastObject.getAsJsonObject("main").getAsJsonPrimitive("feels_like").getAsString();
                            String pressure = forecastObject.getAsJsonObject("main").getAsJsonPrimitive("pressure").getAsString();
                            String windSpeed = forecastObject.getAsJsonObject("wind").getAsJsonPrimitive("speed").getAsString();
                            String humidity = forecastObject.getAsJsonObject("main").getAsJsonPrimitive("humidity").getAsString();
                            forecastUnits.add(new ForecastModel(i, dateTime, weather, temperature, feelsLike, pressure, windSpeed, humidity));
                        }
                        forecastData.postValue(forecastUnits);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e(this.getClass().getSimpleName(), "Failed to get weather forecast data. Error message: " + t.getMessage());
            }
        });
        return forecastData;
    }

    public LiveData<List<ForecastModel>> getForecast() {
        return forecastData;
    }
}

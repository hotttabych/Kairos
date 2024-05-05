package io.loqee.kairos.network.request;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import io.loqee.kairos.network.controller.OSMConnection;
import io.loqee.kairos.network.controller.OSMService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenStreetMapRequest {
    private final MutableLiveData<String> geoData = new MutableLiveData<>();
    private final OSMService osmService;
    private final String TAG = this.getClass().getSimpleName();

    public OpenStreetMapRequest() {
        osmService = OSMConnection.getInstance().getRetrofit().create(OSMService.class);
    }

    public LiveData<String> getGeoData(String format, double lat, double lon) {
        Call<JsonObject> call = osmService.getGeoData(format, lat, lon, "en");
        Log.i(TAG, "onResponse URL: " + call.request().url());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        JsonObject address = jsonObject.getAsJsonObject("address");
                        String city = address.get("city") != null ? address.get("city").getAsString() : "";
                        String town = address.get("town") != null ? address.get("town").getAsString() : "";
                        String village = address.get("village") != null ? address.get("village").getAsString() : "";
                        String state = address.get("state") != null ? address.get("state").getAsString() : "";
                        String addressResult = !city.isEmpty() ? city : !town.isEmpty() ? town : (!village.isEmpty() ? village : state);
                        Log.i(TAG, "onResponse: " + addressResult);
                        geoData.postValue(addressResult);
                    }
                } else {
                    Log.i(TAG, "onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e(this.getClass().getSimpleName(), "Failed to get geodata. Error message: " + t.getMessage());
            }
        });
        return geoData;
    }
}

package io.loqee.kairos.network.viewModel.forecast;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import io.loqee.kairos.R;
import io.loqee.kairos.base.GlobalVars;
import io.loqee.kairos.base.UnitsManager;

import java.util.List;

public class ForecastModelAdapter extends ArrayAdapter<ForecastModel> {
    private static final String UNITS_KEY = "units";

    public ForecastModelAdapter(@NonNull Context context, int resource, @NonNull List<ForecastModel> forecastModels) {
        super(context, resource, forecastModels);
    }

    @Override
    public long getItemId(int position) {
        ForecastModel forecastModel = getItem(position);
        assert forecastModel != null;
        return forecastModel.getId(); // Assuming getId() returns a unique ID for each BeyondModel
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.forecast_card, parent, false);
        }

        Context context = getContext();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        ForecastModel forecastModel = getItem(position);

        UnitsManager unitsManager = new UnitsManager(context, sharedPreferences);

        TextView dateTime = convertView.findViewById(R.id.date_time);
        TextView temperature = convertView.findViewById(R.id.temperature);
        TextView feelsLike = convertView.findViewById(R.id.feels_like);
        TextView weather = convertView.findViewById(R.id.weather);
        TextView windSpeed = convertView.findViewById(R.id.wind_speed_textview);
        TextView pressure = convertView.findViewById(R.id.pressure_textview);
        TextView humidity = convertView.findViewById(R.id.humidity_textview);

        if (forecastModel == null) return convertView;

        String temperatureText;
        if (sharedPreferences.getString(UNITS_KEY, GlobalVars.DEFAULT_UNITS).equals("metric")) {
            temperatureText = context.getString(R.string.celsius, forecastModel.getTemperature());
        } else {
            temperatureText = context.getString(R.string.fahrenheit, forecastModel.getTemperature());
        }

        String feelsLikeText;
        if (sharedPreferences.getString(UNITS_KEY, GlobalVars.DEFAULT_UNITS).equals("metric")) {
            feelsLikeText = context.getString(R.string.celsius, forecastModel.getFeelsLike());
        } else {
            feelsLikeText = context.getString(R.string.fahrenheit, forecastModel.getFeelsLike());
        }

        String windSpeedText;
        if (sharedPreferences.getString(UNITS_KEY, GlobalVars.DEFAULT_UNITS).equals("metric")) {
            windSpeedText = context.getString(R.string.meter_per_second, forecastModel.getWindSpeed());
        } else {
            windSpeedText = context.getString(R.string.mph, forecastModel.getWindSpeed());
        }

        String pressureText = unitsManager.getPressureString(Integer.parseInt(forecastModel.getPressure()));

        String humidityText = context.getString(R.string.percent, forecastModel.getHumidity());

        dateTime.setText(forecastModel.getDateTime());
        temperature.setText(temperatureText);
        feelsLike.setText(context.getString(R.string.feels_like, feelsLikeText));
        weather.setText(capitalizeFirstLetter(forecastModel.getWeather()));
        windSpeed.setText(windSpeedText);
        pressure.setText(pressureText);
        humidity.setText(humidityText);

        return convertView;
    }

    public static String capitalizeFirstLetter(String sentence) {
        if (sentence == null || sentence.isEmpty()) {
            return sentence;
        }
        return sentence.substring(0, 1).toUpperCase() + sentence.substring(1);
    }

}


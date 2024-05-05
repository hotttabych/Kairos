package io.loqee.kairos.ui.fragments;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import io.loqee.kairos.R;
import io.loqee.kairos.base.GlobalVars;
import io.loqee.kairos.base.UnitsManager;
import io.loqee.kairos.databinding.FragmentOverviewBinding;
import io.loqee.kairos.network.viewModel.MainViewModel;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Overview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Overview extends KairosFragment {
    private static final String CITY_KEY = "city";
    private static final String UNITS_KEY = "units";
    private FragmentOverviewBinding binding;
    private String lang;
    private MainViewModel mainViewModel;
    private SharedPreferences sharedPreferences;
    private final Handler handler = new Handler();
    private Runnable runnable;

    public Overview() {
        // Required empty public constructor
    }

    public static Overview newInstance() {
        return new Overview();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        mainViewModel = new MainViewModel((Application) requireContext().getApplicationContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentOverviewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        runnable = this::setupViews;

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            handler.post(runnable);
            binding.swipeRefreshLayout.setRefreshing(false);
        });
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.bubbles_color);

        handler.post(runnable);

        // Inflate the layout for this fragment
        return view;
    }

    private void setupViews() {
        updateTexts();
        String city = sharedPreferences.getString(CITY_KEY, GlobalVars.DEFAULT_CITY);
        String units = sharedPreferences.getString(UNITS_KEY, GlobalVars.DEFAULT_UNITS);
        observeWeatherAndTemperature(city, units);
    }

    private void observeWeatherAndTemperature(String city, String units) {
        mainViewModel.getWeather(city, units, lang).observe(this, weatherModel -> {
            if (weatherModel == null) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.incorrect_location), Toast.LENGTH_SHORT).show();
                return;
            }
            String tempText;
            if (sharedPreferences.getString(UNITS_KEY, GlobalVars.DEFAULT_UNITS).equals("metric")) {
                tempText = getString(R.string.celsius, weatherModel.getTemperature());
            } else {
                tempText = getString(R.string.fahrenheit, weatherModel.getTemperature());
            }
            binding.temperatureTextview.setText(tempText);
            binding.resultTextview.setText(capitalizeFirstLetter(weatherModel.getWeather()));
            binding.tipTextview.setText(getWeatherTip(Double.parseDouble(weatherModel.getTemperature()), weatherModel.getWeather()));
            if (sharedPreferences.getBoolean("gifs", false)) {
                Uri gifUrl = Uri.parse(getGifUrl(Double.parseDouble(weatherModel.getTemperature()), weatherModel.getWeather()));
                // Webp animation
                Transformation<Bitmap> transformation = new CenterInside();

                CircularProgressDrawable circularProgressDrawable;
                circularProgressDrawable = new CircularProgressDrawable(requireContext());
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(30f);
                circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.ka_icon_background));
                circularProgressDrawable.start();

                Glide.with(this)
                        .load(gifUrl) // is not a local resource change to the URL
                        .optionalTransform(transformation)
                        .optionalTransform(WebpDrawable.class, new WebpDrawableTransformation(transformation))
                        .placeholder(circularProgressDrawable)
                        .into(binding.resultGif);

                String meowText;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                    meowText = "Meow";
                } else {
                    meowText = getString(R.string.grinning_cat);
                }
                binding.resultGif.setOnClickListener(l -> Toast.makeText(requireContext(), meowText, Toast.LENGTH_SHORT).show());
            }
            binding.localityTextview.setText(city);

            String feelsLikeText, feelsLikeResultText;
            if (sharedPreferences.getString(UNITS_KEY, GlobalVars.DEFAULT_UNITS).equals("metric")) {
                feelsLikeText = getString(R.string.celsius, weatherModel.getFeelsLike());
            } else {
                feelsLikeText = getString(R.string.fahrenheit, weatherModel.getFeelsLike());
            }
            feelsLikeResultText = getString(R.string.feels_like, feelsLikeText);
            binding.feelsLikeTextview.setText(feelsLikeResultText);

            String windSpeedText;
            if (sharedPreferences.getString(UNITS_KEY, GlobalVars.DEFAULT_UNITS).equals("metric")) {
                windSpeedText = getString(R.string.meter_per_second, weatherModel.getWindSpeed());
            } else {
                windSpeedText = getString(R.string.mph, weatherModel.getWindSpeed());
            }
            binding.windSpeedTextview.setText(windSpeedText);

            UnitsManager unitsManager = new UnitsManager(requireContext(), sharedPreferences);
            String pressureText = unitsManager.getPressureString(Integer.parseInt(weatherModel.getPressure()));
            binding.pressureTextview.setText(pressureText);

            String humidityText = getString(R.string.percent, weatherModel.getHumidity());
            binding.humidityTextview.setText(humidityText);

            String sunriseText = getDateFromTimestamp(weatherModel.getSunrise());
            binding.sunriseTextview.setText(sunriseText);

            String sunsetText = getDateFromTimestamp(weatherModel.getSunset());
            binding.sunsetTextview.setText(sunsetText);

            int lengthOfDayInMillis = weatherModel.getSunset() * 1000 - weatherModel.getSunrise() * 1000;
            String lengthOfDay = getDuration(lengthOfDayInMillis);
            binding.lengthOfDayTextview.setText(lengthOfDay);
        });
    }

    private String setEmojis(String text) {
        String adjustedText;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
            adjustedText = text.replaceAll(characterFilter, "");
        } else {
            adjustedText = text;
        }
        return adjustedText;
    }

    private void updateTexts() {
        binding.disclaimerTextview.setText(getString(R.string.owm_disclaimer));
        binding.detailsTextview.setText(getString(R.string.details));
        binding.windSpeedTitle.setText(getString(R.string.wind_speed));
        binding.pressureTitle.setText(getString(R.string.atmospheric_pressure));
        binding.humidityTitle.setText(getString(R.string.humidity));
        binding.sunriseTitle.setText(getString(R.string.sunrise));
        binding.sunsetTitle.setText(getString(R.string.sunset));
        binding.lengthOfDayTitle.setText(getString(R.string.length_of_day));
        lang = getString(R.string.lang);
    }

    private String getWeatherTip(double tempResult, String weather) {
        String weatherTip = "";
        TreeMap<Double, Integer> weatherTips = new TreeMap<>();
        if (weather.toLowerCase().contains(getString(R.string.rain))) {
            return setEmojis(getString(R.string.rainy));
        } else if (sharedPreferences.getString(UNITS_KEY, GlobalVars.DEFAULT_UNITS).equals("metric")) {
            weatherTips.put(5.0, R.string.cold);
            weatherTips.put(10.0, R.string.coolBreeze);
            weatherTips.put(20.0, R.string.pleasant);
            weatherTips.put(25.0, R.string.warm);
        } else {
            weatherTips.put(41.0, R.string.cold);
            weatherTips.put(50.0, R.string.coolBreeze);
            weatherTips.put(68.0, R.string.pleasant);
            weatherTips.put(77.0, R.string.warm);
        }
        weatherTips.put(Double.MAX_VALUE, R.string.hot);
        Map.Entry<Double, Integer> entry = weatherTips.floorEntry(tempResult);
        if (entry != null) {
            weatherTip = getString(entry.getValue());
        }
        return setEmojis(weatherTip);
    }


    private String getGifUrl(double tempResult, String weather) {
        String gifUrl = "";
        TreeMap<Double, String> gifs = new TreeMap<>();
        if (weather.toLowerCase().contains(getString(R.string.rain))) {
            return "https://i.giphy.com/Gv8ssNe0ayE6JW9ZMB.webp"; // a kitten jumping in puddles
        } else if (sharedPreferences.getString(UNITS_KEY, GlobalVars.DEFAULT_UNITS).equals("metric")) {
            gifs.put(5.0, "https://i.giphy.com/8TIbelFjFXjIJ0Zg1l.webp"); // Stay Warm
            gifs.put(10.0, "https://i.giphy.com/5J1wUyvYnl6Y5qHwlZ.webp"); // FEELS GOOD
            gifs.put(20.0, "https://i.giphy.com/JrSLWbqkTgGSRyJ5cS.webp"); // a cat on a motorcycle
            gifs.put(25.0, "https://i.giphy.com/2SYc7mttUnWWaqvWz8.webp"); // a cat in sunglasses
        } else {
            gifs.put(41.0, "https://i.giphy.com/8TIbelFjFXjIJ0Zg1l.webp"); // Stay Warm
            gifs.put(50.0, "https://i.giphy.com/5J1wUyvYnl6Y5qHwlZ.webp"); // FEELS GOOD
            gifs.put(68.0, "https://i.giphy.com/JrSLWbqkTgGSRyJ5cS.webp"); // a cat on a motorcycle
            gifs.put(77.0, "https://i.giphy.com/2SYc7mttUnWWaqvWz8.webp"); // a cat in sunglasses
        }
        gifs.put(Double.MAX_VALUE, "https://i.giphy.com/ToMjGppLes0ENI5osCc.webp"); // a cat with a fan
        Map.Entry<Double, String> entry = gifs.floorEntry(tempResult);
        if (entry != null) {
            gifUrl = entry.getValue();
        }
        return gifUrl;
    }

    private String getDateFromTimestamp(long timestamp) {
        return DateUtils.formatDateTime(requireContext(), timestamp * 1000, DateUtils.FORMAT_SHOW_TIME);
    }

    private String getDuration(long milliseconds) {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(runnable);
    }
}
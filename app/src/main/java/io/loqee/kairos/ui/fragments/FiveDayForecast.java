package io.loqee.kairos.ui.fragments;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.loqee.kairos.R;
import io.loqee.kairos.base.GlobalVars;
import io.loqee.kairos.databinding.FragmentFiveDayForecastBinding;
import io.loqee.kairos.network.viewModel.forecast.ForecastModel;
import io.loqee.kairos.network.viewModel.forecast.ForecastModelAdapter;
import io.loqee.kairos.network.viewModel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FiveDayForecast#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FiveDayForecast extends Fragment {
    private static final String CITY_KEY = "city";
    private static final String UNITS_KEY = "units";
    private String lang;
    private MainViewModel mainViewModel;
    private SharedPreferences sharedPreferences;
    private final List<ForecastModel> forecastModelList = new ArrayList<>();
    private FragmentFiveDayForecastBinding binding;
    private final Handler handler = new Handler();
    private Runnable runnable;

    public FiveDayForecast() {
        // Required empty public constructor
    }

    public static FiveDayForecast newInstance() {
        return new FiveDayForecast();
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
        binding = FragmentFiveDayForecastBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        runnable = this::setupViews;

        handler.post(runnable);

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            handler.post(runnable);
            binding.swipeRefreshLayout.setRefreshing(false);
        });
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.bubbles_color);

        return view;
    }

    private void setupViews() {
        updateTexts();
        String city = sharedPreferences.getString(CITY_KEY, GlobalVars.DEFAULT_CITY);
        String units = sharedPreferences.getString(UNITS_KEY, GlobalVars.DEFAULT_UNITS);
        observeWeatherForecast(city, units);
    }

    private void observeWeatherForecast(String city, String units) {
        mainViewModel.getWeatherForecast(city, units, lang).observe(this, forecastResult -> {
            ForecastModelAdapter forecastModelAdapter = new ForecastModelAdapter(requireContext(), R.layout.forecast_card, forecastModelList);
            if (forecastResult == null) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.incorrect_location), Toast.LENGTH_SHORT).show();
            } else {
                forecastModelList.clear();
                forecastModelList.addAll(forecastResult);
                binding.forecastCardsListview.setAdapter(forecastModelAdapter);
                binding.forecastCardsListview.invalidateViews();
                forecastModelAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateTexts() {
        binding.fiveDayForecastTitle.setText(getString(R.string.five_day_forecast));
        lang = getString(R.string.lang);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(runnable);
    }
}
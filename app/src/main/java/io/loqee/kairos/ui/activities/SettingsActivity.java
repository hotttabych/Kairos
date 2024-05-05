package io.loqee.kairos.ui.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import io.loqee.kairos.R;
import io.loqee.kairos.base.GeoManager;
import io.loqee.kairos.databinding.SettingsActivityBinding;
import io.loqee.kairos.network.viewModel.MainViewModel;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private static final String CITY_KEY = "city";
    private static final String THEME_KEY = "theme";
    private static final String LANGUAGE_KEY = "language";
    private static final String THEME_LIGHT = "light";
    private static final String THEME_DARK = "dark";
    private static final String THEME_SYSTEM = "system";
    private static final String DEVICE_LANGUAGE = "device";

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private SharedPreferences sharedPreferences;
    private Location mLastLocation;
    private GeoManager geoManager;
    private MainViewModel mainViewModel;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivityBinding settingsActivityBinding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(settingsActivityBinding.getRoot());
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        settingsActivityBinding.topAppBar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        geoManager = new GeoManager(this);
        mainViewModel = new MainViewModel(getApplication());

        preferenceChangeListener = this::onSharedPreferenceChanged;
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
                setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference gpsPref = findPreference("gpsLocation");
            Preference zodiacSignPref = findPreference("zodiac_sign");
            if (gpsPref != null) {
                gpsPref.setOnPreferenceClickListener(preference -> {
                    ((SettingsActivity) requireActivity()).getLastLocation();
                    ((SettingsActivity) requireActivity()).getTownNameAndUpdatePreference();
                    // open browser or intent here
                    return true;
                });
            }
            if (zodiacSignPref != null) zodiacSignPref.setVisible(getString(R.string.lang).equals("ru"));
        }

        private void updateCityPreferenceSummary() {
            Preference cityPref = findPreference(CITY_KEY);
            if (cityPref != null) {
                cityPref.setSummaryProvider(null);
                String city = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(CITY_KEY, "");
                cityPref.setSummary(city);
                cityPref.setSummaryProvider(cityPref.getSummaryProvider());
            }
        }
    }

    private void onSharedPreferenceChanged(@NonNull SharedPreferences sharedPreferences, @Nullable String key) {
        assert key != null;
        if (key.equals(THEME_KEY)) {
            handleThemeChange(sharedPreferences);
        }
        if (key.equals(LANGUAGE_KEY)) {
            setLocale(sharedPreferences);
            SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.settings);
            if (settingsFragment != null) {
                Preference zodiacSignPref = settingsFragment.findPreference("zodiac_sign");
                if (zodiacSignPref != null) zodiacSignPref.setVisible(getString(R.string.lang).equals("ru"));
            }
        }
    }

    private void setLocale(SharedPreferences sharedPreferences) {
        String lang = sharedPreferences.getString(LANGUAGE_KEY, DEVICE_LANGUAGE);
        Resources res = getBaseContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        Locale myLocale;
        if (lang.equals(DEVICE_LANGUAGE)) {
            myLocale = Locale.getDefault();
            sharedPreferences.edit().putString(LANGUAGE_KEY, getResources().getConfiguration().locale.getLanguage()).apply();
        } else {
            myLocale = new Locale(lang);
        }
        Locale.setDefault(myLocale);
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    private void handleThemeChange(@NonNull SharedPreferences sharedPreferences) {
        String currentThemeValue = sharedPreferences.getString(THEME_KEY, THEME_LIGHT);
        // The 'case THEME_LIGHT' branch is redundant as it completely resembles the default branch
        switch (currentThemeValue) {
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }

    private void getTownNameAndUpdatePreference() {
        Toast.makeText(this, getString(R.string.wait), Toast.LENGTH_SHORT).show();
        if (mLastLocation == null) {
            getLastLocation();
        }
        if (mLastLocation != null) {
            Log.i(this.getClass().getSimpleName(), "getTownName: lat: " + mLastLocation.getLatitude() + ", lon: " + mLastLocation.getLongitude());
            mainViewModel.getGeoData("json", mLastLocation.getLatitude(), mLastLocation.getLongitude()).observe(this, geoData -> {
                Toast.makeText(this, getString(R.string.done), Toast.LENGTH_SHORT).show();
                sharedPreferences.edit().putString(CITY_KEY, geoData).apply();
                SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.settings);
                if (settingsFragment != null) {
                    settingsFragment.updateCityPreferenceSummary();
                }
            });
        } else {
            handler.postDelayed(this::getTownNameAndUpdatePreference, 2500);
        }
    }

    private void getLastLocation() {
        geoManager.getLastLocation(location -> {
            if (location == null) {
                geoManager.requestNewLocationData(mLocationListener);
            } else {
                mLastLocation = location;
            }
        });
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            mLastLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(@NonNull String provider) {}

        @Override
        public void onProviderDisabled(@NonNull String provider) {}
    };

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setLocale(sharedPreferences);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }
}
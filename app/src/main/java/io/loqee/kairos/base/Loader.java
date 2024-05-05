package io.loqee.kairos.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import io.loqee.kairos.ui.activities.DashboardActivity;

import java.util.Locale;

public class Loader extends AppCompatActivity {
    private static final String THEME_KEY = "theme";
    private static final String LANGUAGE_KEY = "language";
    private static final String THEME_LIGHT = "light";
    private static final String THEME_DARK = "dark";
    private static final String THEME_SYSTEM = "system";
    private static final String DEVICE_LANGUAGE = "device";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, DashboardActivity.class);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set the night mode according to the theme preference
        setNightMode(sharedPreferences);

        // Set the locale according to the language preference
        setLocale(sharedPreferences);
        startActivity(intent);
        finish(); // Finish the Loader activity to prevent it from being displayed
    }

    // A method that sets the night mode according to the theme preference
    private void setNightMode(SharedPreferences sharedPreferences) {
        String currentThemeValue = sharedPreferences.getString(THEME_KEY, THEME_LIGHT);
        if (THEME_DARK.equals(currentThemeValue)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (THEME_SYSTEM.equals(currentThemeValue)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // A method that sets the locale according to the language preference
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
}
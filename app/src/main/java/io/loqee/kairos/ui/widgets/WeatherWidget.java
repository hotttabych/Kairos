package io.loqee.kairos.ui.widgets;

import android.app.Application;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

import androidx.preference.PreferenceManager;

import io.loqee.kairos.R;
import io.loqee.kairos.base.GlobalVars;
import io.loqee.kairos.network.viewModel.MainViewModel;

import java.util.Locale;

public class WeatherWidget extends AppWidgetProvider {
    private static final String CITY_KEY = "city";
    private static final String UNITS_KEY = "units";
    private static final String LANGUAGE_KEY = "language";
    private static final String DEVICE_LANGUAGE = "device";
    private static String lang;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String city, String temperature, String weatherCondition) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        views.setTextViewText(R.id.appwidget_city, city);
        views.setTextViewText(R.id.appwidget_temperature, temperature);
        views.setTextViewText(R.id.appwidget_weather_condition, weatherCondition);

        // Create an Intent to update the widget
        Intent intentUpdate = new Intent(context, WeatherWidget.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        // Use an array to allow for multiple widget instances
        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        // Wrap the intent as a PendingIntent
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

        // Assign the pending intent to the widget layout
        views.setOnClickPendingIntent(R.id.appwidget_layout, pendingUpdate);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            setLocale(context, sharedPreferences);
            MainViewModel mainViewModel = new MainViewModel((Application) context.getApplicationContext());
            String city = sharedPreferences.getString(CITY_KEY, GlobalVars.DEFAULT_CITY);
            String units = sharedPreferences.getString(UNITS_KEY, GlobalVars.DEFAULT_UNITS);
            mainViewModel.getWeather(city, units, lang).observeForever(weatherResult -> {
                String temperature = weatherResult.getTemperature() + "°";
                updateAppWidget(context, appWidgetManager, appWidgetId, city, temperature, weatherResult.getWeather());
            });
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void setLang(String lang) {
        WeatherWidget.lang = lang;
    }

    private void setLocale(Context context, SharedPreferences sharedPreferences) {
        String lang = sharedPreferences.getString(LANGUAGE_KEY, DEVICE_LANGUAGE);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        Locale myLocale;
        if (lang.equals(DEVICE_LANGUAGE)) {
            myLocale = Locale.getDefault();
            sharedPreferences.edit().putString(LANGUAGE_KEY, context.getResources().getConfiguration().locale.getLanguage()).apply();
        } else {
            myLocale = new Locale(lang);
        }
        Locale.setDefault(myLocale);
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        setLang(context.getString(R.string.lang));
    }
}

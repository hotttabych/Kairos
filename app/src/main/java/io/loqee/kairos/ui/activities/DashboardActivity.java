package io.loqee.kairos.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import io.loqee.kairos.R;
import io.loqee.kairos.base.ViewPager2Adapter;
import io.loqee.kairos.databinding.ActivityDashboardBinding;
import io.loqee.kairos.ui.fragments.FiveDayForecast;
import io.loqee.kairos.ui.fragments.HoroscopeFragment;
import io.loqee.kairos.ui.fragments.Overview;

import java.util.Locale;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {

    // Constants
    private static final String LANGUAGE_KEY = "language";
    private static final String DEVICE_LANGUAGE = "device";
    private static final String THEME_KEY = "theme";
    private static final String THEME_LIGHT = "light";
    private static final String THEME_DARK = "dark";
    private static final String THEME_SYSTEM = "system";

    private String currentLanguage;

    private ActivityDashboardBinding binding;
    private SharedPreferences sharedPreferences;
    private MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        setupTitleBar();
        setNavigationItemSelectionListener();
        setupBottomNavbar(binding.bottomNavbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        currentLanguage = sharedPreferences.getString(LANGUAGE_KEY, DEVICE_LANGUAGE);

        binding.dashboardViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    binding.bottomNavbar.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);
                binding.bottomNavbar.getMenu().getItem(position).setChecked(true);
                prevMenuItem = binding.bottomNavbar.getMenu().getItem(position);
            }

        });

        setupViewPager(binding.dashboardViewpager);

        setLocale(sharedPreferences);

        handleThemeChange(sharedPreferences);
    }

    private void setupTitleBar() {
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false); // disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // remove the icon
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings_btn) {
            Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBottomNavbar(BottomNavigationView bottomNavigationView) {
        if (getString(R.string.lang).equals("ru")) {
            bottomNavigationView.getMenu().clear(); //clear old inflated items.
            bottomNavigationView.inflateMenu(R.menu.bottom_menu_ru);
            setNavigationItemSelectionListener();
            setupViewPager(binding.dashboardViewpager);
        }
    }

    private void setupViewPager(ViewPager2 viewPager2) {
        ViewPager2Adapter adapter = new ViewPager2Adapter(this);
        Fragment overviewFragment = Overview.newInstance();
        Fragment forecastFragment = FiveDayForecast.newInstance();
        adapter.addFragment(overviewFragment);
        adapter.addFragment(forecastFragment);
        if (getString(R.string.lang).equals("ru")) {
            Fragment horoscopeFragment = HoroscopeFragment.newInstance();
            adapter.addFragment(horoscopeFragment);
        }
        viewPager2.setAdapter(adapter);
    }

    private void setNavigationItemSelectionListener() {
        binding.bottomNavbar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.overviewBtn) {
                binding.dashboardViewpager.setCurrentItem(0);
            } else if (itemId == R.id.forecastBtn) {
                binding.dashboardViewpager.setCurrentItem(1);
            } else if (itemId == R.id.horoscopeBtn && getString(R.string.lang).equals("ru")) {
                binding.dashboardViewpager.setCurrentItem(2);
            }
            return false;
        });
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current_fragment", binding.bottomNavbar.getSelectedItemId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLocale(sharedPreferences);
        String newLanguage = sharedPreferences.getString(LANGUAGE_KEY, DEVICE_LANGUAGE);
        if (!newLanguage.equals(currentLanguage)) {
            setupBottomNavbar(binding.bottomNavbar);
            currentLanguage = newLanguage;
        }
    }
}
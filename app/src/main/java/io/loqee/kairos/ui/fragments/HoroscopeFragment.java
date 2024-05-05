package io.loqee.kairos.ui.fragments;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import io.loqee.kairos.databinding.FragmentHoroscopeBinding;
import io.loqee.kairos.network.viewModel.MainViewModel;

import java.util.Arrays;

public class HoroscopeFragment extends Fragment {
    private static final String ZODIAC_SIGN_KEY = "zodiac_sign";
    private static final String HOROSCOPE_FIRST_LAUNCH_PREFERENCE_KEY = "isHoroscopeFirstOpened";
    private static final String ASTROLOGICAL_CAT_GIF_URL = "https://i.giphy.com/vaZMgFDVZD2reZtNkk.webp";
    private String zodiacSign;
    private FragmentHoroscopeBinding binding;
    private MainViewModel mainViewModel;
    private SharedPreferences sharedPreferences;
    private Context context;
    private final Handler handler = new Handler();
    private Runnable runnable;

    public HoroscopeFragment() {
        // Required empty public constructor
    }

    public static HoroscopeFragment newInstance() {
        return new HoroscopeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        mainViewModel = new MainViewModel((Application) requireContext().getApplicationContext());
        context = requireContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHoroscopeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        runnable = this::setupViews;

        handler.post(runnable);

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            handler.post(runnable);
            binding.swipeRefreshLayout.setRefreshing(false);
        });
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.bubbles_color);

        showZodiacSignWarning();

        return view;
    }

    private void setupViews() {
        zodiacSign = sharedPreferences.getString(ZODIAC_SIGN_KEY, GlobalVars.DEFAULT_ZODIAC_SIGN);
        binding.horoscopeTextview.setText(getString(R.string.loading));

        if (sharedPreferences.getBoolean("gifs", false)) {
            Transformation<Bitmap> transformation = new CenterInside();

            CircularProgressDrawable circularProgressDrawable;
            circularProgressDrawable = new CircularProgressDrawable(requireContext());
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.ka_icon_background));
            circularProgressDrawable.start();

            Glide.with(this)
                    .load(ASTROLOGICAL_CAT_GIF_URL) // is not a local resource change to the URL
                    .optionalTransform(transformation)
                    .optionalTransform(WebpDrawable.class, new WebpDrawableTransformation(transformation))
                    .placeholder(circularProgressDrawable)
                    .into(binding.catGif);

            String meowText;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                meowText = "Meow";
            } else {
                meowText = getString(R.string.grinning_cat);
            }
            binding.catGif.setOnClickListener(l -> Toast.makeText(requireContext(), meowText, Toast.LENGTH_SHORT).show());
        }

        observeHoroscope();
    }

    private void observeHoroscope() {
        mainViewModel.getHoroscope(zodiacSign).observe(this, horoscopeResult -> {
            if (horoscopeResult == null) {
                Toast.makeText(requireContext(), context.getString(R.string.incorrect_zodiac_sign_error), Toast.LENGTH_SHORT).show();
            } else {
                binding.horoscopeTextview.setText(horoscopeResult);
            }
        });

        // Getting the index of the zodiac sign in the values array
        String[] zodiacValues = context.getResources().getStringArray(R.array.zodiac_values);
        int index = Arrays.asList(zodiacValues).indexOf(zodiacSign);

        if (index != -1) {
            // Get the corresponding localized string
            String[] zodiacEntries = context.getResources().getStringArray(R.array.zodiac_entries);
            String localizedZodiacSign = zodiacEntries[index];

            // Setting the TextView to display the horoscope text
            binding.zodiacSignTextview.setText(localizedZodiacSign);
        }
    }

    private void showZodiacSignWarning() {
        if (sharedPreferences.getBoolean(HOROSCOPE_FIRST_LAUNCH_PREFERENCE_KEY, true)) {
            Toast.makeText(context, getString(R.string.default_zodiac_sign_warning), Toast.LENGTH_LONG).show();
            sharedPreferences.edit().putBoolean(HOROSCOPE_FIRST_LAUNCH_PREFERENCE_KEY, false).apply();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(runnable);
    }
}
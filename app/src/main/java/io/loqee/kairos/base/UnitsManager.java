package io.loqee.kairos.base;

import android.content.Context;
import android.content.SharedPreferences;

import io.loqee.kairos.R;

public class UnitsManager {
    private static final String UNITS_OF_PRESSURE_KEY = "unitsOfPressure";
    private static final String DEFAULT_UNITS_OF_PRESSURE = "hPa";
    private static final double HPA_TO_MMHG = 0.75006375541921;
    private static final double HPA_TO_INHG = 0.75006375541921;
    private static final double HPA_TO_ATM = 0.0009869233;
    SharedPreferences sharedPreferences;
    Context context;

    public UnitsManager(Context context, SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    public String getPressureString(int pressure) {
        String pressureString;
        double convertedPressure, roundedConvertedPressure;
        switch (sharedPreferences.getString(UNITS_OF_PRESSURE_KEY, DEFAULT_UNITS_OF_PRESSURE)) {
            case "mbar":
                pressureString = context.getString(R.string.pressure_mbar, String.valueOf(pressure));
                break;
            case "mmHg":
                convertedPressure = pressure * HPA_TO_MMHG;
                roundedConvertedPressure = Math.round(convertedPressure * 100.0) / 100.0;
                pressureString = context.getString(R.string.pressure_mmHg, String.valueOf(roundedConvertedPressure));
                break;
            case "inHg":
                convertedPressure = pressure * HPA_TO_INHG;
                roundedConvertedPressure = Math.round(convertedPressure * 100.0) / 100.0;
                pressureString = context.getString(R.string.pressure_inHg, String.valueOf(roundedConvertedPressure));
                break;
            case "atm":
                convertedPressure = pressure * HPA_TO_ATM;
                roundedConvertedPressure = Math.round(convertedPressure * 100.0) / 100.0;
                pressureString = context.getString(R.string.pressure_atm, String.valueOf(roundedConvertedPressure));
                break;
            default:
                pressureString = context.getString(R.string.pressure_hpa, String.valueOf(pressure));
                break;
        }
        return pressureString;
    }
}

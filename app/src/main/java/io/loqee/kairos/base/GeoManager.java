package io.loqee.kairos.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import io.loqee.kairos.R;

public class GeoManager {
    private final LocationManager mLocationManager;
    private final Context context;
    private boolean hasShownLocationOffToast = false; // Add this line

    public interface OnLocationReceivedListener {
        void onLocationReceived(Location location);
    }

    public GeoManager(Context context) {
        this.context = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation(OnLocationReceivedListener onLocationReceivedListener) {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    requestNewLocationData(new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            onLocationReceivedListener.onLocationReceived(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {}

                        @Override
                        public void onProviderEnabled(@NonNull String provider) {
                            hasShownLocationOffToast = false; // Add this line
                        }

                        @Override
                        public void onProviderDisabled(@NonNull String provider) {}
                    });
                } else {
                    onLocationReceivedListener.onLocationReceived(location);
                }
            } else {
                if (!hasShownLocationOffToast) {
                    Toast.makeText(context, context.getString(R.string.location_off), Toast.LENGTH_SHORT).show();
                    hasShownLocationOffToast = true;
                }
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    public void requestNewLocationData(LocationListener mLocationListener) {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, mLocationListener, Looper.myLooper());
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        int PERMISSION_ID = 44;
        ActivityCompat.requestPermissions((AppCompatActivity) context, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
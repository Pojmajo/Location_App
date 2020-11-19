package com.example.location_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.model.PointOfInterest;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MapActivity extends AppCompatActivity implements MapFragment.OnDataPass{
    private static final int PERMISSION_CODE = 1000;
    private LocationManager lm;
    private LocationListener listener;
    private TextView distancetextView;
    PointOfInterest user_poi;


    @Override
    public void onDataPass(PointOfInterest poi) {
        user_poi = poi;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permission, PERMISSION_CODE);
            }
            return;
        }


        Fragment fragment = new MapFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                przetwarzajLokalizacje(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        registerListener();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        registerListener();
    }


    private void registerListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }

    Location prevLocation = null;
    private void przetwarzajLokalizacje(Location location) {
        if (user_poi != null) {
            distancetextView = (TextView) findViewById(R.id.distance_direction_textView);
            Double vanueLat = location.getLatitude();
            Double vanueLon = location.getLatitude();
            Double userLat = user_poi.latLng.latitude;
            Double userLon = user_poi.latLng.longitude;
            Double distance = calculateDistanceInKilometer(userLat, userLon, vanueLat, vanueLon);
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.CEILING);
            String distance_text = "Distance: " + df.format(distance) +"km";

            distancetextView.setText(distance_text);
        }
        prevLocation = location;
    }

    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    public double calculateDistanceInKilometer(double userLat, double userLng,
                                            double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (double) (AVERAGE_RADIUS_OF_EARTH_KM * c);
    }
}


package com.example.location_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private LocationManager lm;
    private LocationListener listener;
    private TextView lon_lan_text_view;
    private TextView speed_direction_text_view;
    private TextView accuracy_text_view;
    private TextView nmeaBox;
    private static final int PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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



        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                przetwarzajLokalizacje(location);
                saveLocation(location);
                SharedPreferences sp = getPreferences(MODE_PRIVATE);
                sp.edit().putString("location", "gps:" + location.getLongitude() + " "+ location.getLatitude()).commit();
                Date d = new Date();
                sp.edit().putString("date",d.toString()).commit();
                showDataFromPref();
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
        accuracy_text_view = (TextView) findViewById(R.id.accuracy_text);
        lon_lan_text_view = (TextView) findViewById(R.id.lon_lat_text);
        speed_direction_text_view = (TextView) findViewById(R.id.speed_direction_text);

        registerListener();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        registerListener();
    }

    public void mapActivity(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void mapActivityCustom(View view) {
        Intent intent = new Intent(this, MapCustomActivity.class);
        startActivity(intent);
    }



    private void registerListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        lm.addNmeaListener((OnNmeaMessageListener) (nmea, timestamp) -> {
            nmeaBox = ((TextView)findViewById(R.id.nmeaBox));
            saveNMEA(nmea);
            if(nmea.contains("GPGSA")) {
                nmeaBox.setText(nmea);
            }
            Log.i("satelliteNMEA", nmea);
        });

    }

    private void saveNMEA(String nmeaStr) {
        FileOutputStream out = null;
        File root = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File myDir = new File(root + "/saved_nmea");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = "NMEA_LAB5";
        File file = new File (myDir, fname);
        try {
            boolean append = true;
            out = new FileOutputStream(file, append);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(out);
        pw.println(nmeaStr);
        pw.close();

    }

    private void saveLocation(Location location) {
    FileOutputStream out = null;
    File root = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
    File myDir = new File(root + "/saved_location");
    if (!myDir.exists()) {
        myDir.mkdirs();
    }
    String location_info = location.getLatitude() + ";" + location.getLongitude();
    String fname = "Location_LAB5.txt";
    File file = new File (myDir, fname);
    try {
        boolean append = true;
        out = new FileOutputStream(file, append);
    } catch (Exception e) {
        e.printStackTrace();
    }
    PrintWriter pw = new PrintWriter(out);
    pw.println(location_info);
    pw.close();
    }

    void showDataFromPref(){
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        TextView preftv = (TextView) findViewById(R.id.preferenceTextView);
        preftv.setText(sp.getString("date","date:empty")+ "\n"+sp.getString("location","loc:empty"));
    }

    Location prevLocation = null;
    private void przetwarzajLokalizacje(Location location) {
        String lon_lat_info = "Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude();
        String accuracy_info = "Location accuracy: " + location.getAccuracy() + "%";
        String speed_direction_info = "Speed: " + location.getSpeed() + "\nDirection: ";
            if(prevLocation!=null){
                float bearing = prevLocation.bearingTo(location);
                speed_direction_info += bearing;
            }
        accuracy_text_view.setText(accuracy_info);
        lon_lan_text_view.setText(lon_lat_info);
        speed_direction_text_view.setText(speed_direction_info);
        prevLocation = location;
    }

}
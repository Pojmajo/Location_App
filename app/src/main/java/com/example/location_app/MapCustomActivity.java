package com.example.location_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class MapCustomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_custom);

        Fragment customFragment = new MapsCustomFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_custom, customFragment)
                .commit();
    }
}
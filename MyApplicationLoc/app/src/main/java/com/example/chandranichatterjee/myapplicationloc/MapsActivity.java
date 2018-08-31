package com.example.chandranichatterjee.myapplicationloc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sqisland.tutorial.recipes.R;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    Double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            lat = b.getDouble("lat");
            lon = b.getDouble("lon");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(lat, lon);
        googleMap.addMarker(new MarkerOptions().position(latLng)
                .title("Your last location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}

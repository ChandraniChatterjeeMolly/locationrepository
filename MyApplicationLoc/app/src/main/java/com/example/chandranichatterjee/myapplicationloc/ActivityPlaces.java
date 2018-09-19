package com.example.chandranichatterjee.myapplicationloc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.sqisland.tutorial.recipes.R;

public class ActivityPlaces extends AppCompatActivity {

    private static final String DEST_PLACE = "DEST_PLACE";
    private static final int BACK_TO_MAIN_INTENT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        PlaceAutocompleteFragment places= (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Toast.makeText(getApplicationContext(),place.getName(),Toast.LENGTH_SHORT).show();

                Intent backToMainIntent = new Intent();
                backToMainIntent.putExtra(DEST_PLACE, place.getName());
                setResult(BACK_TO_MAIN_INTENT, backToMainIntent);
                finish();
            }

            @Override
            public void onError(Status status) {

                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();

            }
        });
    }
}

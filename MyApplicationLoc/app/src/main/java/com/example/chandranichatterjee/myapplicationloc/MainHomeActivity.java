package com.example.chandranichatterjee.myapplicationloc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.chandranichatterjee.myapplicationloc.util.CircleTransform;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sqisland.tutorial.recipes.R;

public class MainHomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Button btnMenu;
    DrawerLayout menu_layout;
    String profileName, profileEmail, photoURI;
    ImageView profile_pic;
    LinearLayout ll_get_loc;
    TextView profile_name, profile_email, tv_get_loc;
    private ActionBarDrawerToggle mDrawerToggle;
    private FusedLocationProviderApi fusedLocationProviderApi;
    GoogleApiClient googleApiClient;
    Location location;
    boolean retres = false;
    private GoogleMap mGoogleMap;
    LocationRequest locationRequest;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int REQUEST_CHECK_SETTINGS = 33;
    private static final int REQUEST_PERMISSIONS_LAST_LOCATION_REQUEST_CODE = 34;
    private static final int REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE = 35;
    protected static long MIN_UPDATE_INTERVAL = 30 * 1000;
    private LocationManager locationManager;
    private boolean GpsStatus;
    private ProgressDialog loading;
    int started = 0;
    int back = 0;
    int reload = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_home);

        //getting values from intent
        Bundle b = getIntent().getExtras();
        if (b != null) {
            profileName = b.getString("profileName");
            profileEmail = b.getString("profileEmail");
            photoURI = b.getString("photoURI");
        }

        btnMenu = (Button) findViewById(R.id.btnMenu);
        menu_layout = (DrawerLayout) findViewById(R.id.menu_layout);
        menu_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        menu_layout.closeDrawer(Gravity.LEFT);

        tv_get_loc = (TextView) findViewById(R.id.tv_get_loc);
        ll_get_loc = (LinearLayout) findViewById(R.id.ll_get_loc);

        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        ll_get_loc.startAnimation(animFadeIn);

        ll_get_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                fusedLocationProviderApi = LocationServices.FusedLocationApi;
                checkLocationPermission();
//                if (checkLocationPermission()) {
//                    //this block
////                    if (turnOnLocation()) {
//////                        if (!(googleApiClient != null || googleApiClient.isConnected())) {
//////                            googleApiClient = new GoogleApiClient.Builder(MainHomeActivity.this)
//////                                    .enableAutoManage(MainHomeActivity.this, MainHomeActivity.this)
//////                                    .addApi(LocationServices.API)
//////                                    .addConnectionCallbacks(MainHomeActivity.this)
//////                                    .addOnConnectionFailedListener(MainHomeActivity.this)
//////                                    .build();
//////                            googleApiClient.connect();
//////                        }
////                        if (googleApiClient == null) {
////                            googleApiClient = new GoogleApiClient.Builder(MainHomeActivity.this)
////                                    .enableAutoManage(MainHomeActivity.this, MainHomeActivity.this)
////                                    .addApi(LocationServices.API)
////                                    .addConnectionCallbacks(MainHomeActivity.this)
////                                    .addOnConnectionFailedListener(MainHomeActivity.this)
////                                    .build();
////                        }
////                        if (!googleApiClient.isConnected()) {
////                            googleApiClient.connect();
////                            startLoadingDialogue();
////                        }
//////                    LocationRequest locationRequest = LocationRequest.create()
//////                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//////
//////                    locationRequest.setInterval(5);
//////                    locationRequest.setFastestInterval(1);
//////                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, MainHomeActivity.this);
//////                    location = fusedLocationProviderApi.getLastLocation(googleApiClient);
////
//////                    if (location != null) {
//////
//////                        //drawMarker(location);
//////                        Intent i = new Intent(MainHomeActivity.this, MapsActivity.class);
//////                        Bundle b = new Bundle();
//////                        b.putDouble("lat", location.getLatitude());
//////                        b.putDouble("lon", location.getLongitude());
//////                        i.putExtras(b);
//////                        startActivity(i);
//////                    }
////                    } else {
////                        Toast.makeText(MainHomeActivity.this, "Loaction Must be turned on to proceed", Toast.LENGTH_LONG).show();
////                    }
//                    //this block
//                }
            }
        });

        menu_layout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                        profile_pic = (ImageView) drawerView.findViewById(R.id.profile_pic);
                        profile_name = (TextView) drawerView.findViewById(R.id.profile_name);
                        profile_email = (TextView) drawerView.findViewById(R.id.profile_email);

                        Glide.with(MainHomeActivity.this).load(photoURI)
                                .crossFade()
                                .thumbnail(0.5f)
                                .bitmapTransform(new CircleTransform(MainHomeActivity.this))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(profile_pic);

                        profile_name.setText(profileName);
                        profile_email.setText(profileEmail);

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                        drawerView.clearFocus();
                        drawerView.setPressed(false);
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

        mDrawerToggle = new ActionBarDrawerToggle(this, menu_layout,
                R.drawable.menu1, 0, 0) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }

        };
        menu_layout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        btnMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                //rposUtil.hideSoftKeyboard(btnDrawer);
                if (menu_layout != null) {
                    if (menu_layout.isDrawerOpen(Gravity.LEFT)) {
                        //isDrawerOpen = false;
                        menu_layout.closeDrawer(Gravity.LEFT);
                    } else {
                        //isDrawerOpen = true;
                        menu_layout.openDrawer(Gravity.LEFT);
                    }
                }

            }
        });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        //menuItem.setChecked(true);


                        // close drawer when item is tapped
                        menu_layout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here


                        return true;
                    }
                });

    }

    private void startLoadingDialogue() {
        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setTitle("Please Wait");
        loading.setMessage("Getting your location...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCanceledOnTouchOutside(false);
        loading.setIcon(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.your_loc));
        loading.show();
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                new AlertDialog.Builder(this)
//                        .setTitle(R.string.title_location_permission)
//                        .setMessage(R.string.text_location_permission)
//                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //Prompt the user once explanation has been shown
//                                ActivityCompat.requestPermissions(MainHomeActivity.this,
//                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                        MY_PERMISSIONS_REQUEST_LOCATION);
//
////                                Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////                                startActivity(intent1);
//                            }
//                        })
//                        .create()
//                        .show();
//
//
//            } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            //}
            return false;
        } else {
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(MainHomeActivity.this, MainHomeActivity.this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
            }
            if (!googleApiClient.isConnected()) {
                startLoadingDialogue();
                googleApiClient.connect();

            }
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (turnOnLocation()) {

//                        if (!(googleApiClient != null && googleApiClient.isConnected())) {
                            //if (googleApiClient == null || !(googleApiClient.isConnected())) {
//                            googleApiClient = new GoogleApiClient.Builder(this)
//                                    .enableAutoManage(MainHomeActivity.this, MainHomeActivity.this)
//                                    .addApi(LocationServices.API)
//                                    .addConnectionCallbacks(this)
//                                    .addOnConnectionFailedListener(this)
//                                    .build();
//                            googleApiClient.connect();
                            if (googleApiClient == null) {
                                googleApiClient = new GoogleApiClient.Builder(this)
                                        .enableAutoManage(MainHomeActivity.this, MainHomeActivity.this)
                                        .addApi(LocationServices.API)
                                        .addConnectionCallbacks(this)
                                        .addOnConnectionFailedListener(this)
                                        .build();
                            }
                            if (!googleApiClient.isConnected()) {
                                googleApiClient.connect();
                                startLoadingDialogue();
                            }
                            // }
                            //commented
//                            LocationRequest locationRequest = LocationRequest.create()
//                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                            locationRequest.setInterval(5);
//                            locationRequest.setFastestInterval(1);
//                            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
//
//                            location = fusedLocationProviderApi.getLastLocation(googleApiClient);
                        }
//                         else {
//                            new AlertDialog.Builder(this)
//                                    .setTitle("Sorry!!")
//                                    .setMessage(getString(R.string.text_location_permission))
//                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                        }
//                                    })
//                                    .show();
//                        }
                    }


                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Sorry!!")
                            .setMessage(getString(R.string.text_location_permission))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                }


            }
            return;
        }

    }


    @Override
    public void onBackPressed() {
        if (menu_layout.isDrawerOpen(GravityCompat.START)) {
            menu_layout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (menu_layout.isDrawerOpen(GravityCompat.START)) {

                menu_layout.closeDrawer(GravityCompat.START);
            } else {
                return super.dispatchTouchEvent(event);
            }
//        } else if (event.getAction() == MotionEvent.ACTION_DOWN && isOutSideClicked) {
//            isOutSideClicked = false;
//            return super.dispatchTouchEvent(event);
//        } else if (event.getAction() == MotionEvent.ACTION_MOVE && isOutSideClicked) {
//            return super.dispatchTouchEvent(event);
//        }
//
//        if (isOutSideClicked) {
//            //make http call/db request
//            Toast.makeText(this, "Hello..", Toast.LENGTH_SHORT).show();
//        }
//        return super.dispatchTouchEvent(event);
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5);
        locationRequest.setFastestInterval(1);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        location = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);

        loadLocation();

//        if (loading != null && loading.isShowing()) {
//            loading.dismiss();
//            loading.cancel();
//        }
//
//        if (location != null) {
//
//            //drawMarker(location);
//            Intent i = new Intent(MainHomeActivity.this, MapsActivityNew.class);
//            Bundle b = new Bundle();
//            b.putDouble("lat", location.getLatitude());
//            b.putDouble("lon", location.getLongitude());
//            i.putExtras(b);
//            back = 1;
//            googleApiClient.disconnect();
//            startActivity(i);
//        } else {
//            Toast.makeText(this, "Sorry Location can not be loaded. Please try again later", Toast.LENGTH_LONG).show();
//        }

    }

    private void loadLocation() {

        if (loading != null && loading.isShowing()) {
            loading.dismiss();
            loading.cancel();
        }

        if (location != null) {

            //drawMarker(location);
            Intent i = new Intent(MainHomeActivity.this, MapsActivityNew.class);
            Bundle b = new Bundle();
            b.putDouble("lat", location.getLatitude());
            b.putDouble("lon", location.getLongitude());
            i.putExtras(b);
            back = 1;
            googleApiClient.disconnect();
            startActivity(i);
        } else {
            if (reload == 0){
                reload ++;
                loadLocation();
            }
           // else
            //Toast.makeText(this, "Sorry Location can not be loaded. Please try again later", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (back != 1) {
        if (started == 1) {
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(MainHomeActivity.this, MainHomeActivity.this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
            }
            if (!googleApiClient.isConnected()) {
                googleApiClient.connect();
                startLoadingDialogue();
            }
        }
    }
    else {
            googleApiClient.disconnect();
        }
    }




    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (back != 1) {
            if (started == 1) {
                if (googleApiClient == null) {
                    googleApiClient = new GoogleApiClient.Builder(this)
                            .enableAutoManage(MainHomeActivity.this, MainHomeActivity.this)
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();
                }
                if (!googleApiClient.isConnected()) {
                    googleApiClient.connect();
                    startLoadingDialogue();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (back != 1) {
        if (started == 1) {
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(MainHomeActivity.this, MainHomeActivity.this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
            }
            if (!googleApiClient.isConnected()) {
                googleApiClient.connect();
                startLoadingDialogue();
            }
        }
    }
    else{
            googleApiClient.disconnect();
        }
    }

    private boolean turnOnLocation() {
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true);
//        builder.build();
//
//        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult result) {
//                final Status status = result.getStatus();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        //Log.i(TAG, "All location settings are satisfied.");
//                        retres = true;
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        //Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
//
//                        try {
//                            // Show the dialog by calling startResolutionForResult(), and check the result
//                            // in onActivityResult().
//                            status.startResolutionForResult(MainHomeActivity.this, REQUEST_CHECK_SETTINGS);
//                            retres  = true;
//                        } catch (IntentSender.SendIntentException e) {
//                            //Log.i(TAG, "PendingIntent unable to execute request.");
//                            retres = false;
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        //Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
//                        retres = false;
//                        break;
//                }
//            }
//        });
//
//        return  retres;

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (GpsStatus == true) {
                retres = true;
            } else {
                started = 1;
                //del later
                Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent1);
                //turnOnLocation();
                //
            }
            return retres;
        } catch (Exception e) {
            return retres;
        }

    }


}

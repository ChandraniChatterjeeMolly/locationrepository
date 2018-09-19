package com.example.chandranichatterjee.myapplicationloc;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.chandranichatterjee.myapplicationloc.util.CircleTransform;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.sqisland.tutorial.recipes.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainHomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Button btnMenu, cancel, go;
    DrawerLayout menu_layout;
    String profileName, profileEmail, photoURI;
    ImageView profile_pic;
    LinearLayout ll_get_loc, ll_get_dest;
    TextView profile_name, profile_email, tv_get_loc;
    private ActionBarDrawerToggle mDrawerToggle;
    private FusedLocationProviderApi fusedLocationProviderApi;
    GoogleApiClient googleApiClient;
    Location location;
    boolean retres = false;
    private GoogleMap mGoogleMap;
    LocationRequest locationRequest;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private LocationManager locationManager;
    private boolean GpsStatus;
    private ProgressDialog loading;
    int started = 0;
    int back = 0;
    int reload = 0;
    int reload1 = 0;
    int loc = 0;
    int dest = 0;
    VideoView video_view;
    Uri uri;
    EditText et_src, et_dest;
    double latloc,longloc;
    private ImageView img_src;
    String destPlc;


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
        video_view = (VideoView) findViewById(R.id.video_view);
        uri = Uri.parse("android.resource://com.example.chandranichatterjee.myapplicationloc/" + R.raw.loc4);
        video_view.setVideoURI(uri);
        video_view.start();
        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                video_view.start();
            }
        });

        menu_layout = (DrawerLayout) findViewById(R.id.menu_layout);
        menu_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        menu_layout.closeDrawer(Gravity.LEFT);

        ll_get_loc = (LinearLayout) findViewById(R.id.ll_get_loc);
        ll_get_dest = (LinearLayout) findViewById(R.id.ll_get_dest);
        // ll_get_dest.setVisibility(View.GONE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dest_dialog = inflater.inflate(R.layout.dest_dialog, null, false);

        final Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        ll_get_loc.startAnimation(animFadeIn);
        final Animation animFadeIn1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_new);
        ll_get_dest.startAnimation(animFadeIn1);

        final Animation animBlink = new AlphaAnimation(0.0f, 1.0f);
        animBlink.setDuration(50);
        animBlink.setStartOffset(20);
        animBlink.setRepeatMode(Animation.REVERSE);
        animBlink.setRepeatCount(Animation.INFINITE);

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainHomeActivity.this);
        builder.setTitle("Find you destination")
                .setView(dest_dialog)
                .setCancelable(false)
                .setIcon(R.drawable.your_dest_dia)
                .create();

        final AlertDialog alertDialog = builder.create();

        ll_get_dest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dest = 1;
                alertDialog.show();

                et_src = (EditText) dest_dialog.findViewById(R.id.et_src);
                et_dest = (EditText) dest_dialog.findViewById(R.id.et_dest);
                cancel = (Button) dest_dialog.findViewById(R.id.cancel);
                go = (Button) dest_dialog.findViewById(R.id.go);
                img_src = (ImageView) dest_dialog.findViewById(R.id.img_src);

                if (et_src.getText().toString() == null || et_src.getText().toString().isEmpty()) {
                    img_src.setAnimation(animBlink);
                }

                et_src.setSingleLine(true);
                et_src.setMarqueeRepeatLimit(-1);
                et_dest.setSingleLine(true);
                et_dest.setMarqueeRepeatLimit(-1);

                fusedLocationProviderApi = LocationServices.FusedLocationApi;
                checkLocationPermission();

//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        latloc = location.getLatitude();
//                        longloc = location.getLongitude();
//                        String strplc = getAddressFromLocation(latloc,longloc);
//                        et_src.setText(strplc);
//                        img_src.clearAnimation();
//                    }
//                },5000);



                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                et_dest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainHomeActivity.this,ActivityPlaces.class);
                        startActivity(i);
                    }
                });

            }
        });




        ll_get_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loc = 1;
                fusedLocationProviderApi = LocationServices.FusedLocationApi;
                checkLocationPermission();
            }
        });

        menu_layout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                        video_view.start();
                        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                video_view.start();
                            }
                        });
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
                        video_view.start();
                        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                video_view.start();
                            }
                        });

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                        drawerView.clearFocus();
                        drawerView.setPressed(false);
                        video_view.start();
                        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                video_view.start();
                            }
                        });

                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                        video_view.start();
                        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                video_view.start();
                            }
                        });
                    }
                }
        );

        mDrawerToggle = new ActionBarDrawerToggle(this, menu_layout,
                R.drawable.menu1, 0, 0) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                video_view.start();
                video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        video_view.start();
                    }
                });
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                video_view.start();
                video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        video_view.start();
                    }
                });
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
                        menu_layout.closeDrawers();
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
            if (turnOnLocation()) {
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
        return false;
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

        if (loading != null && loading.isShowing()) {
            loading.dismiss();
            loading.cancel();
        }

        if (dest == 1) {
            loadDest();
        }


        if (loc == 1) {
            loadLocation();
        }
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
            if (reload == 0) {
                reload++;
                loadLocation();
            }
            // else
            //Toast.makeText(this, "Sorry Location can not be loaded. Please try again later", Toast.LENGTH_LONG).show();
        }
    }

    private void loadDest() {

        if (loading != null && loading.isShowing()) {
            loading.dismiss();
            loading.cancel();
        }

        if (location != null) {

            latloc = location.getLatitude();
            longloc = location.getLongitude();
            String strplc = getAddressFromLocation(latloc,longloc);
            et_src.setText(strplc);
            img_src.clearAnimation();
            googleApiClient.disconnect();
        } else {
            if (reload1 == 0) {
                reload1++;
                loadDest();
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
        video_view.start();
        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                video_view.start();
            }
        });

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
        } else {
            googleApiClient.disconnect();
        }
    }


    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    protected void onPause() {
        super.onPause();

        video_view.start();
        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                video_view.start();
            }
        });

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
        video_view.start();
        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                video_view.start();
            }
        });

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
        } else {
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

                Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
               // startActivity(intent1);
                startActivityForResult(intent1, RESULT_OK);
                //turnOnLocation();
                //
            }
            return retres;
        } catch (Exception e) {
            return retres;
        }

    }
    private String getAddressFromLocation(double latitude,double longitude){

        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        String strplace;

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                android.location.Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();
                for (int i = 0; i <= fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(" ");
                }

                strplace = strAddress.toString();
                return strplace;

            } else {
                return "Searching Current Address";
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "Could not get address..!";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        turnOnLocation();

        if (requestCode == 1000){
            if (data != null) {
                if (data.getExtras() != null) {
                    destPlc = data.getExtras().getString("DEST_PLACE");
                    et_dest.setText(""+destPlc);
                }
            }
        }
    }
}

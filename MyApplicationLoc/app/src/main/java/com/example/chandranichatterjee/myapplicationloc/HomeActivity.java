package com.example.chandranichatterjee.myapplicationloc;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.Task;
import com.sqisland.tutorial.recipes.R;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private final int RC_SIGN_IN = 2000;
    GoogleApiClient mGoogleApiClient;
    MySharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        pref = new MySharedPreferences(this);

        pref.setFirstLogin(false);
        if (checkGoogleUpdates()) {
            googleSignIn();
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("hi").setTitle("hi")
                .setPositiveButton("Impostazioni", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                })
                .setNegativeButton("Chiudi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
    }

    private void googleSignIn() {
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestScopes(new Scope(Scopes.DRIVE_FILE))
//                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
//                .requestScopes(new Scope(Scopes.CLOUD_SAVE))
//                .requestScopes(new Scope(Scopes.PROFILE))
//                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
//                .requestScopes(new Scope(Scopes.PLUS_ME))
//                .build();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this,
//                        this)
//                .addApi(Drive.API)
//                .addApi(Plus.API)
//                //.addScope(Drive.SCOPE_FILE)
//                //.addScope(Plus.SCOPE_PLUS_PROFILE)
//                //.addScope(Plus.SCOPE_PLUS_LOGIN)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Drive.API)
                .addApi(Plus.API)
                .build();
         mGoogleApiClient.connect();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private boolean checkGoogleUpdates() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (errorCode == ConnectionResult.SUCCESS) {
            //Toast.makeText(this, "Play services are up-to-date", Toast.LENGTH_SHORT).show();
            return true;
        }
        //else if (googleApiAvailability.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS)
        else {
            if (errorCode == 9) {
                Intent i = new Intent(this, NoSupportActivity.class);
                Bundle b = new Bundle();
                b.putString("error_detail", getString(R.string.service_invalid));
                i.putExtras(b);
                startActivity(i);
            } else {
                if (googleApiAvailability.isUserResolvableError(errorCode)) {
                    googleApiAvailability.getErrorDialog(this, errorCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
//                Toast.makeText(this,"Your device is not supported for this app", Toast.LENGTH_SHORT);
                    //finish();
                    Intent i = new Intent(this, NoSupportActivity.class);
                    Bundle b = new Bundle();
                    b.putString("error_detail", getString(R.string.no_support_title));
                    i.putExtras(b);
                    startActivity(i);
                }
            }
            return false;

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
//                if (resultCode == Activity.RESULT_OK) {
//                    // Google+
//                    if (mGoogleApiClient.hasConnectedApi(Plus.API))
//                        person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
//
//                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//
//                }
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfolly, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Uri photoURI = acct.getPhotoUrl();
            String profileName = acct.getDisplayName();
            String profileEmail = acct.getEmail();


            if (acct.getPhotoUrl() != null) {
                //new LoadProfileImage(imgProfilePic).execute(acct.getPhotoUrl().toString());
            }
            Intent i = new Intent(this,MainHomeActivity.class);
            Bundle b = new Bundle();
            b.putString("profileName", profileName);
            b.putString("profileEmail", profileEmail);
            b.putString("photoURI",photoURI.toString());
            i.putExtras(b);
            startActivity(i);

        } else {
            // Signed out, show unauthenticated UI.

        }
    }


}

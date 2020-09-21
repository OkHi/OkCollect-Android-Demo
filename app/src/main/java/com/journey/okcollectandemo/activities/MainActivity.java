package com.journey.okcollectandemo.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journey.okcollectandemo.R;
import com.journey.okcollectandemo.database.DataProvider;
import com.parse.ParseUser;

import io.okhi.android_core.models.OkHiAppContext;
import io.okhi.android_core.models.OkHiAuth;
import io.okhi.android_core.models.OkHiException;
import io.okhi.android_core.models.OkHiLocation;
import io.okhi.android_core.models.OkHiMode;
import io.okhi.android_core.models.OkHiUser;
import io.okhi.android_okcollect.OkCollect;
import io.okhi.android_okcollect.callbacks.OkCollectCallback;
import io.okhi.android_okcollect.utilities.OkHiConfig;
import io.okhi.android_okcollect.utilities.OkHiTheme;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.journey.okcollectandemo.Secret.DEV_CLIENT_BRANCH;
import static com.journey.okcollectandemo.Secret.DEV_CLIENT_KEY;

public class MainActivity extends AppCompatActivity {
    OkHiAppContext okhiAppContext = new OkHiAppContext.Builder("dev")
            .setDeveloper("OkHi")
            .setPlatform("Android")
            .setAppMeta("OkCollect Demo", "1.0.0", 1)
            .build();
    OkHiAuth auth = new OkHiAuth.Builder(DEV_CLIENT_BRANCH, DEV_CLIENT_KEY)
            .withContext(okhiAppContext)
            .build();
    private OkCollect okCollect;
    private FloatingActionButton createAddressBtn;
    private ProgressBar progressBar;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 600000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 7;
    private RecyclerView addressList;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mCurrentLocation;
    private SettingsClient mSettingsClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private String phone, userId, firstname, lastname, sessionToken;
    private OkCollectCallback<OkHiUser, OkHiLocation> okCollectCallback;
    private DataProvider dataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createAddressBtn = findViewById(R.id.fab);
        progressBar = findViewById(R.id.progressBar);
        dataProvider = new DataProvider(this);
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
            mSettingsClient = LocationServices.getSettingsClient(MainActivity.this);
        } catch (Exception e) {
            displayLog("mfusedlocationclient error " + e.toString());
        }
        OkHiTheme theme = new OkHiTheme.Builder("#ba0c2f")
                .withAppBar("https://cdn.okhi.co/icon.png", "#ba0c2f")
                .build();

        OkHiConfig config = new OkHiConfig.Builder()
                .withStreetView()
                .build();

        okCollect = new OkCollect.Builder(auth, this)
                .withTheme(theme)
                .withConfig(config)
                .build();

        createAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHiUser user = new OkHiUser.Builder(phone)
                        .withFirstName(firstname)
                        .withLastName(lastname)
                        .build();
                okCollect.launch(user, new OkCollectCallback<OkHiUser, OkHiLocation>() {
                    @Override
                    public void onSuccess(OkHiUser user, OkHiLocation location) {
                        showMessage("Address created "+user.getPhone()+" "+location.getId());
                    }

                    @Override
                    public void onError(OkHiException e) {
                        showMessage("Error "+e.getMessage());
                    }
                });
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(VISIBLE);
        try {
            if (ParseUser.getCurrentUser() != null) {
                if (ParseUser.getCurrentUser().isAuthenticated()) {
                    checkPermission();
                } else {
                    progressBar.setVisibility(INVISIBLE);
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            } else {
                progressBar.setVisibility(INVISIBLE);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            displayLog("error getting address list " + e.toString());
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }


    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
            }
        };
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        displayLog("All location settings are satisfied.");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                displayLog("Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    displayLog("PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                displayLog(errorMessage);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        break;
                    case Activity.RESULT_CANCELED:
                        displayLog(" settings result canceled");
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    public void checkLocationSettings() {
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
        initializeUser();
    }

    private void checkPermission() {
        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
        if (permissionAccessFineLocationApproved) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                boolean backgroundLocationPermissionApproved =
                        ActivityCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                == PackageManager.PERMISSION_GRANTED;
                if (backgroundLocationPermissionApproved) {
                    checkLocationSettings();
                } else {
                    final Dialog dialog = new Dialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    dialog.getWindow().setDimAmount(0.6f);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.dialog_permission_primer);
                    dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
                    final Button submitbidBtn = dialog.findViewById(R.id.gotitBtn);
                    submitbidBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                            Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                    MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                        }
                    });
                    try {
                        dialog.show();
                    } catch (Exception e) {
                        displayLog("dialog show error " + e.toString());
                    }
                }
            } else {
                checkLocationSettings();
            }
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.getWindow().setDimAmount(0.6f);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_permission_primer);
            dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
            final Button submitbidBtn = dialog.findViewById(R.id.gotitBtn);
            submitbidBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                },
                                MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                    }
                }
            });
            try {
                dialog.show();
            } catch (Exception e) {
                displayLog("dialog show error " + e.toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                if (ParseUser.getCurrentUser() != null) {
                    ParseUser.logOut();
                    dataProvider.deleteAllAddresseses();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    ParseUser.logOut();
                    dataProvider.deleteAllAddresseses();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationSettings();
                }
                return;
            }
        }
    }

    private void initializeUser() {
        progressBar.setVisibility(INVISIBLE);
        ParseUser user = ParseUser.getCurrentUser();
        sessionToken = user.getSessionToken();
        userId = user.getObjectId();
        phone = user.getUsername();
        firstname = user.getString("firstName");
        lastname = user.getString("lastName");
        displayAddresses(true);
    }

    private void displayAddresses(Boolean display){

    }

    private void displayLog(String log){
        Log.i("MainActivity", log);
    }

}
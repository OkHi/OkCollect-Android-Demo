package com.journey.okcollectandemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.journey.okcollectandemo.R;

import io.okhi.android_core.OkHi;
import io.okhi.android_core.interfaces.OkHiRequestHandler;
import io.okhi.android_core.models.OkHiAppContext;
import io.okhi.android_core.models.OkHiAuth;
import io.okhi.android_core.models.OkHiException;
import io.okhi.android_core.models.OkHiLocation;
import io.okhi.android_core.models.OkHiUser;
import io.okhi.android_okcollect.OkCollect;
import io.okhi.android_okcollect.callbacks.OkCollectCallback;
import io.okhi.android_okcollect.utilities.OkHiConfig;
import io.okhi.android_okcollect.utilities.OkHiTheme;

import static com.journey.okcollectandemo.Secret.DEV_CLIENT_BRANCH;
import static com.journey.okcollectandemo.Secret.DEV_CLIENT_KEY;
import static com.journey.okcollectandemo.Secret.PHONE;

public class MainActivity extends AppCompatActivity {
    private static final OkHiAppContext okhiAppContext = new OkHiAppContext.Builder("dev")
            .setDeveloper("OkHi")
            .setPlatform("Android")
            .setAppMeta("OkHi", "1.0.0", 1)
            .build();

    private static final OkHiAuth okhiAuth = new OkHiAuth.Builder(DEV_CLIENT_BRANCH, DEV_CLIENT_KEY)
            .withContext(okhiAppContext)
            .build();

    private OkCollect okCollect;
    private OkHi okhi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        okhi = new OkHi(this);

        OkHiTheme theme = new OkHiTheme.Builder("#008577")
                .setAppBarLogo("https://cdn.okhi.co/icon.png") //optional
                .setAppBarColor("#008577") //optional, the primary color value will be used.
                .build();

        OkHiConfig config = new OkHiConfig.Builder()
                .withStreetView()
                .build();

        okCollect = new OkCollect.Builder(okhiAuth, this)
                .withTheme(theme)
                .withConfig(config)
                .build();
    }

    //start the address creation flow
    private void launchOkCollect(){
        boolean canStartOkCollect = canStartAddressCreation();
        if(canStartOkCollect) {
            final OkHiUser user = new OkHiUser.Builder(PHONE)
                    .withFirstName("Ramogi")
                    .withLastName("Ochola")
                    .build();
            okCollect.launch(user, new OkCollectCallback<OkHiUser, OkHiLocation>() {
                @Override
                public void onSuccess(OkHiUser user, OkHiLocation location) {
                    displayLog(user.getPhone() + " " + location.getId());
                    showMessage(user.getPhone() + " " + location.getId());
                }

                @Override
                public void onError(OkHiException e) {
                    displayLog(e.getCode() + " " + e.getMessage());
                    showMessage(e.getCode() + " " + e.getMessage());
                }
            });
        }
    }
    private void showMessage(String log){
        Toast.makeText(MainActivity.this,log,Toast.LENGTH_LONG).show();
    }

    //handler class that extends OkHiRequestHandler
    class Handler implements OkHiRequestHandler<Boolean> {
        @Override
        public void onResult(Boolean result) {
            if (result) launchOkCollect();
        }
        @Override
        public void onError(OkHiException exception) {
            showMessage(exception.getMessage());
        }
    }

    // Define a method you'll use to check if conditions are met to start address creation
    private boolean canStartAddressCreation() {
        Handler requestHandler = new Handler();
        // Check and request user to enable location services
        if (!OkHi.isLocationServicesEnabled(getApplicationContext())) {
            okhi.requestEnableLocationServices(requestHandler);
        } else if (!OkHi.isGooglePlayServicesAvailable(getApplicationContext())) {
            // Check and request user to enable google play services
            okhi.requestEnableGooglePlayServices(requestHandler);
        } else if (!OkHi.isLocationPermissionGranted(getApplicationContext())) {
            // Check and request user to grant location permission
            okhi.requestLocationPermission("Hey we need location permissions", "Pretty please..", requestHandler);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Pass permission results to okcollect
        okhi.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass activity results results to okcollect
        okhi.onActivityResult(requestCode, resultCode, data);
    }

    public void handleButtonTap(View view) {
        launchOkCollect();
    }
    private void displayLog(String log){
        Log.i("MainActivity", log);
    }
}
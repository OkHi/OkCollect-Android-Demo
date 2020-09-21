package com.journey.okcollectandemo;

import android.app.Application;

import com.parse.Parse;

import static com.journey.okcollectandemo.Secret.DEV_APPLICATION_ID;
import static com.journey.okcollectandemo.Secret.DEV_CLIENT_ID;
import static com.journey.okcollectandemo.Secret.PROD_APPLICATION_ID_EXT;
import static com.journey.okcollectandemo.Secret.PROD_APPLICATION_ID_INT;
import static com.journey.okcollectandemo.Secret.PROD_CLIENT_ID_EXT;
import static com.journey.okcollectandemo.Secret.PROD_CLIENT_ID_INT;
import static com.journey.okcollectandemo.Secret.SANDBOX_APPLICATION_ID;
import static com.journey.okcollectandemo.Secret.SANDBOX_CLIENT_ID;
import static com.journey.okcollectandemo.utilities.Constants.DEVMASTER;
import static com.journey.okcollectandemo.utilities.Constants.EXTERNAL;
import static com.journey.okcollectandemo.utilities.Constants.SANDBOX;
import static com.journey.okcollectandemo.utilities.Constants.productionVersion;

public class OkCollectApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (productionVersion) {
            if (EXTERNAL) {
                Parse.initialize(new Parse.Configuration.Builder(this)
                        .applicationId(PROD_APPLICATION_ID_EXT)
                        .clientKey(PROD_CLIENT_ID_EXT)
                        .server("https://parseapi.back4app.com/").enableLocalDataStore().build());

            } else {
                Parse.initialize(new Parse.Configuration.Builder(this)
                        .applicationId(PROD_APPLICATION_ID_INT)
                        .clientKey(PROD_CLIENT_ID_INT)
                        .server("https://parseapi.back4app.com/").enableLocalDataStore().build());
            }
        } else if (SANDBOX) {
            Parse.initialize(new Parse.Configuration.Builder(this)
                    .applicationId(SANDBOX_APPLICATION_ID)
                    .clientKey(SANDBOX_CLIENT_ID)
                    .server("https://parseapi.back4app.com/").enableLocalDataStore().build());
        } else if (DEVMASTER) {
            Parse.initialize(new Parse.Configuration.Builder(this)
                    .applicationId(DEV_APPLICATION_ID)
                    .clientKey(DEV_CLIENT_ID)
                    .server("https://parseapi.back4app.com/").enableLocalDataStore().build());
        }
    }
}

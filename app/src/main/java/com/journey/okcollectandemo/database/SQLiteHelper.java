package com.journey.okcollectandemo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.journey.okcollectandemo.utilities.Constants.COLUMN_ACCURACY;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_CLAIMUALID;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_CUSTOMERNAME;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_DIRECTION;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_DISPLAYTITLE;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_FIRSTNAME;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_ID;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_LASTNAME;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_LAT;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_LNG;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_PHONECUSTOMER;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_PHOTO;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_PLACEID;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_PROPERTY;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_PROPERTYNAME;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_PROPERTYNUMBER;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_STREETNAME;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_SUBTITLE;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_TITLE;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_URL;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_USERID;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_USERNAME;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_VALUE;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_VERIFIED;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_WATCHERROR;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_WATCHING;
import static com.journey.okcollectandemo.utilities.Constants.TABLE_NAME_ADDRESSES;

/**
 * Created by ramogiochola on 6/18/16.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "com.journey.okcollectandemo.database.okverify.db";
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_CREATE_ADDRESSES =
            "create table " + TABLE_NAME_ADDRESSES + " (" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_CUSTOMERNAME + " VARCHAR, " +
                    COLUMN_PHONECUSTOMER + " VARCHAR, " +
                    COLUMN_CLAIMUALID + " VARCHAR, " +
                    COLUMN_DIRECTION + " VARCHAR, " +
                    COLUMN_PROPERTYNAME + " VARCHAR, " +
                    COLUMN_PROPERTYNUMBER + " VARCHAR, " +
                    COLUMN_LAT + " REAL, " +
                    COLUMN_LNG + " REAL, " +
                    COLUMN_ACCURACY + " VARCHAR, " +
                    COLUMN_USERID + " VARCHAR, " +
                    COLUMN_FIRSTNAME + " VARCHAR, " +
                    COLUMN_LASTNAME + " VARCHAR, " +
                    COLUMN_VERIFIED + " INTEGER, " +
                    COLUMN_USERNAME + " VARCHAR, " +
                    COLUMN_PLACEID + " VARCHAR, " +
                    COLUMN_URL + " VARCHAR, " +
                    COLUMN_PHOTO + " VARCHAR, " +
                    COLUMN_TITLE + " VARCHAR, " +
                    COLUMN_SUBTITLE + " VARCHAR, " +
                    COLUMN_DISPLAYTITLE + " VARCHAR, " +
                    COLUMN_STREETNAME + " VARCHAR, " +
                    COLUMN_WATCHING + " VARCHAR, " +
                    COLUMN_WATCHERROR + " VARCHAR, " +
                    " UNIQUE(" + COLUMN_CLAIMUALID + ") ON CONFLICT REPLACE);";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_ADDRESSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ADDRESSES);
        onCreate(db);
    }
}

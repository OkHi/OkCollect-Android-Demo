package com.journey.okcollectandemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.journey.okcollectandemo.datamodel.AddressItem;

import java.util.ArrayList;
import java.util.List;

import static com.journey.okcollectandemo.utilities.Constants.COLUMN_CLAIMUALID;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_LAT;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_LNG;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_WATCHERROR;
import static com.journey.okcollectandemo.utilities.Constants.COLUMN_WATCHING;
import static com.journey.okcollectandemo.utilities.Constants.TABLE_NAME_ADDRESSES;

/**
 * Created by ramogiochola on 6/18/16.
 */
public class DataProvider {
    private static final String TAG = "DataProvider";
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public DataProvider(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void openRead() throws SQLException {
        database = dbHelper.getReadableDatabase();
    }

    public void openWrite() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertAddressList(ContentValues values) {
        displayLog(" insertAddressList  with values method called");
        long insertId = 0;
        try {
            try {
                openWrite();
            } catch (Exception e) {
                displayLog("insertAddressList openWrite error " + e.toString());
            }
            displayLog("after write before insert");
            insertId = database.insert(TABLE_NAME_ADDRESSES, null, values);
            displayLog("insertAddressList method executed " + insertId);

        } catch (SQLException sqle) {
            displayLog(" insertAddressList error " + sqle.toString());
        } finally {
            close();
        }
        return insertId;
    }

    public void deleteAddress(String ualId) {
        displayLog("deleteAddress() method called");
        String selection = COLUMN_CLAIMUALID + " = ? ";
        String[] selectionArgs = {"" + ualId};
        try {
            try {
                openWrite();
            } catch (Exception e) {
                displayLog("deleteAddress openWrite error " + e.toString());
            }
            database.delete(TABLE_NAME_ADDRESSES, selection, selectionArgs);
            displayLog("deleteAddress() method executed");
        } catch (SQLException sqle) {
            displayLog(" deleteAddress error " + sqle.toString());
        } finally {
            close();
        }
    }

    public void deleteAllAddresseses() {
        displayLog("deleteAllAddresseses() method called");
        try {
            try {
                openWrite();
            } catch (Exception e) {
                displayLog("deleteAllAddresseses openWrite error " + e.toString());
            }
            database.delete(TABLE_NAME_ADDRESSES, null, null);
            displayLog("deleteAllAddresseses() method executed");
        } catch (SQLException sqle) {
            displayLog(" deleteAllAddresseses error " + sqle.toString());
        } finally {
            close();
        }
    }


    public int updateAddress(ContentValues values, String update) {
        int rowsaffected = 0;
        String selection = COLUMN_CLAIMUALID + " = ? ";
        String[] selectionArgs = {update};
        try {
            try {
                openWrite();
            } catch (Exception e) {
                displayLog("updateUal openWrite error " + e.toString());
            }
            rowsaffected = database.update(TABLE_NAME_ADDRESSES, values, selection, selectionArgs);
            displayLog(" updateUal status updated " + rowsaffected);

        } catch (SQLException sqle) {
            displayLog("updateUal error " + sqle.toString());
        } finally {
            close();
        }
        return rowsaffected;
    }

    private void displayLog(String log) {
        Log.i(TAG, log);
    }
}

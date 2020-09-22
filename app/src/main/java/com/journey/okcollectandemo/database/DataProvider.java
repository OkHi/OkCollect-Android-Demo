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

    public List<AddressItem> getAddresses() {
        List<AddressItem> addressItems = new ArrayList<>();
        Cursor cursor;
        try {
            try {
                openRead();
            } catch (Exception e) {
                displayLog("getGeofences openRead error " + e.toString());
            }
            cursor = database.query(TABLE_NAME_ADDRESSES, null, null, null,
                    null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                AddressItem addressItem = new AddressItem();
                addressItem.setCustomername(cursor.getString(1));
                addressItem.setPhonecustomer(cursor.getString(2));
                addressItem.setUalid(cursor.getString(3));
                addressItem.setDirection(cursor.getString(4));
                addressItem.setPropname(cursor.getString(5));
                addressItem.setPropnumber(cursor.getString(6));
                addressItem.setLat(cursor.getDouble(7));
                addressItem.setLng(cursor.getDouble(8));
                addressItem.setAcc(cursor.getDouble(9));
                addressItem.setUserId(cursor.getString(10));
                addressItem.setFirstname(cursor.getString(11));
                addressItem.setLastname(cursor.getString(12));
                addressItem.setPlaceId(cursor.getString(13));
                addressItem.setUrl(cursor.getString(14));
                addressItem.setPhoto(cursor.getString(15));
                addressItem.setTitle(cursor.getString(16));
                addressItem.setSubtitle(cursor.getString(17));
                addressItem.setDisplaytitle(cursor.getString(18));
                addressItem.setStreetName(cursor.getString(19));
                addressItems.add(addressItem);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (SQLException sqle) {
            displayLog("get geofences error " + sqle.toString());
        } finally {
            close();
        }
        return addressItems;
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

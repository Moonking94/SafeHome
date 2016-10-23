package com.fyp.imranabdulhadi.safehome;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Imran Abdulhadi on 10/23/2016.
 */

public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "SafeHome";

    // Database table name
    private static final String TABLE_ADDRESS = "raspberrypi_address";

    // Table columns name
    private static final String KEY_ID = "address_id";
    private static final String KEY_ADDRESS = "address_ip";
    private static final String KEY_DESC = "address_desc";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ADDRESS + "(" + KEY_ID + "INTEGER PRIMARY KEY," + KEY_ADDRESS
                + " TEXT," + KEY_DESC + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS);
        // Recreate the table again
        onCreate(db);
    }

    /**
     * Add new address
     * @param address
     */
    public void addAddress(Address address) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, address.getAddressIp());
        values.put(KEY_DESC, address.getAddressDesc());

        // Insert row
        db.insert(TABLE_ADDRESS, null, values);
        db.close();
    }

    /**
     * Get only 1 value of address
     * @param
     * @return
     */
    public Address getAddress(String addressIP) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ADDRESS, new String[]{KEY_ID, KEY_ADDRESS, KEY_DESC}, KEY_ADDRESS + " = ?",
                new String[]{String.valueOf(addressIP)}, null, null, null, null);
        if(cursor!=null)
            cursor.moveToFirst();
        Address address = new Address(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));

        cursor.close();
        return address;
    }

    /**
     *
     * @return
     */
    public List<Address> getAllAddress() {
        List<Address> addressList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ADDRESS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Address address = new Address();
                address.setAddressId(Integer.parseInt(cursor.getString(0)));
                address.setAddressIp(cursor.getString(1));
                address.setAddressDesc(cursor.getString(2));

                addressList.add(address);
            } while(cursor.moveToNext());
        }

        cursor.close();
        return addressList;
    }

    /**
     *
     * @return
     */
    public int getAddressCount() {
        String countQuery = "SELECT * FROM " + TABLE_ADDRESS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    /**
     *
     * @param address
     * @return
     */
    public int updateAddress(Address address) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, address.getAddressIp());
        values.put(KEY_DESC, address.getAddressDesc());

        return db.update(TABLE_ADDRESS, values, KEY_ID + " = ?", new String[]{String.valueOf(address.getAddressId())});
    }

    /**
     *
     * @param address
     */
    public void deleteAddress(Address address) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ADDRESS, KEY_ID + " = ?", new String[]{String.valueOf(address.getAddressId())});
        db.close();
    }
}

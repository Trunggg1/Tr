package com.example.assessment2app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseSiteHelper extends SQLiteOpenHelper {

    // Database Name and Version
    private static final String DATABASE_NAME = "BloodDonationApp.db";
    private static final int DATABASE_VERSION = 3; // Updated version for enhancements

    // Table: Donation Sites
    private static final String TABLE_SITES = "donation_sites";
    private static final String COLUMN_SITE_ID = "id";
    private static final String COLUMN_SITE_ADDRESS = "site_address";
    private static final String COLUMN_DONATION_HOURS = "donation_hours";
    private static final String COLUMN_REQUIRED_BLOOD_TYPES = "required_blood_types";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    public DatabaseSiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SITES_TABLE = "CREATE TABLE " + TABLE_SITES + "("
                + COLUMN_SITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SITE_ADDRESS + " TEXT, "
                + COLUMN_DONATION_HOURS + " TEXT, "
                + COLUMN_REQUIRED_BLOOD_TYPES + " TEXT, "
                + COLUMN_LATITUDE + " REAL, "
                + COLUMN_LONGITUDE + " REAL)";
        db.execSQL(CREATE_SITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_SITES + " ADD COLUMN " + COLUMN_LATITUDE + " REAL");
            db.execSQL("ALTER TABLE " + TABLE_SITES + " ADD COLUMN " + COLUMN_LONGITUDE + " REAL");
        }
    }

    // Insert a new donation site
    public boolean insertSite(String address, String hours, String bloodTypes, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SITE_ADDRESS, address);
        values.put(COLUMN_DONATION_HOURS, hours);
        values.put(COLUMN_REQUIRED_BLOOD_TYPES, bloodTypes);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);

        long result = db.insert(TABLE_SITES, null, values);
        db.close();
        return result != -1;
    }

    // Retrieve all donation sites
    public Cursor getAllSites() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SITES, null);
    }

    // Retrieve a specific site by ID
    public Cursor getSiteById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SITES + " WHERE " + COLUMN_SITE_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Update a donation site
    public boolean updateSite(int id, String address, String hours, String bloodTypes, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SITE_ADDRESS, address);
        values.put(COLUMN_DONATION_HOURS, hours);
        values.put(COLUMN_REQUIRED_BLOOD_TYPES, bloodTypes);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);

        int rowsUpdated = db.update(TABLE_SITES, values, COLUMN_SITE_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsUpdated > 0;
    }

    // Delete a donation site
    public boolean deleteSite(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_SITES, COLUMN_SITE_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }
}

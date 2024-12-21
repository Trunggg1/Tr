package com.example.assessment2app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseDonorHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "donors.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "donors";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_BLOOD_TYPE = "blood_type";

    public DatabaseDonorHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_BLOOD_TYPE + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addDonor(String name, String bloodType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_BLOOD_TYPE, bloodType);
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }

    public List<String> getAllDonors() {
        List<String> donors = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_NAME, COLUMN_BLOOD_TYPE},
                null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String bloodType = cursor.getString(1);
                donors.add(name + " - " + bloodType);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return donors;
    }

    public boolean removeDonor(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_NAME + "=?", new String[]{name});
        db.close();
        return result > 0; // Return true if the donor was successfully removed
    }

}

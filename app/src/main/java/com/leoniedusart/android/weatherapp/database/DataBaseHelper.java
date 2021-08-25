package com.leoniedusart.android.weatherapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.leoniedusart.android.weatherapp.models.City;
import com.leoniedusart.android.weatherapp.models.DbCity;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MyWeatherDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CITY = "City";
    private static final String KEY_ID = "city_id";
    private static final String KEY_API_ID = "city_api_id";
    private static final String KEY_NAME = "city_name";
    private static final String CREATE_TABLE_CITY = "CREATE TABLE "
            + TABLE_CITY
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_API_ID + " INTEGER UNIQUE,"
            + KEY_NAME + " TEXT"
            + ")";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);
        onCreate(sqLiteDatabase);
    }

    public long insert(City city)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_API_ID, city.getmApiID());
        values.put(KEY_NAME, city.getmName());

        long newRowID = db.insert(TABLE_CITY, null, values);
        db.close();

        return newRowID;
    }

    public void delete(int apiId)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CITY, KEY_API_ID + " = " + apiId , null);
        db.close();
    }

    public boolean cityExists(int apiId)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CITY + " WHERE " + KEY_API_ID + " = " + apiId, null);
        boolean cityExists = cursor.moveToFirst();
        cursor.close();
        //db.close();
        return cityExists;
    }

    public ArrayList<DbCity> findAll()
    {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<DbCity> cities = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CITY, null);
        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                DbCity city = new DbCity(cursor.getLong(cursor.getColumnIndex(KEY_ID)), cursor.getInt(cursor.getColumnIndex(KEY_API_ID)), cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                cities.add(city);
                cursor.moveToNext();
            }
        }
        cursor.close();
        //db.close();

        return cities;
    }
}

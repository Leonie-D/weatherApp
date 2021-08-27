package com.leoniedusart.android.weatherapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.leoniedusart.android.weatherapp.models.City;
import com.leoniedusart.android.weatherapp.models.DbCity;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MyWeatherDB";
    private static final int DATABASE_VERSION = 8;
    private static final String TABLE_CITY = "City";
    private static final String KEY_ID = "city_id";
    private static final String KEY_API_ID = "city_api_id";
    private static final String KEY_NAME = "city_name";
    private static final String KEY_ORDER = "city_order";
    private static final String CREATE_TABLE_CITY = "CREATE TABLE "
            + TABLE_CITY
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_API_ID + " INTEGER UNIQUE,"
            + KEY_NAME + " TEXT,"
            + KEY_ORDER + " INTEGER"
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

    public long insert(City city, int order)
    {
        SQLiteDatabase db = getWritableDatabase();

        // +1 sur tous les order supp
        Cursor cursor = db.rawQuery("SELECT " + KEY_ID + ", " + KEY_ORDER + " FROM " + TABLE_CITY + " WHERE " + KEY_ORDER + " >= " + order, null);
        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                int cityId = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                int newOrder = cursor.getInt(cursor.getColumnIndex(KEY_ORDER)) + 1;
                ContentValues values = new ContentValues();
                values.put(KEY_ORDER, newOrder);
                db.update(TABLE_CITY, values, KEY_ID + " = " + cityId, null);
                cursor.moveToNext();
            }
        }

        // insert
        ContentValues values = new ContentValues();
        values.put(KEY_API_ID, city.getmApiID());
        values.put(KEY_NAME, city.getmName());
        values.put(KEY_ORDER, order);

        long newRowID = db.insert(TABLE_CITY, null, values);
        db.close();

        return newRowID;
    }

    public void delete(int apiId)
    {
        SQLiteDatabase db = getWritableDatabase();

        // -1 sur tous les order supp
        Cursor cursor = db.rawQuery("SELECT " + KEY_ID + ", " + KEY_ORDER + " FROM " + TABLE_CITY + " WHERE " + KEY_ORDER + " > (SELECT " + KEY_ORDER + " FROM " + TABLE_CITY + " WHERE " + KEY_API_ID + " = " + apiId + ")" , null);
        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                int cityId = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                int newOrder = cursor.getInt(cursor.getColumnIndex(KEY_ORDER)) - 1;
                ContentValues values = new ContentValues();
                values.put(KEY_ORDER, newOrder);
                db.update(TABLE_CITY, values, KEY_ID + " = " + cityId, null);
                cursor.moveToNext();
            }
        }

        db.delete(TABLE_CITY, KEY_API_ID + " = " + apiId , null);
        //db.close();
    }

    public int cityExists(int apiId)
    {
        int cityOrder = -1; // init to invalid value
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CITY + " WHERE " + KEY_API_ID + " = " + apiId, null);
        if(cursor.moveToFirst())
        {
            cityOrder = cursor.getInt(cursor.getColumnIndex(KEY_ORDER));
        }
        cursor.close();
        //db.close();
        return cityOrder;
    }

    public ArrayList<DbCity> findAll()
    {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<DbCity> cities = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CITY + " ORDER BY " + KEY_ORDER + " ASC", null);
        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                DbCity city = new DbCity(cursor.getLong(cursor.getColumnIndex(KEY_ID)), cursor.getInt(cursor.getColumnIndex(KEY_API_ID)), cursor.getString(cursor.getColumnIndex(KEY_NAME)), cursor.getInt(cursor.getColumnIndex(KEY_ORDER)));
                cities.add(city);
                cursor.moveToNext();
            }
        }
        cursor.close();
        //db.close();

        return cities;
    }

    public void swap(int cityOrder1, int cityOrder2)
    {
        Log.d("LDtag", String.valueOf(cityOrder1));
        Log.d("LDtag", String.valueOf(cityOrder2));

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + KEY_ID + " FROM " + TABLE_CITY + " WHERE " + KEY_ORDER + " = " + cityOrder1, null);
        if(cursor.moveToFirst())
        {
            int city1Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));

            if(cityOrder1 < cityOrder2) {
                Cursor cursor2 = db.rawQuery("SELECT " + KEY_ID + ", " + KEY_ORDER + " FROM " + TABLE_CITY + " WHERE " + KEY_ORDER + " <= " + cityOrder2 + " AND " + KEY_ORDER + " > " + cityOrder1 + " AND " + KEY_ID + " != " + city1Id, null);

                if(cursor2.moveToFirst())
                {
                    while(!cursor2.isAfterLast())
                    {
                        int cityId = cursor2.getInt(cursor2.getColumnIndex(KEY_ID));
                        int newOrder = cursor2.getInt(cursor2.getColumnIndex(KEY_ORDER)) - 1;
                        ContentValues values = new ContentValues();
                        values.put(KEY_ORDER, newOrder);
                        db.update(TABLE_CITY, values, KEY_ID + " = " + cityId, null);
                        cursor2.moveToNext();
                    }
                }
            }
            else
            {
                Cursor cursor2 = db.rawQuery("SELECT " + KEY_ID + ", " + KEY_ORDER + " FROM " + TABLE_CITY + " WHERE " + KEY_ORDER + " >= " + cityOrder2 + " AND " + KEY_ORDER + " < " + cityOrder1 + " AND " + KEY_ID + " != " + city1Id, null);

                if(cursor2.moveToFirst())
                {
                    while(!cursor2.isAfterLast())
                    {
                        int cityId = cursor2.getInt(cursor2.getColumnIndex(KEY_ID));
                        int newOrder = cursor2.getInt(cursor2.getColumnIndex(KEY_ORDER)) + 1;
                        ContentValues values = new ContentValues();
                        values.put(KEY_ORDER, newOrder);
                        db.update(TABLE_CITY, values, KEY_ID + " = " + cityId, null);
                        cursor2.moveToNext();
                    }
                }
            }

            ContentValues values = new ContentValues();
            values.put(KEY_ORDER, cityOrder2);
            db.update(TABLE_CITY, values, KEY_ID + " = " + city1Id, null);
        }

        db.close();
    }
}

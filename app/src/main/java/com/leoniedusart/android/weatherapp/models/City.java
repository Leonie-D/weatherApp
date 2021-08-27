package com.leoniedusart.android.weatherapp.models;

import com.leoniedusart.android.weatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class City {
    private final int mApiID;
    private final String mName;
    private final String mDesc;
    private final String mTemp;
    private final Double mLat;
    private final Double mLon;
    private final int mWeatherIcon;
    private final ArrayList<Weather> mForecast = new ArrayList<>();

    public City(int mApiId, String mName, String mDesc, String mTemp, Double mLat, Double mLon, int mWeatherIcon) {
        this.mApiID = mApiId;
        this.mName = mName;
        this.mDesc = mDesc;
        this.mTemp = mTemp;
        this.mLat = mLat;
        this.mLon = mLon;
        this.mWeatherIcon = mWeatherIcon;
    }

    public City(String stringJson) throws JSONException {
        JSONObject json = new JSONObject(stringJson);
        mApiID = json.getInt("id");
        mName = json.getString("name");
        mDesc = json.getJSONArray("weather").getJSONObject(0).getString("description");
        mTemp = String.format("%d°C", Math.round(json.getJSONObject("main").getDouble("temp")));
        mLat = json.getJSONObject("coord").getDouble("lat");
        mLon = json.getJSONObject("coord").getDouble("lon");
        switch (json.getJSONArray("weather").getJSONObject(0).getString("main")) {
            case "Clouds":
                if(mDesc.equals("peu nuageux")) {
                    mWeatherIcon = R.drawable.ic_cloudy;
                } else {
                    mWeatherIcon = R.drawable.ic_cloud;
                }
                break;
            case "Rain":
                mWeatherIcon = R.drawable.ic_rainy;
                break;
            case "Clear":
            default:
                mWeatherIcon = R.drawable.ic_sun;
        }
    }

    public City(String stringJson, boolean withForecasting) throws JSONException {
        JSONObject json = new JSONObject(stringJson);
        mName = json.getJSONObject("city").getString("name");
        mApiID= json.getJSONObject("city").getInt("id");
        mDesc = "";
        mTemp = "";
        mLat = json.getJSONObject("city").getJSONObject("coord").getDouble("lat");
        mLon = json.getJSONObject("city").getJSONObject("coord").getDouble("lon");
        mWeatherIcon = 0;
        JSONArray forecast = json.getJSONArray("list");
        for(int i = 0; i < forecast.length(); i++)
        {
            mForecast.add(new Weather(forecast.getJSONObject(i)));
        }
    }

    public int getmApiID() {
        return mApiID;
    }

    public String getmName() {
        return mName;
    }

    public String getmDesc() {
        return mDesc;
    }

    public String getmTemp() {
        return mTemp;
    }

    public int getmWeatherIcon() {
        return mWeatherIcon;
    }

    public Double getmLat() {
        return mLat;
    }

    public Double getmLon() {
        return mLon;
    }

    public ArrayList<Weather> getmForecast() {
        return mForecast;
    }
}

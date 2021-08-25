package com.leoniedusart.android.weatherapp.models;

import com.leoniedusart.android.weatherapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class City {
    private final int mApiID;
    private final String mName;
    private final String mDesc;
    private final String mTemp;
    private final int mWeatherIcon;

    public City(int mApiId, String mName, String mDesc, String mTemp, int mWeatherIcon) {
        this.mApiID = mApiId;
        this.mName = mName;
        this.mDesc = mDesc;
        this.mTemp = mTemp;
        this.mWeatherIcon = mWeatherIcon;
    }

    public City(String stringJson) throws JSONException {
        JSONObject json = new JSONObject(stringJson);
        mApiID = json.getInt("id");
        mName = json.getString("name");
        mDesc = json.getJSONArray("weather").getJSONObject(0).getString("description");
        mTemp = String.format("%d°C", Math.round(json.getJSONObject("main").getDouble("temp")));
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
}

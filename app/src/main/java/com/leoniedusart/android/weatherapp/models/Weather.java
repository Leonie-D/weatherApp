package com.leoniedusart.android.weatherapp.models;

import com.leoniedusart.android.weatherapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Weather {
    private final String mDesc;
    private final String mTemp;
    private final int mWeatherIcon;

    public Weather(JSONObject jsonObject) throws JSONException
    {
        mDesc = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
        mTemp = String.format("%d°C", Math.round(jsonObject.getJSONObject("temp").getDouble("day")));
        switch (jsonObject.getJSONArray("weather").getJSONObject(0).getString("main")) {
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

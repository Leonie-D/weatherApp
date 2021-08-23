package com.leoniedusart.android.weatherapp.models;

public class City {
    private final String mName;
    private final String mDesc;
    private final String mTemp;
    private final int mWeatherIcon;

    public City(String mName, String mDesc, String mTemp, int mWeatherIcon) {
        this.mName = mName;
        this.mDesc = mDesc;
        this.mTemp = mTemp;
        this.mWeatherIcon = mWeatherIcon;
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

package com.leoniedusart.android.weatherapp.models;

public class DbCity {
    private final long mIdDb;
    private final int mIdApi;
    private final String mName;

    public DbCity(long mIdDb, int mIdApi, String mName) {
        this.mIdDb = mIdDb;
        this.mIdApi = mIdApi;
        this.mName = mName;
    }

    public long getmIdDb() {
        return mIdDb;
    }

    public int getmIdApi() {
        return mIdApi;
    }

    public String getmName() {
        return mName;
    }
}

package com.leoniedusart.android.weatherapp.models;

public class DbCity {
    private final long mIdDb;
    private final int mIdApi;
    private final String mName;
    private final int mOrder;

    public DbCity(long mIdDb, int mIdApi, String mName, int mOrder) {
        this.mIdDb = mIdDb;
        this.mIdApi = mIdApi;
        this.mName = mName;
        this.mOrder = mOrder;
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

    public int getmOrder() {
        return mOrder;
    }
}

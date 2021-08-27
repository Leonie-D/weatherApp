package com.leoniedusart.android.weatherapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.leoniedusart.android.weatherapp.R;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public interface CityAPI {
    OkHttpClient mOkHttpClient = new OkHttpClient();
    Handler handler = new Handler();
    int apiKey = R.string.weather_api_key;
    String lang = Locale.getDefault().getLanguage();

    default String getUrl(Context context, int cityId) {
        return String.format("https://api.openweathermap.org/data/2.5/weather?id=%d&units=metric&lang=%s&appid=%s", cityId, lang, context.getResources().getString(apiKey));
    }

    default String getUrl(Context context, String cityName) {
        return String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=%s&appid=%s", cityName, lang, context.getResources().getString(apiKey));
    }

    default String getUrl(Context context, Double cityLat, Double cityLon) {
        return String.format("https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=metric&lang=%s&appid=%s", cityLat, cityLon, lang, context.getResources().getString(apiKey));
    }

    default void apiCall(Context context, String url, boolean init) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected) {
            Request request = new Request.Builder().url(url).build();
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    alertUser(context, R.string.pb);
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()) {
                        final String stringJson = response.body().string();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onSuccess(stringJson, init);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                alertUser(context, R.string.notfound);
                            }
                        });
                    }
                }
            });
        } else {
            alertUser(context, R.string.no_internet);
        }
    }

    default void alertUser(Context context, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message);
        builder.setPositiveButton(R.string.ok, null);
        builder.create().show();
    }

    void onSuccess(String stringJson, boolean init);
}

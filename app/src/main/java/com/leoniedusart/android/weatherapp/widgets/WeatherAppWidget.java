package com.leoniedusart.android.weatherapp.widgets;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.leoniedusart.android.weatherapp.R;
import com.leoniedusart.android.weatherapp.activities.MainActivity;
import com.leoniedusart.android.weatherapp.models.City;
import com.leoniedusart.android.weatherapp.utils.CityAPI;

import org.json.JSONException;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherAppWidget extends AppWidgetProvider implements CityAPI {
    private Context mContext;
    private int[] mAppWidgetIds;
    private AppWidgetManager mAppWidgetManager;
    private LocationManager mLocationManager;
    private static LocationListener mLocationListener;
    private static final int REQUEST_CODE = 30;
    private static City currentCity;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_app_widget);

        ConnectivityManager connMng = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMng.getActiveNetworkInfo();
        // Si accès internet, on cherche à actualiser la géoloc pour appeler l'API météo
        if (networkInfo != null && networkInfo.isConnected()) {
            if(currentCity != null)
            {
                views.setViewVisibility(R.id.widget_progress_bar, View.INVISIBLE);
                views.setTextViewText(R.id.widget_text_view_city_name, currentCity.getmName());
                views.setTextViewText(R.id.widget_text_view_city_desc, currentCity.getmDesc());
                views.setTextViewText(R.id.widget_text_view_city_temp, currentCity.getmTemp());
                views.setImageViewResource(R.id.widget_image_view_icon, currentCity.getmWeatherIcon());
            }
            views.setViewVisibility(R.id.image_view_refresh_btn, View.INVISIBLE);
        }
        else
        {
            views.setTextViewText(R.id.text_view_city_name, context.getResources().getString(R.string.no_connection));
            views.setViewVisibility(R.id.image_view_refresh_btn, View.VISIBLE);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_main, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mContext = context;
        mAppWidgetIds = appWidgetIds;
        mAppWidgetManager = appWidgetManager;

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Double lat = location.getLatitude();
                Double lon = location.getLongitude();

                // si les coordonnées sont mise à jour, appel API pour actualiser météo locale
                apiCall(context, getUrl(context, lat, lon), false);

                mLocationManager.removeUpdates(this);
            }
        };

        getLocation();

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private void getLocation()
    {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // intent vers la main activity (pour demander les permissions)
            // voir : start activity for result pour que le widget enchaine sur le traitement
        } else {
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            }
            else
            {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            }
        }
    }

    @Override
    public void onSuccess(String stringJson, boolean init) {
        try {
            currentCity = new City(stringJson);
            for (int appWidgetId : mAppWidgetIds) {
                updateAppWidget(mContext, mAppWidgetManager, appWidgetId);
            }
        } catch (JSONException e) {
            alertUser(mContext, R.string.pb);
        }
    }
}
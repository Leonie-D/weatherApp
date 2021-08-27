package com.leoniedusart.android.weatherapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leoniedusart.android.weatherapp.R;
import com.leoniedusart.android.weatherapp.models.City;
import com.leoniedusart.android.weatherapp.models.Weather;
import com.leoniedusart.android.weatherapp.utils.CityAPI;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements CityAPI {

    private Context mContext;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearLayoutMain;
    private TextView mTextViewCityName;
    private TextView mTextViewCityDesc;
    private TextView mTextViewCityTemp;
    private ImageView mImageViewCityIcon;
    private ImageView mImageViewRefreshBtn;
    private City mCity;
    private static double mLat;
    private static double mLon;
    private static final int REQUEST_CODE = 30;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mLinearLayoutMain = findViewById(R.id.linear_layout_main);
        mImageViewRefreshBtn = findViewById(R.id.image_view_refresh_btn);
        mProgressBar = findViewById(R.id.indeterminateBar);

        ConnectivityManager connMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMng.getActiveNetworkInfo();

        // Si accès internet, on cherche à actualiser la géoloc pour appeler l'API météo
        if (networkInfo != null && networkInfo.isConnected()) {
            mImageViewRefreshBtn.setVisibility(View.INVISIBLE);
            mLinearLayoutMain.setVisibility(View.VISIBLE);

            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    mLat = location.getLatitude();
                    mLon = location.getLongitude();

                    // si les coordonnées sont mise à jour, appel API pour actualiser météo locale
                    apiCall(mContext, getUrl(mContext, mLat, mLon), true);

                    mLocationManager.removeUpdates(this);
                }
            };
            getLocation();
        }
        else
        {
            mImageViewRefreshBtn.setVisibility(View.VISIBLE);
            mLinearLayoutMain.setVisibility(View.INVISIBLE);
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(mContext, R.string.permission_missing, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_location:
                if(mCity != null){
                    Intent intent = new Intent(mContext, MapsActivity.class);
                    intent.putExtra("cityName", mCity.getmName());
                    intent.putExtra("cityLat", mLat);
                    intent.putExtra("cityLon", mLon);
                    mContext.startActivity(intent);
                }
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onFavouritesBtnClick(View view)
    {
        Intent intent = new Intent(this, FavouritesActivity.class);
        startActivity(intent);
    }

    public void onRefreshBtnClick(View view)
    {
        this.recreate();
    }

    @Override
    public void onSuccess(String stringJson, boolean init) {
        try {
            if(init)
            {
                City city = new City(stringJson);
                renderCurrentWeather(city);
                Log.d("LDtag", String.valueOf(city.getmApiID()));
                apiCall(mContext, getUrl(mContext, city.getmApiID(), true), false);
            }
            else
            {
                City city = new City(stringJson, true);
                renderForecasting(city);
            }
        } catch (JSONException e) {
            alertUser(mContext, R.string.pb);
        }
    }

    /**
     * Si permissions activées, requiert la mise à jour de la localisation
     */
    private void getLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

    /**
     * Mise à jour de l'ui
     * @param city
     */
    private void renderCurrentWeather(City city)
    {
        mCity = city;
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextViewCityName = findViewById(R.id.text_view_city_name);
        mTextViewCityName.setText(city.getmName());
        mTextViewCityDesc = findViewById(R.id.text_view_city_desc);
        mTextViewCityDesc.setText(city.getmDesc());
        mTextViewCityTemp = findViewById(R.id.text_view_city_temp);
        mTextViewCityTemp.setText(city.getmTemp());
        mImageViewCityIcon = findViewById(R.id.image_view_icon);
        Drawable icon = ResourcesCompat.getDrawable(mContext.getResources(), city.getmWeatherIcon(), mContext.getTheme());
        mImageViewCityIcon.setImageDrawable(icon);
    }

    private void renderForecasting(City city)
    {
        ((TextView) findViewById(R.id.text_view_city_desc_f1)).setText(city.getmForecast().get(0).getmDesc());
        ((TextView) findViewById(R.id.text_view_city_desc_f2)).setText(city.getmForecast().get(1).getmDesc());
        ((TextView) findViewById(R.id.text_view_city_desc_f3)).setText(city.getmForecast().get(2).getmDesc());
        ((TextView) findViewById(R.id.text_view_city_desc_f4)).setText(city.getmForecast().get(3).getmDesc());

        ((TextView) findViewById(R.id.text_view_city_temp_f1)).setText(city.getmForecast().get(0).getmTemp());
        ((TextView) findViewById(R.id.text_view_city_temp_f2)).setText(city.getmForecast().get(1).getmTemp());
        ((TextView) findViewById(R.id.text_view_city_temp_f3)).setText(city.getmForecast().get(2).getmTemp());
        ((TextView) findViewById(R.id.text_view_city_temp_f4)).setText(city.getmForecast().get(3).getmTemp());
    }
}
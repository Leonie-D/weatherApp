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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leoniedusart.android.weatherapp.R;
import com.leoniedusart.android.weatherapp.models.City;
import com.leoniedusart.android.weatherapp.utils.CityAPI;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements CityAPI {

    private Context mContext;
    private LinearLayout mLinearLayoutMain;
    private TextView mTextViewCityName;
    private TextView mTextViewCityDesc;
    private TextView mTextViewCityTemp;
    private ImageView mImageViewCityIcon;
    private ImageView mImageViewRefreshBtn;
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

        ConnectivityManager connMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMng.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            mImageViewRefreshBtn.setVisibility(View.INVISIBLE);
            mLinearLayoutMain.setVisibility(View.VISIBLE);

            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Log.d("LDtag", "truc");
                    mLat = location.getLatitude();
                    mLon = location.getLongitude();

                    apiCall(mContext, getUrl(mLat, mLon), false);

                    mLocationManager.removeUpdates(this);
                }
            };
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            } else {
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            }

            Log.d("LDtag", String.valueOf(mLat));
            //apiCall(mContext, getUrl(mLat, mLon), false);
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
                    Log.d("LDtag", "plouf");
                } else {
                    Log.d("LDtag", "pouet");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
            City city = new City(stringJson);
            renderCurrentWeather(city);
        } catch (JSONException e) {
            alertUser(mContext, R.string.pb);
        }
    }

    private void renderCurrentWeather(City city)
    {
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
}
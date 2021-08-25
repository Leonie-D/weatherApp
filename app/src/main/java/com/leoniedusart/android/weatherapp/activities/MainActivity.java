package com.leoniedusart.android.weatherapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leoniedusart.android.weatherapp.R;
import com.leoniedusart.android.weatherapp.models.City;
import com.leoniedusart.android.weatherapp.utils.CityAPI;

import org.json.JSONException;

public class  MainActivity extends AppCompatActivity implements CityAPI {

    private Context mContext;
    private LinearLayout mLinearLayoutMain;
    private TextView mTextViewCityName;
    private TextView mTextViewCityDesc;
    private TextView mTextViewCityTemp;
    private ImageView mImageViewCityIcon;
    private ImageView mImageViewRefreshBtn;
    private static final double lat = 40.716709;
    private static final double lon = -74.005698;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        ConnectivityManager connMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMng.getActiveNetworkInfo();

        mLinearLayoutMain = findViewById(R.id.linear_layout_main);
        mImageViewRefreshBtn = findViewById(R.id.image_view_refresh_btn);

        if(networkInfo != null && networkInfo.isConnected())
        {
            mImageViewRefreshBtn.setVisibility(View.INVISIBLE);
            mLinearLayoutMain.setVisibility(View.VISIBLE);

            apiCall(mContext, getUrl(lat, lon), false);
        }
        else
        {
            mImageViewRefreshBtn.setVisibility(View.VISIBLE);
            mLinearLayoutMain.setVisibility(View.INVISIBLE);
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
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
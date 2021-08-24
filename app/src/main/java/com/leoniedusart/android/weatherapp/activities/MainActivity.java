package com.leoniedusart.android.weatherapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leoniedusart.android.weatherapp.R;
import com.leoniedusart.android.weatherapp.models.City;
import com.leoniedusart.android.weatherapp.utils.DataKeys;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private LinearLayout mLinearLayoutMain;
    private TextView mTextViewCityName;
    private TextView mTextViewCityDesc;
    private TextView mTextViewCityTemp;
    private ImageView mImageViewCityIcon;
    private ImageView mImageViewRefreshBtn;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;
    private static final double lat = 40.716709;
    private static final double lon = -74.005698;
    private static final String apikey = "0de3404ffb4014065996f62bf2434b39";

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

            mOkHttpClient = new OkHttpClient();
            mHandler = new Handler();
            Request request = new Request.Builder().url(String.format("http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=metric&lang=fr&appid=%s", lat, lon, apikey)).build();
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    alertUser(mContext, R.string.pb);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()) {
                        final String stringJson = response.body().string();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                renderCurrentWeather(stringJson);
                            }
                        });
                    } else {
                        alertUser(mContext, R.string.pb);
                    }
                }
            });
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

    private void alertUser(Context context, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create().show();
    }

    private void renderCurrentWeather(String stringJson)
    {
        try {
            City city = new City(stringJson);
            mTextViewCityName = findViewById(R.id.text_view_city_name);
            mTextViewCityName.setText(city.getmName());
            mTextViewCityDesc = findViewById(R.id.text_view_city_desc);
            mTextViewCityDesc.setText(city.getmDesc());
            mTextViewCityTemp = findViewById(R.id.text_view_city_temp);
            mTextViewCityTemp.setText(city.getmTemp());
            mImageViewCityIcon = findViewById(R.id.image_view_icon);
            Drawable icon = ResourcesCompat.getDrawable(mContext.getResources(), city.getmWeatherIcon(), mContext.getTheme());
            mImageViewCityIcon.setImageDrawable(icon);
        } catch (JSONException e) {
            alertUser(mContext, R.string.pb);
        }
    }
}
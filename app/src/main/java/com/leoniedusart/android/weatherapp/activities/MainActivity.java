package com.leoniedusart.android.weatherapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leoniedusart.android.weatherapp.R;
import com.leoniedusart.android.weatherapp.utils.DataKeys;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mLinearLayoutMain;
    private TextView mTextViewCityName;
    private ImageView mImageViewRefreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager connMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMng.getActiveNetworkInfo();

        mLinearLayoutMain = findViewById(R.id.linear_layout_main);
        mImageViewRefreshBtn = findViewById(R.id.image_view_refresh_btn);

        if(networkInfo != null && networkInfo.isConnected())
        {
            mImageViewRefreshBtn.setVisibility(View.INVISIBLE);
            mLinearLayoutMain.setVisibility(View.VISIBLE);
            mTextViewCityName = findViewById(R.id.text_view_city_name);
            mTextViewCityName.setText(R.string.city_name);
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
}
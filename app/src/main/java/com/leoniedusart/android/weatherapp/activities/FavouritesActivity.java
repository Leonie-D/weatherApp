package com.leoniedusart.android.weatherapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.leoniedusart.android.weatherapp.R;
import com.leoniedusart.android.weatherapp.adapters.FavouriteAdapter;
import com.leoniedusart.android.weatherapp.databinding.ActivityFavouritesBinding;
import com.leoniedusart.android.weatherapp.models.City;
import com.leoniedusart.android.weatherapp.utils.DataKeys;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FavouritesActivity extends AppCompatActivity {
    private Context mContext;
    private Toolbar toolbar;
    private CollapsingToolbarLayout toolBarLayout;
    private FloatingActionButton fab;
    private ArrayList<City> mCities;
    private RecyclerView mRecyclerViewFavourites;
    FavouriteAdapter mAdapter;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;
    private static final String apikey = "0de3404ffb4014065996f62bf2434b39";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        mContext = this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        mRecyclerViewFavourites = findViewById(R.id.recycler_view_favourites_list);

        mCities = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerViewFavourites.setLayoutManager(layoutManager);
        mAdapter = new FavouriteAdapter(mContext, mCities);
        mRecyclerViewFavourites.setAdapter(mAdapter);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Ajouter une ville");

                View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_add_favourite, null);
                final EditText editTextAddFavourite = (EditText) v.findViewById(R.id.edit_text_add_favourite);
                builder.setView(v);

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mOkHttpClient = new OkHttpClient();
                        mHandler = new Handler();
                        Request request = new Request.Builder().url(String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=fr&appid=%s", editTextAddFavourite.getText().toString(), apikey)).build();
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
                                            addFavourite(stringJson);
                                        }
                                    });
                                } else {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            alertUser(mContext, R.string.notfound);
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                builder.create().show();
            }
        });
    }

    private void alertUser(Context context, int message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(message);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create().show();
    }

    private void addFavourite(String stringJson)
    {
        try {
            City city = new City(stringJson);
            mCities.add(city); // todo : check if not already in list
            mAdapter.notifyDataSetChanged();
        } catch(Exception e)
        {
            alertUser(mContext, R.string.pb);
        }
    }
}
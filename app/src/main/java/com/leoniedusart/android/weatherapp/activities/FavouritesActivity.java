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

import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity {
    private Context mContext;
    private Toolbar toolbar;
    private CollapsingToolbarLayout toolBarLayout;
    private FloatingActionButton fab;
    private ArrayList<City> mCities;
    private RecyclerView mRecyclerViewFavourites;

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
        City city1 = new City("Montréal", "Légères pluies", "22°C", R.drawable.ic_rainy);
        City city2 = new City("New York", "Ensolleillé", "22°C", R.drawable.ic_sun);
        City city3 = new City("Paris", "Nuageux", "24°C", R.drawable.ic_cloudy);
        City city4 = new City("Toulouse", "Pluies modérées", "20°C", R.drawable.ic_rainy);

        mCities.add(city1);
        mCities.add(city2);
        mCities.add(city3);
        mCities.add(city4);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerViewFavourites.setLayoutManager(layoutManager);
        FavouriteAdapter mAdapter = new FavouriteAdapter(mContext, mCities);
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
                        mCities.add(new City(editTextAddFavourite.getText().toString(), "Nuageux", "18°C", R.drawable.ic_cloud));
                        mAdapter.notifyDataSetChanged();
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
}
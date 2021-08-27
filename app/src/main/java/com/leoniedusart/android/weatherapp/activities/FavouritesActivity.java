package com.leoniedusart.android.weatherapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.leoniedusart.android.weatherapp.R;
import com.leoniedusart.android.weatherapp.adapters.FavouriteAdapter;
import com.leoniedusart.android.weatherapp.database.DataBaseHelper;
import com.leoniedusart.android.weatherapp.models.City;
import com.leoniedusart.android.weatherapp.models.DbCity;
import com.leoniedusart.android.weatherapp.utils.CityAPI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FavouritesActivity extends AppCompatActivity implements CityAPI {
    private Context mContext;
    private DataBaseHelper mDbHelper;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mToolBarLayout;
    private FloatingActionButton mFab;
    private ArrayList<City> mCities;
    private City mCityRemoved;
    private RecyclerView mRecyclerViewFavourites;
    FavouriteAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        mContext = this;
        mDbHelper = new DataBaseHelper(mContext);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolBarLayout = findViewById(R.id.toolbar_layout);
        mToolBarLayout.setTitle(getTitle());

        // init liste
        mCities = new ArrayList<>();
        for(DbCity dbCity : mDbHelper.findAll())
        {
            City city = new City(dbCity.getmIdApi(), dbCity.getmName(), null, null, 0.0, 0.0, 0);
            mCities.add(city);
            apiCall(mContext, getUrl(mContext, dbCity.getmIdApi()), true);
        }

        // RecyclerView
        mRecyclerViewFavourites = findViewById(R.id.recycler_view_favourites_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerViewFavourites.setLayoutManager(layoutManager);
        mAdapter = new FavouriteAdapter(mContext, mCities);
        mRecyclerViewFavourites.setAdapter(mAdapter);

        // Swipe
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN|ItemTouchHelper.UP, ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT){

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = ((FavouriteAdapter.ViewHolder) viewHolder).getBindingAdapterPosition();
                int toPosition = ((FavouriteAdapter.ViewHolder) target).getBindingAdapterPosition();
                Collections.swap(mCities, fromPosition, toPosition);
                mDbHelper.swap(fromPosition, toPosition);
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                switch(direction) {
                    case ItemTouchHelper.LEFT:
                        int position = ((FavouriteAdapter.ViewHolder) viewHolder).getBindingAdapterPosition();
                        mCityRemoved = mCities.get(position);
                        mDbHelper.delete(mCityRemoved.getmApiID());
                        mCities.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        Snackbar.make(findViewById(R.id.coordinator_layout), String.format(getResources().getString(R.string.removed_city), mCityRemoved.getmName()), Snackbar.LENGTH_LONG)
                                .setAction(R.string.cancel, new View.OnClickListener(){
                                    @Override
                                    public void onClick(View view) {
                                        mDbHelper.insert(mCityRemoved, position);
                                        mCities.add(position, mCityRemoved);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                })
                                .show();
                        break;
                    case ItemTouchHelper.RIGHT:
                        mAdapter.notifyDataSetChanged();
                        Snackbar.make(findViewById(R.id.coordinator_layout), R.string.on_your_left, Snackbar.LENGTH_SHORT)
                                .show();
                        break;
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(mRecyclerViewFavourites);

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle(R.string.add_city);

                View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_add_favourite, null);
                final EditText editTextAddFavourite = (EditText) v.findViewById(R.id.edit_text_add_favourite);
                builder.setView(v);

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        apiCall(mContext, getUrl(mContext, editTextAddFavourite.getText().toString()), false);
                    }
                });

                builder.setNegativeButton(R.string.cancel, null);

                builder.create().show();
            }
        });
    }

    @Override
    public void onSuccess(String stringJson, boolean init) {
        try {
            City city = new City(stringJson);
            int cityOrder = mDbHelper.cityExists(city.getmApiID());
            if (!init && cityOrder >= 0) {
                alertUser(mContext, R.string.already_added);
            }
            else
            {
                if(!init)
                {
                    mDbHelper.insert(city, mCities.size());
                    mCities.add(city);
                }
                else
                {
                    try {
                        Log.d("LDtag", city.getmName() + " : " + cityOrder);
                        mCities.set(cityOrder, city);
                    } catch (IndexOutOfBoundsException e)
                    {
                        Log.d("LDtag", "ouch");
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        } catch(Exception e)
        {
            alertUser(mContext, R.string.pb);
        }
    }
}
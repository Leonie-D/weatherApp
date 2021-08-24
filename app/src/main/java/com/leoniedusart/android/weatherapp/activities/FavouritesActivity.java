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
import com.leoniedusart.android.weatherapp.models.City;
import com.leoniedusart.android.weatherapp.utils.CityAPI;
import java.util.ArrayList;
import java.util.Collections;

public class FavouritesActivity extends AppCompatActivity implements CityAPI {
    private Context mContext;
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

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolBarLayout = findViewById(R.id.toolbar_layout);
        mToolBarLayout.setTitle(getTitle());

        // RecyclerView
        mRecyclerViewFavourites = findViewById(R.id.recycler_view_favourites_list);
        mCities = new ArrayList<>();
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
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                switch(direction) {
                    case ItemTouchHelper.LEFT:
                        int position = ((FavouriteAdapter.ViewHolder) viewHolder).getBindingAdapterPosition();
                        mCityRemoved = mCities.get(position);
                        mCities.remove(position);
                        mAdapter.notifyDataSetChanged();
                        Snackbar.make(findViewById(R.id.coordinator_layout), String.format("%s a été supprimée.", mCityRemoved.getmName()), Snackbar.LENGTH_LONG)
                                .setAction(R.string.cancel, new View.OnClickListener(){
                                    @Override
                                    public void onClick(View view) {
                                        mCities.add(position, mCityRemoved);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                })
                                .show();
                        break;
                    case ItemTouchHelper.RIGHT:
                        mAdapter.notifyDataSetChanged();
                        Snackbar.make(findViewById(R.id.coordinator_layout), "L'autre gauche...", Snackbar.LENGTH_SHORT)
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

                builder.setTitle("Ajouter une ville");

                View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_add_favourite, null);
                final EditText editTextAddFavourite = (EditText) v.findViewById(R.id.edit_text_add_favourite);
                builder.setView(v);

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        apiCall(mContext, getUrl(editTextAddFavourite.getText().toString()));
                    }
                });

                builder.setNegativeButton(R.string.cancel, null);

                builder.create().show();
            }
        });
    }

    @Override
    public void onSuccess(String stringJson) {
        try {
            City city = new City(stringJson);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mCities.stream().anyMatch(c -> c.getmName().equals(city.getmName()))) {
                alertUser(mContext, R.string.already_added);
            }
            else
            {
                mCities.add(city);
                mAdapter.notifyDataSetChanged();
            }
        } catch(Exception e)
        {
            alertUser(mContext, R.string.pb);
        }
    }
}
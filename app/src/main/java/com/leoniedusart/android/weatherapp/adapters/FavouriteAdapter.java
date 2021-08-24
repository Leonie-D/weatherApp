package com.leoniedusart.android.weatherapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.leoniedusart.android.weatherapp.R;
import com.leoniedusart.android.weatherapp.models.City;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<City> mCities;

    // Constructor
    public FavouriteAdapter(Context context, ArrayList<City> cities) {
        this.mContext = context;
        this.mCities = cities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.favourite_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FavouriteAdapter.ViewHolder holder, int position) {
        City city = mCities.get(position);
        holder.mCityPosition = position;
        holder.mTextViewCityName.setText(city.getmName());
        holder.mTextViewCityDesc.setText(city.getmDesc());
        holder.mTextViewCityTemp.setText(city.getmTemp());
        Drawable icon = ResourcesCompat.getDrawable(mContext.getResources(), city.getmWeatherIcon(), mContext.getTheme());
        holder.mImageViewIcon.setImageDrawable(icon);
    }

    @Override
    public int getItemCount() {
        return mCities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView mTextViewCityName;
        public TextView mTextViewCityDesc;
        public TextView mTextViewCityTemp;
        public ImageView mImageViewIcon;
        public int mCityPosition;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextViewCityName = itemView.findViewById(R.id.text_view_city_name);
            mTextViewCityDesc = itemView.findViewById(R.id.text_view_city_desc);
            mTextViewCityTemp = itemView.findViewById(R.id.text_view_city_temp);
            mImageViewIcon = itemView.findViewById(R.id.image_view_icon);
            //itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setTitle(String.format("Supprimer %s ?", ((TextView) view.findViewById(R.id.text_view_city_name)).getText().toString()));

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mCities.remove(mCityPosition);
                    FavouriteAdapter.this.notifyDataSetChanged();
                }
            });

            builder.setNegativeButton(R.string.cancel, null);

            builder.create().show();
            return true;
        }
    }
}

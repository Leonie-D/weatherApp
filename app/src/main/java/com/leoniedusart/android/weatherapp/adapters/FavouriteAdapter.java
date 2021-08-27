package com.leoniedusart.android.weatherapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.leoniedusart.android.weatherapp.activities.MapsActivity;
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
        holder.mTextViewCityId.setText(String.valueOf(city.getmApiID()));
        holder.mTextViewCityLat.setText(String.valueOf(city.getmLat()));
        holder.mTextViewCityLon.setText(String.valueOf(city.getmLon()));
        holder.mTextViewCityName.setText(city.getmName());
        holder.mTextViewCityDesc.setText(city.getmDesc());
        holder.mTextViewCityTemp.setText(city.getmTemp());
        if(city.getmWeatherIcon() != 0)
        {
            Drawable icon = ResourcesCompat.getDrawable(mContext.getResources(), city.getmWeatherIcon(), mContext.getTheme());
            holder.mImageViewIcon.setImageDrawable(icon);
        }
    }

    @Override
    public int getItemCount() {
        return mCities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        public TextView mTextViewCityId;
        public TextView mTextViewCityLat;
        public TextView mTextViewCityLon;
        public TextView mTextViewCityName;
        public TextView mTextViewCityDesc;
        public TextView mTextViewCityTemp;
        public ImageView mImageViewIcon;
        public int mCityPosition;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextViewCityId = itemView.findViewById(R.id.text_view_api_id);
            mTextViewCityLat = itemView.findViewById(R.id.text_view_lat);
            mTextViewCityLon = itemView.findViewById(R.id.text_view_lon);
            mTextViewCityName = itemView.findViewById(R.id.text_view_city_name);
            mTextViewCityDesc = itemView.findViewById(R.id.text_view_city_desc);
            mTextViewCityTemp = itemView.findViewById(R.id.text_view_city_temp);
            mImageViewIcon = itemView.findViewById(R.id.image_view_icon);
            itemView.setOnClickListener(this);
            //itemView.setOnLongClickListener(this);
        }

        // méthode non utilisée donc non mise à jour (n'utilise pas la BdD)
        @Override
        public boolean onLongClick(View view) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setTitle(String.format(mContext.getResources().getString(R.string.remove_city_question), ((TextView) view.findViewById(R.id.text_view_city_name)).getText().toString()));

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mCities.remove(mCityPosition);
                    notifyDataSetChanged();
                }
            });

            builder.setNegativeButton(R.string.cancel, null);

            builder.create().show();
            return true;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, MapsActivity.class);
            intent.putExtra("cityName", ((TextView) view.findViewById(R.id.text_view_city_name)).getText().toString());
            intent.putExtra("cityLat", Double.valueOf(((TextView) view.findViewById(R.id.text_view_lat)).getText().toString()));
            intent.putExtra("cityLon", Double.valueOf(((TextView) view.findViewById(R.id.text_view_lon)).getText().toString()));
            mContext.startActivity(intent);
        }
    }
}

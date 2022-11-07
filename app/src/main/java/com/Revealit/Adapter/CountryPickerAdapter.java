package com.Revealit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.ModelClasses.CountryCodes;
import com.Revealit.R;
import com.Revealit.UserOnboardingProcess.NewAuthMobileAndPromoActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class CountryPickerAdapter extends RecyclerView.Adapter<CountryPickerAdapter.ViewHolder> {



    private View view;
    private Context mContext;
    private NewAuthMobileAndPromoActivity mActivity;
    private ViewHolder viewHolder;
    private List<CountryCodes.Data> itemListData;


    public CountryPickerAdapter(Context mContext, NewAuthMobileAndPromoActivity mActivity, List<CountryCodes.Data> itemListData) {
        this.mContext= mContext;
        this.mActivity= mActivity;
        this.itemListData =itemListData;


    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtItemName,txtCountryCode;
        private final ImageView imgItem;
        private final ProgressBar progressImgLoad;
        private final RelativeLayout relatativeMain;


        public ViewHolder(View mView) {

            super(mView);

            txtItemName = (TextView) mView.findViewById(R.id.txtItemName);
            txtCountryCode = (TextView) mView.findViewById(R.id.txtCountryCode);
            imgItem = (ImageView)mView.findViewById(R.id.imgItem);
            progressImgLoad =(ProgressBar)mView.findViewById(R.id.progressImgLoad);
            relatativeMain =(RelativeLayout)mView.findViewById(R.id.relatativeMain);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_items_country_picker, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

           //SET ITEM TEXT
        holder.txtItemName.setText(itemListData.get(position).getCountry_name());
        holder.txtCountryCode.setText(" + "+itemListData.get(position).getPhone_code());


            Glide.with(mContext)
                    .load("" + itemListData.get(position).getFlag_url())
                    .placeholder(R.drawable.placeholder)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.progressImgLoad.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            holder.progressImgLoad.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(holder.imgItem);

            holder.relatativeMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.selectedCountry(itemListData.get(position).getFlag_url(),itemListData.get(position).getPhone_code(),itemListData.get(position).getMobile_digits());
                }
            });


    }


    @Override
    public int getItemCount() {

        return itemListData.size();
    }
}
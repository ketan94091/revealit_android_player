package com.Revealit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.WebViewScreen;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.ModelClasses.DotsLocationsModel;
import com.Revealit.ModelClasses.GetProductDetailsModel;
import com.Revealit.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

public class ProductPurchaseVendorListAdapter extends RecyclerView.Adapter<ProductPurchaseVendorListAdapter.ViewHolder> {



    private View view;
    private Context mContext;
    private Activity mActivity;
    private ViewHolder viewHolder;
    private List<GetProductDetailsModel.DataOffers> offersArrayList = new ArrayList<>();


    public ProductPurchaseVendorListAdapter(Context mContext, Activity mActivity, List<GetProductDetailsModel.DataOffers> offersArrayList) {

        this.mContext= mContext;
        this.mActivity= mActivity;
        this.offersArrayList =offersArrayList;

        //REMOVE FIRST OBJECT
        this.offersArrayList.remove(0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtVendorName,txtDiscountByVendor,txtVendorURL;
        private final RelativeLayout relatativeMain;
        private final ImageView imgVendorLogo;
        private final LinearLayout linearShare,linarFavorite;


        public ViewHolder(View mView) {

            super(mView);

            txtVendorName = (TextView) mView.findViewById(R.id.txtVendorName);
            txtDiscountByVendor = (TextView) mView.findViewById(R.id.txtDiscountByVendor);
            txtVendorURL = (TextView) mView.findViewById(R.id.txtVendorURL);
            relatativeMain = (RelativeLayout) mView.findViewById(R.id.relatativeMain);
            imgVendorLogo = (ImageView)mView.findViewById(R.id.imgVendorLogo);
            linearShare = (LinearLayout)mView.findViewById(R.id.linearShare);
            linarFavorite = (LinearLayout)mView.findViewById(R.id.linarFavorite);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_vendor_list, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.txtVendorName.setText(offersArrayList.get(position).getVendorName());
        holder.txtDiscountByVendor.setText(offersArrayList.get(position).getOfferText());
        holder.txtVendorURL.setText(offersArrayList.get(position).getOfferUrl());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new RoundedCorners(25));

        Glide.with(mContext)
                .load(""+offersArrayList.get(position).getVendorLogoUrl())
                .placeholder(R.drawable.placeholder).apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(holder.imgVendorLogo);



        holder.relatativeMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(mActivity, WebViewScreen.class);
                mIntent.putExtra(Constants.RESEARCH_URL ,""+offersArrayList.get(position).getOfferUrl());
                mIntent.putExtra(Constants.RESEARCH_URL_SPONSER ,""+offersArrayList.get(position).getVendorName());
                mActivity.startActivity(mIntent);

            }
        });
        holder.linearShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonMethods.displayToast(mContext ,"SHARE CLICKED!");

            }
        });
        holder.linarFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonMethods.displayToast(mContext ,"FAVOURITE CLICKED!");

            }
        });





    }


    @Override
    public int getItemCount() {

        return offersArrayList.size();
    }
}
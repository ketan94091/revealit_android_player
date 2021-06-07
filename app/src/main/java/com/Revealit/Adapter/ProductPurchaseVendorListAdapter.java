package com.Revealit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.WebViewScreen;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.ModelClasses.DotsLocationsModel;
import com.Revealit.R;

import java.util.ArrayList;
import java.util.List;

public class ProductPurchaseVendorListAdapter extends RecyclerView.Adapter<ProductPurchaseVendorListAdapter.ViewHolder> {



    private View view;
    private Context mContext;
    private Activity mActivity;
    private ViewHolder viewHolder;
    List<DotsLocationsModel.BlueDotMetum> blueDotMeta = new ArrayList<>();
    String sponsorName = "";


    public ProductPurchaseVendorListAdapter(Context mContext, Activity mActivity, List<DotsLocationsModel.BlueDotMetum> blueDotMeta, String sponsorName) {

        this.mContext= mContext;
        this.mActivity= mActivity;
        this.blueDotMeta = blueDotMeta;
        this.sponsorName = sponsorName;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtTitle;
        private final RelativeLayout relatativeMain;
        private final ImageView imgType;


        public ViewHolder(View mView) {

            super(mView);

            txtTitle = (TextView) mView.findViewById(R.id.txtTitle);
            relatativeMain = (RelativeLayout) mView.findViewById(R.id.relatativeMain);
            imgType = (ImageView)mView.findViewById(R.id.imgType);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_blue_dots_meta_list, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.txtTitle.setText(blueDotMeta.get(position).getTitle());

        holder.relatativeMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(mActivity, WebViewScreen.class);
                mIntent.putExtra(Constants.RESEARCH_URL ,""+blueDotMeta.get(position).getUrl());
                mIntent.putExtra(Constants.RESEARCH_URL_SPONSER ,""+sponsorName);
                mActivity.startActivity(mIntent);

            }
        });



    }


    @Override
    public int getItemCount() {

        return blueDotMeta.size();
    }
}
package com.Revealit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.WebViewScreen;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.ModelClasses.GetRecipesDetails;
import com.Revealit.ModelClasses.InfluencersModel;
import com.Revealit.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

public class InfluencersListAdapter extends RecyclerView.Adapter<InfluencersListAdapter.ViewHolder> {



    private View view;
    private Context mContext;
    private Activity mActivity;
    private ViewHolder viewHolder;
    private List<InfluencersModel.Data> influencersDataList = new ArrayList<>();


    public InfluencersListAdapter(Context mContext, Activity mActivity, List<InfluencersModel.Data> influencersDataList) {

        this.mContext= mContext;
        this.mActivity= mActivity;
        this.influencersDataList =influencersDataList;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtChefName,txtHTMLInfluecersDetalis;
        private final ImageView imgSponsorLogo , imgChef;


        public ViewHolder(View mView) {

            super(mView);

            txtHTMLInfluecersDetalis = (TextView) mView.findViewById(R.id.txtHTMLInfluecersDetalis);
            txtChefName = (TextView) mView.findViewById(R.id.txtChefName);

            imgSponsorLogo = (ImageView)mView.findViewById(R.id.imgSponsorLogo);
            imgChef = (ImageView)mView.findViewById(R.id.imgChef);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_influencers_list, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.txtChefName.setText(influencersDataList.get(position).getName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.txtHTMLInfluecersDetalis.setText(Html.fromHtml(influencersDataList.get(position).getBio(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.txtHTMLInfluecersDetalis.setText(Html.fromHtml(influencersDataList.get(position).getBio()));
        }

        Glide.with(mContext)
                .load(""+influencersDataList.get(position).getInfluencer_image())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(holder.imgChef);


        Glide.with(mContext)
                .load(""+influencersDataList.get(position).getInfluencer_image())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(holder.imgSponsorLogo);


    }


    @Override
    public int getItemCount() {

        return influencersDataList.size();
    }
}
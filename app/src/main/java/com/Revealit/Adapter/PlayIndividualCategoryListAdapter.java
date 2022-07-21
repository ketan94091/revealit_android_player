package com.Revealit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.ExoPlayerActivity;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.ModelClasses.CategoryWisePlayListModel;
import com.Revealit.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public  class PlayIndividualCategoryListAdapter extends RecyclerView.Adapter<PlayIndividualCategoryListAdapter.ViewHolder> {


    private View view;
    private Context mContext;
    private Activity mActivity;
    private ViewHolder viewHolder;
    ArrayList<CategoryWisePlayListModel.DataBean> strCategoryList;
    private int positionOfVideoList = 0;


    public PlayIndividualCategoryListAdapter(Context mContext, Activity mActivity, ArrayList<CategoryWisePlayListModel.DataBean> strCategoryList, int positionOfVideoList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.strCategoryList = strCategoryList;
        this.positionOfVideoList =positionOfVideoList;

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgVideo;
        private final RelativeLayout relativeLayout;
        private final ProgressBar progressImgLoad;

        public ViewHolder(View mView) {

            super(mView);

            imgVideo = (ImageView) mView.findViewById(R.id.imgVideo);
            relativeLayout = (RelativeLayout) mView.findViewById(R.id.relativeLayout);
            progressImgLoad = (ProgressBar) mView.findViewById(R.id.progressImgLoad);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_category_item_list, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //SET TWO ITEMS IN SCREEN
        holder.relativeLayout.getLayoutParams().width = (int) ((getScreenWidth() - 30) / 2);


        //LOAD COVER IMAGE WITH GLIDE
        Glide.with(mActivity)
                .load("" + strCategoryList.get(position).getMediaCoverArt())
                .placeholder(R.drawable.placeholder)
                .apply(new RequestOptions().transform(new RoundedCorners(10)))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressImgLoad.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressImgLoad.setVisibility(View.GONE);
                        return false;
                    }
                }).into(holder.imgVideo);


        holder.imgVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent mIntent = new Intent(mActivity, VideoViewActivity.class);
                Intent mIntent = new Intent(mActivity, ExoPlayerActivity.class);
                mIntent.putExtra(Constants.MEDIA_URL, "" + strCategoryList.get(position).getMediaUrl());
                mIntent.putExtra(Constants.MEDIA_ID, "" + strCategoryList.get(position).getMediaID());
                mIntent.putExtra(Constants.VIDEO_NAME, "" + strCategoryList.get(position).getMediaShowTitle());
                mIntent.putExtra(Constants.VIDEO_SEEK_TO, "0");
                mIntent.putExtra(Constants.IS_VIDEO_SEEK, false);
                mActivity.startActivity(mIntent);

                //CommonMethods.displayToast(mContext, "CLICKED ON : "+position);
            }
        });

    }


    @Override
    public int getItemCount() {

        return strCategoryList.size();
    }

    public int getScreenWidth() {

        WindowManager wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }

}
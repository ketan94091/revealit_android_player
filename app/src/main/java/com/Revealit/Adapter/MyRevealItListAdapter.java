package com.Revealit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.VideoViewActivity;
import com.Revealit.CommonClasse.CommonMethods;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.ModelClasses.CategoryNamesListModel;
import com.Revealit.ModelClasses.CategoryWisePlayListModel;
import com.Revealit.R;
import com.Revealit.SqliteDatabase.DatabaseHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

public class MyRevealItListAdapter extends RecyclerView.Adapter<MyRevealItListAdapter.ViewHolder> {



    private View view;
    private Context mContext;
    private Activity mActivity;
    private ViewHolder viewHolder;
   // private  ArrayList<CategoryWisePlayListModel.DataBean> testList   = new ArrayList<>();
    private List<CategoryWisePlayListModel.DataBean> testList = new ArrayList<>();
    private ArrayList<Long> mLongRevealTime = new ArrayList<>();

    public MyRevealItListAdapter(Context mContext, Activity mActivity, List<CategoryWisePlayListModel.DataBean> testList, DatabaseHelper mDatabaseHelper, ArrayList<Long> mLongRevealTime) {

        this.mContext= mContext;
        this.mActivity= mActivity;
        this.testList= testList;
        this.mLongRevealTime= mLongRevealTime;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtVideoName,txtVideoNameSubTitle ,txtTime;
        private final ImageView imgCoverArt;
        private final ProgressBar progressImgLoad;
        private final RelativeLayout relatativeMain;


        public ViewHolder(View mView) {

            super(mView);

            imgCoverArt = (ImageView)mView.findViewById(R.id.imgCoverArt);

            txtVideoName = (TextView) mView.findViewById(R.id.txtVideoName);
            txtVideoNameSubTitle = (TextView) mView.findViewById(R.id.txtVideoNameSubTitle);
            txtTime = (TextView) mView.findViewById(R.id.txtTime);
            progressImgLoad = (ProgressBar)mView.findViewById(R.id.progressImgLoad);
            relatativeMain =(RelativeLayout)mView.findViewById(R.id.relatativeMain);



        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_my_revealit_list, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //LOAD COVER IMAGE WITH GLIDE
        Glide.with(mActivity)
                .load(""+testList.get(position).getMediaCoverArt())
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(10)))
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
                })
                .into(holder.imgCoverArt);

        holder.txtVideoName.setText(testList.get(position).getMediaTitle());
        holder.txtVideoNameSubTitle.setText(testList.get(position).getMediaShowTitle());

        /*String str = testList.get(position).getMediaTitle();
        str = str.replaceAll("u0027" , "'");
        //str = str.replace("'\'", "");
        //str = str.replaceAll("'\\\'","");
        str = str.replaceAll("\\\\", "");
        holder.txtVideoName.setText(str);

        String strSubTitle = testList.get(position).getMediaShowTitle();
        strSubTitle = strSubTitle.replaceAll("u0027" , "'");
        strSubTitle = strSubTitle.replaceAll("'\'","");
        holder.txtVideoNameSubTitle.setText(strSubTitle);*/
        holder.txtTime.setText(""+ CommonMethods.timeDifference(mLongRevealTime.get(position)));

        holder.relatativeMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(mActivity, VideoViewActivity.class);
                mIntent.putExtra(Constants.MEDIA_URL ,""+testList.get(position).getMediaUrl());
                mIntent.putExtra(Constants.MEDIA_ID ,""+testList.get(position).getMediaID());
                mIntent.putExtra(Constants.VIDEO_NAME ,""+testList.get(position).getMediaShowTitle());
                mActivity.startActivity(mIntent);
            }
        });



    }


    @Override
    public int getItemCount() {

        return testList.size();
    }
}
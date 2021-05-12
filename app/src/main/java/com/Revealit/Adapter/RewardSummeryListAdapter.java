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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.Activities.VideoViewActivity;
import com.Revealit.CommonClasse.Constants;
import com.Revealit.ModelClasses.CategoryWisePlayListModel;
import com.Revealit.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class RewardSummeryListAdapter extends RecyclerView.Adapter<RewardSummeryListAdapter.ViewHolder> {



    private View view;
    private Context mContext;
    private Activity mActivity;
    private ViewHolder viewHolder;
    private String[] strType = {"Video pause","Attention","Stop video","Click on items"};
    private String[] strAmount = {"0.1","0.3","0.2","0.4"};
    private String[] strWhen = {"1 hour ago","3 hour ago","1 day ago","1 week ago"};


    public RewardSummeryListAdapter(Context mContext, Activity mActivity) {
        this.mContext= mContext;
        this.mActivity= mActivity;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtType,txtWhen ,txtAmount;


        public ViewHolder(View mView) {

            super(mView);

            txtType = (TextView) mView.findViewById(R.id.txtType);
            txtAmount = (TextView) mView.findViewById(R.id.txtAmount);
            txtWhen = (TextView) mView.findViewById(R.id.txtWhen);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_reward_summery_list, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.txtType.setText(strType[position]);
        holder.txtAmount.setText(strAmount[position]);
        holder.txtWhen.setText(strWhen[position]);

    }


    @Override
    public int getItemCount() {

        return strType.length;
    }
}
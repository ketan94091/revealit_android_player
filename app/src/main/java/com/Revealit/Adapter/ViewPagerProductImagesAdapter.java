package com.Revealit.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.Revealit.ModelClasses.GetProductDetailsModel;
import com.Revealit.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerProductImagesAdapter extends PagerAdapter {

    private Context mContext;
    private List<GetProductDetailsModel.ImageData> mImagesList;

    public ViewPagerProductImagesAdapter(Context mContext, List<GetProductDetailsModel.ImageData> mImagesList) {
        this.mContext = mContext;
        this.mImagesList = mImagesList;
    }

    @Override
    public int getCount() {
        return mImagesList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_product_images, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.pagerItems);
        ProgressBar progressImageLoad = (ProgressBar) itemView.findViewById(R.id.progressImageLoad);
        Glide.with(mContext)
                .load(mImagesList.get(position).getUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressImageLoad.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        progressImageLoad.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
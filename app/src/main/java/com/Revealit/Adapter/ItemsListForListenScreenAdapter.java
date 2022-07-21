package com.Revealit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.Revealit.ModelClasses.ItemListFromItemIdModel;
import com.Revealit.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class ItemsListForListenScreenAdapter extends RecyclerView.Adapter<ItemsListForListenScreenAdapter.ViewHolder> {



    private View view;
    private Context mContext;
    private Activity mActivity;
    private ViewHolder viewHolder;
    private List<ItemListFromItemIdModel.Data> itemListData;


    public ItemsListForListenScreenAdapter(Context mContext, Activity mActivity, List<ItemListFromItemIdModel.Data> itemListData) {
        this.mContext= mContext;
        this.mActivity= mActivity;
        this.itemListData =itemListData;


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtItemName;
        private final ImageView imgItem;
        private final ProgressBar progressImgLoad;


        public ViewHolder(View mView) {

            super(mView);

            txtItemName = (TextView) mView.findViewById(R.id.txtItemName);
            imgItem = (ImageView)mView.findViewById(R.id.imgItem);
            progressImgLoad =(ProgressBar)mView.findViewById(R.id.progressImgLoad);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(mActivity).inflate(R.layout.raw_items_list_for_listenscreen, parent, false);

        view.setTag(viewHolder);
        viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

           //SET ITEM TEXT
        holder.txtItemName.setText(itemListData.get(position).getProduct_name());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new RoundedCorners(25));

            Glide.with(mContext)
                    .load("" + itemListData.get(position).getProduct_thumbnail())
                    .placeholder(R.drawable.placeholder)
.apply(requestOptions)
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


    }


    @Override
    public int getItemCount() {

        return itemListData.size();
    }
}